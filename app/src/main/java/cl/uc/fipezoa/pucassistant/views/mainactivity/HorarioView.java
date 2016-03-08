package cl.uc.fipezoa.pucassistant.views.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
public class HorarioView extends LinearLayout {

    Map<String, ModuloView> views;
    static String[] days = new String[]{"", "L", "M", "W", "J", "V", "S"};
    private ModuloView.OnModuloFocusListener fragmentParent;

    public HorarioView(Context context) {
        super(context);
        setupUi();
    }

    public HorarioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUi();
    }

    public void setupUi(){
        views = new HashMap<>();
        setOrientation(VERTICAL);

        // setFocusable(true);
        for (int modulo = 0; modulo < 9; modulo++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(HORIZONTAL);
            row.setMinimumHeight((int) getResources().getDimension(R.dimen.min_row_height));
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);
            row.setFocusableInTouchMode(true);
            for (String day : days){
                if (day.equals("")){
                    if (modulo == 0){
                        View view = new View(getContext());
                        LinearLayout.LayoutParams lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                        lParams.weight = 1;
                        lParams.gravity = Gravity.CENTER;
                        view.setLayoutParams(lParams);
                        row.addView(view);
                    }else{
                        TextView dayView = new TextView(getContext());
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                        lParams.gravity = Gravity.CENTER_VERTICAL;
                        lParams.weight = 1;
                        dayView.setGravity(Gravity.CENTER);
                        dayView.setLayoutParams(lParams);
                        dayView.setText(String.valueOf(modulo));
                        dayView.setTextSize(getResources().getDimension(R.dimen.label_text_size));
                        row.addView(dayView);
                    }
                }else{
                    if (modulo == 0){
                        TextView dayView = new TextView(getContext());
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                        lParams.weight = 4;
                        lParams.gravity = Gravity.CENTER_VERTICAL;
                        lParams.setMargins(2, 0, 2, 0);
                        dayView.setGravity(Gravity.CENTER);
                        dayView.setLayoutParams(lParams);
                        dayView.setText(day);
                        dayView.setTextSize(getResources().getDimension(R.dimen.label_text_size));
                        row.addView(dayView);
                    }else{
                        String key = day + String.valueOf(modulo);
                        ModuloView moduloView = new ModuloView(getContext(), fragmentParent);
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                        lParams.gravity = Gravity.CENTER_VERTICAL;
                        lParams.weight = 4;
                        lParams.setMargins(2, 0, 2, 0);
                        moduloView.setLayoutParams(lParams);
                        moduloView.setGravity(Gravity.CENTER_VERTICAL);
                        views.put(key, moduloView);
                        row.addView(moduloView);
                    }
                }
            }
            addView(row);
            View hLine = new View(getContext());
            LayoutParams lineParams;
            if (modulo == 3){
                lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.altura_barra_break));
            }else {
                lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.altura_barra));
            }
            hLine.setLayoutParams(lineParams);
            hLine.setBackgroundColor(Color.parseColor("#AAAAAA"));
            addView(hLine);
        }
    }

    public void setParent(ModuloView.OnModuloFocusListener fragmentParent){
        this.fragmentParent = fragmentParent;
        for (ModuloView moduloView : views.values()){
            moduloView.setOnModuleFocusListener(fragmentParent);
        }
    }

    public void setFocusableInTouchMode(boolean focusable){
        super.setFocusableInTouchMode(focusable);
        for (ModuloView moduloView : views.values()){
            if (moduloView.modulosSize() > 0){
                moduloView.setFocusableInTouchMode(focusable);
            }
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        for (ModuloView moduloView : views.values()){
            if (moduloView.modulosSize() > 0){
                moduloView.setOnClickListener(l);
            }
        }
    }

    public void addModulo(Modulo modulo){
        String key = modulo.getDia() + String.valueOf(modulo.getNumero());
        ModuloView moduloView = views.get(key);
        if (moduloView.getModulos().size() == 0){
            moduloView.setFocusableInTouchMode(isFocusableInTouchMode());
        }
        moduloView.addModulo(modulo);
        if (moduloView.modulosSize() > 1){
            moduloView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        }
    }

    public void addModulo(Modulo modulo, int alpha){
        String key = modulo.getDia() + String.valueOf(modulo.getNumero());
        ModuloView moduloView = views.get(key);
        if (moduloView.getModulos().size() == 0){
            moduloView.setFocusableInTouchMode(isFocusableInTouchMode());
        }
        moduloView.addModulo(modulo, alpha);
        if (moduloView.modulosSize() > 1){
            moduloView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        }
    }

    public void changeAlpha(Modulo modulo, int alpha){
        String key = modulo.getDia() + String.valueOf(modulo.getNumero());
        ModuloView moduloView = views.get(key);
        moduloView.changeAlpha(modulo, alpha);
    }

    public void removeModulo(Modulo modulo){
        String key = modulo.getDia() + String.valueOf(modulo.getNumero());
        ModuloView moduloView = views.get(key);
        moduloView.removeModulo(modulo);
        if (moduloView.modulosSize() > 1){
            moduloView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        }
        if (moduloView.getModulos().size() == 0){
            moduloView.setFocusableInTouchMode(false);
        }
    }

    public void clearModulos(){
        for (ModuloView view : views.values()){
            for (Modulo modulo : (ArrayList<Modulo>)view.getModulos().clone()){
                view.getModulos().remove(modulo);
            }
        }
    }
}
