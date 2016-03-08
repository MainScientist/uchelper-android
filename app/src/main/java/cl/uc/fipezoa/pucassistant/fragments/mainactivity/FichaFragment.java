package cl.uc.fipezoa.pucassistant.fragments.mainactivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import cl.uc.fipezoa.pucapi.fichaacademica.RamoCursado;
import cl.uc.fipezoa.pucapi.fichaacademica.RamoEnCurso;
import cl.uc.fipezoa.pucapi.fichaacademica.Semestre;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.services.UserReloadService;

public class FichaFragment extends Fragment {

    LinearLayout lLayout;
    BroadcastReceiver dataReloadedReceiver;

    public FichaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment_ficha, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Ficha Académica");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lLayout = (LinearLayout)view.findViewById(R.id.ficha_layout);
        loadFichaAcademica();
    }



    public void loadFichaAcademica(){
        lLayout.removeAllViews();
        if (DataManager.user.getFichaAcademica() != null) {
            for (Semestre semestre : DataManager.user.getFichaAcademica().getSemestres()) {
                addTitulo(semestre.getPeriodo());
                if (semestre.getRamosCursados().size() > 0) {
                    addSubtitulo("Ramos Cursados");
                    addHeadersCursados();
                    for (RamoCursado ramoCursado : semestre.getRamosCursados()) {
                        addRamoCursado(ramoCursado);
                    }
                    addPromedioPonderado(semestre);
                    addPromedioPonderadoAcumulado(semestre);
                }
                if (semestre.getRamosEnCurso().size() > 0){
                    addSubtitulo("Ramos en Curso");
                    addHeadersEnCurso();
                    for (RamoEnCurso ramoEnCurso : semestre.getRamosEnCurso()){
                        addRamoEnCurso(ramoEnCurso);
                    }
                }

                addHorizontalLine();
            }
        }else{
            Toast.makeText(getContext(), "Ficha academica cargada incorrectamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addTitulo(String periodo){
        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        titulo.setLayoutParams(tituloParams);
        titulo.setGravity(Gravity.CENTER_HORIZONTAL);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setText(periodo);
        titulo.setTextSize(getResources().getDimension(R.dimen.ficha_title_tsize));
        lLayout.addView(titulo);
    }

    public void addSubtitulo(String periodo){
        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        tituloParams.setMargins(0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin), 0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin));
        titulo.setLayoutParams(tituloParams);
        titulo.setGravity(Gravity.CENTER_HORIZONTAL);
        titulo.setText(periodo);
        titulo.setTextSize(getResources().getDimension(R.dimen.ficha_subtitle_tsize));
        lLayout.addView(titulo);
    }

    public void addPromedioPonderado(Semestre semestre){
        LinearLayout row = new LinearLayout(getContext());
        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin) / 2, 0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin) / 2);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 18);
        titulo.setLayoutParams(tituloParams);
        titulo.setTypeface(null, Typeface.ITALIC);
        titulo.setText("Promedio ponderado");

        TextView nota = new TextView(getContext());
        LinearLayout.LayoutParams notaParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        nota.setLayoutParams(notaParams);
        nota.setTypeface(null, Typeface.ITALIC);
        DecimalFormat dFormat = new DecimalFormat("##.##");
        nota.setText(dFormat.format(semestre.getPromedio()));

        row.addView(titulo);
        row.addView(nota);
        lLayout.addView(row);
    }

    public void addPromedioPonderadoAcumulado(Semestre semestre){
        LinearLayout row = new LinearLayout(getContext());
        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin)/2, 0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin)/2);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 18);
        titulo.setLayoutParams(tituloParams);
        titulo.setTypeface(null, Typeface.ITALIC);
        titulo.setText("Promedio ponderado acumulado");

        TextView nota = new TextView(getContext());
        LinearLayout.LayoutParams notaParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        nota.setLayoutParams(notaParams);
        nota.setTypeface(null, Typeface.ITALIC);
        DecimalFormat dFormat = new DecimalFormat("##.##");
        nota.setText(dFormat.format(semestre.getPromedioAcumulado()));

        row.addView(titulo);
        row.addView(nota);
        lLayout.addView(row);
    }

    public void addHeadersCursados(){
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        titulo.setLayoutParams(tituloParams);
        titulo.setTypeface(null, Typeface.ITALIC);
        titulo.setText("Sigla");

        TextView nombre = new TextView(getContext());
        LinearLayout.LayoutParams nombreParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10);
        nombre.setLayoutParams(nombreParams);
        nombre.setTypeface(null, Typeface.ITALIC);
        nombre.setText("Nombre");

        TextView creditos = new TextView(getContext());
        LinearLayout.LayoutParams credParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        creditos.setLayoutParams(credParams);
        creditos.setTypeface(null, Typeface.ITALIC);
        creditos.setText("Créditos");

        TextView nota = new TextView(getContext());
        LinearLayout.LayoutParams notaParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        nota.setLayoutParams(notaParams);
        nota.setTypeface(null, Typeface.ITALIC);
        nota.setText("Nota");

        row.addView(titulo);
        row.addView(nombre);
        row.addView(creditos);
        row.addView(nota);
        lLayout.addView(row);
    }

    public void addHeadersEnCurso(){

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        titulo.setLayoutParams(tituloParams);
        titulo.setTypeface(null, Typeface.ITALIC);
        titulo.setText("Sigla");

        TextView nombre = new TextView(getContext());
        LinearLayout.LayoutParams nombreParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10);
        nombre.setLayoutParams(nombreParams);
        nombre.setTypeface(null, Typeface.ITALIC);
        nombre.setText("Nombre");

        TextView creditos = new TextView(getContext());
        LinearLayout.LayoutParams credParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        creditos.setLayoutParams(credParams);
        creditos.setTypeface(null, Typeface.ITALIC);
        creditos.setText("Créditos");

        row.addView(titulo);
        row.addView(nombre);
        row.addView(creditos);
        lLayout.addView(row);

    }

    public void addRamoEnCurso(RamoEnCurso ramoEnCurso){
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        titulo.setLayoutParams(tituloParams);
        titulo.setText(ramoEnCurso.getSigla());

        TextView nombre = new TextView(getContext());
        LinearLayout.LayoutParams nombreParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10);
        nombre.setLayoutParams(nombreParams);
        nombre.setText(ramoEnCurso.getNombre());

        TextView creditos = new TextView(getContext());
        LinearLayout.LayoutParams credParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        creditos.setGravity(Gravity.CENTER_HORIZONTAL);
        creditos.setLayoutParams(credParams);
        creditos.setText(String.valueOf(ramoEnCurso.getCreditos()));

        row.addView(titulo);
        row.addView(nombre);
        row.addView(creditos);
        lLayout.addView(row);
    }

    public void addRamoCursado(RamoCursado ramoCursado){
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView titulo = new TextView(getContext());
        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        titulo.setLayoutParams(tituloParams);
        titulo.setText(ramoCursado.getSigla());

        TextView nombre = new TextView(getContext());
        LinearLayout.LayoutParams nombreParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10);
        nombre.setLayoutParams(nombreParams);
        nombre.setText(ramoCursado.getNombre());

        TextView creditos = new TextView(getContext());
        LinearLayout.LayoutParams credParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
        creditos.setGravity(Gravity.CENTER_HORIZONTAL);
        creditos.setLayoutParams(credParams);
        creditos.setText(String.valueOf(ramoCursado.getCreditos()));

        TextView nota = new TextView(getContext());
        LinearLayout.LayoutParams notaParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        nota.setGravity(Gravity.CENTER_HORIZONTAL);
        nota.setLayoutParams(notaParams);
        nota.setText(ramoCursado.getNota());

        row.addView(titulo);
        row.addView(nombre);
        row.addView(creditos);
        row.addView(nota);
        lLayout.addView(row);
    }

    public void addHorizontalLine(){
        View horizontalLine = new View(getContext());
        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)getResources().getDimension(R.dimen.altura_barra));
        horizontalLine.setLayoutParams(lineParams);
        horizontalLine.setBackgroundColor(Color.parseColor("#AAAAAA"));
        lLayout.addView(horizontalLine);
    }
}
