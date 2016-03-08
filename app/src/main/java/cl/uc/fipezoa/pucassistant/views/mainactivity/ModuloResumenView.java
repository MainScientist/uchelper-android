package cl.uc.fipezoa.pucassistant.views.mainactivity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import cl.uc.fipezoa.pucapi.Ramo;
import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Ramos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.BuscaCursosFragment;

/**
 * Created by fipezoa on 1/31/2016.
 */
public class ModuloResumenView extends LinearLayout {

    static private String[] horas = new String[]{"", "8:30-9.50", "10:00-11:20", "11:30-12:50",
                                                 "14:00-15:20", "15:30-16:50", "17:00-18:20",
                                                 "18:30-19:50", "20:00-21:20"};

    ScrollView parent;
    LinearLayout otherSectionsContainer;


    public ModuloResumenView(Context context) {
        super(context);
        setupUi();
    }

    public ModuloResumenView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        setupUi();
    }

    public void setupUi(){
        setOrientation(VERTICAL);
    }

    public void addModulo(final Modulo modulo){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_view_resumen_modulo, null);
        TextView sigla = (TextView)view.findViewById(R.id.resumen_modulo_sigla);
        TextView nombre = (TextView)view.findViewById(R.id.resumen_modulo_nombre);
        TextView nrc = (TextView)view.findViewById(R.id.resumen_modulo_nrc);
        TextView hora = (TextView)view.findViewById(R.id.resumen_modulo_hora);
        TextView sala = (TextView)view.findViewById(R.id.resumen_modulo_sala);
        TextView campus = (TextView)view.findViewById(R.id.resumen_modulo_campus);
        TextView tipo = (TextView)view.findViewById(R.id.resumen_modulo_tipo);
        TextView prof = (TextView)view.findViewById(R.id.resumen_modulo_prof);

        RamoBuscaCursos ramo = modulo.getRamo();
        sigla.setText(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
        nombre.setText(ramo.getNombre());
        nrc.setText(String.valueOf(ramo.getNrc()));
        hora.setText(horas[modulo.getNumero()]);
        sala.setText(modulo.getSala());
        campus.setText(ramo.getCampus());
        tipo.setText(modulo.getTipo());
        String profesores = "";
        for (String profName : ramo.getProfesores()){
            profesores = profName + "\n";
        }
        prof.setText(profesores.trim());

        LinearLayout.LayoutParams lParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lParams.setMargins(
                (int)getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int)getResources().getDimension(R.dimen.activity_vertical_margin),
                (int)getResources().getDimension(R.dimen.activity_horizontal_margin),
                0);
        view.setLayoutParams(lParams);
        switch (modulo.getTipo()){
            case "CAT":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_catedra_background));
                break;
            case "LAB":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_lab_background));
                break;
            case "AYUD":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_ayud_background));
                break;
            case "PRAC":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_practica_background));
                break;
            case "TALL":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_taller_background));
                break;
            case "TERR":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_terr_background));
                break;
            case "TES":
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_tesis_background));
                break;
            default:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hv_otro_background));
                break;
        }

        addView(view);

    }


    private void requestDisallowParentInterceptTouchEvent(View v, Boolean disallowIntercept) {
        if (parent != null){
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }else {
            while (v.getParent() != null && v.getParent() instanceof View) {
                if (v.getParent() instanceof ScrollView) {
                    parent = (ScrollView)v.getParent();
                    parent.requestDisallowInterceptTouchEvent(disallowIntercept);
                }
                v = (View) v.getParent();
            }
        }
    }
}
