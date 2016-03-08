package cl.uc.fipezoa.pucassistant.views.mainactivity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 1/30/2016.
 */
public class ModuloView extends LinearLayout {

    private ArrayList<Modulo> modulos = new ArrayList<>();
    private Map<Modulo, Integer> alphas = new HashMap<>();
    private OnModuloFocusListener listener;

    public ModuloView(Context context, ArrayList<Modulo> modulos){
        this(context);
        this.modulos = modulos;
        for (Modulo modulo : modulos){
            alphas.put(modulo, 255);
        }
        refresh();
    }

    public ModuloView(Context context) {
        super(context);
        setupView();
    }

    public ModuloView(Context context, OnModuloFocusListener listener) {
        this(context);
        this.listener = listener;
    }

    public ModuloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();

    }

    public ArrayList<Modulo> getModulos(){
        return modulos;
    }

    private void setupView(){
//        LinearLayout.LayoutParams layoutParams = new LayoutParams((int)getResources().getDimension(R.dimen.ancho_casilla_horario), ViewGroup.LayoutParams.WRAP_CONTENT);
//        setOrientation(HORIZONTAL);
//        setLayoutParams(layoutParams);
        setOrientation(VERTICAL);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_modulo_states));
        // setFocusableInTouchMode(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modulosSize() > 0) {
                    if (hasFocus()) {
                        clearFocus();
                    }
                }
            }
        });

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (listener != null) {
                    if (hasFocus) {
                        listener.onModuleFocusChange(ModuloView.this);
                    } else {
                        listener.onModuleFocusChange(null);
                    }
                }
            }
        });
        refresh();
    }

    public void changeAlpha(Modulo modulo, int alpha){
        alphas.put(modulo, alpha);
        refresh();
    }

    public void refresh(){
        removeAllViews();
        for (Modulo modulo : modulos){
            RamoBuscaCursos ramo = modulo.getRamo();
            TextView nombreRamo = new TextView(getContext());
            LayoutParams layoutParams = new LayoutParams((int)getResources().getDimension(R.dimen.ancho_casilla_horario),
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 2, 0, 2);
            nombreRamo.setLayoutParams(layoutParams);
            nombreRamo.setText(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
            switch (modulo.getTipo()){
                case "CAT":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_catedra_background));
                    break;
                case "LAB":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_lab_background));
                    break;
                case "AYUD":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_ayud_background));
                    break;
                case "PRAC":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_practica_background));
                    break;
                case "TALL":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_taller_background));
                    break;
                case "TERR":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_terr_background));
                    break;
                case "TES":
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_tesis_background));
                    break;
                default:
                    nombreRamo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_otro_background));
                    break;
            }
            nombreRamo.getBackground().setAlpha(alphas.get(modulo));
            nombreRamo.setTextSize(getResources().getDimension(R.dimen.horario_text_size));
            nombreRamo.setGravity(Gravity.CENTER);

            setGravity(Gravity.CENTER);
            addView(nombreRamo);
        }
    }

    public void setOnModuleFocusListener(OnModuloFocusListener parentFragment){
        this.listener = parentFragment;
    }

    public int modulosSize(){
        return modulos.size();
    }

    public void addModulo(Modulo modulo, int alpha){
        modulos.add(modulo);
        alphas.put(modulo, alpha);
        refresh();
    }

    public void addModulo(Modulo modulo){
        modulos.add(modulo);
        alphas.put(modulo, 255);
        refresh();
    }

    public void removeModulo(Modulo modulo){
        modulos.remove(modulo);
        alphas.remove(modulo);
        refresh();
    }

    public interface OnModuloFocusListener {
        void onModuleFocusChange(ModuloView view);
    }
}
