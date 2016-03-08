package cl.uc.fipezoa.pucassistant.fragments.horarios;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.classes.MiHorario;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.fragments.buscacursos.FormFragment;
import cl.uc.fipezoa.pucassistant.views.buscacursos.RamoView;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioView;

/**
 * Created by fipezoa on 2/18/2016.
 */
public class AddRamoFragment extends Fragment implements FormFragment.OnBuscarCursoListener {

    LinearLayout content;
    LinearLayout resultsContainer;
    ScrollView scrollView;
    ArrayList<RamoBuscaCursos> results;
    HorarioView horarioView;
    MiHorario miHorario;
    FormFragment formFragment;
    LinearLayout ramosHorarioContainer;
    Map<RamoBuscaCursos, RamoView> resultViews = new HashMap<>();

    public AddRamoFragment(){
    }

    public void setMiHorario(MiHorario miHorario){
        this.miHorario = miHorario;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.horarios_fragment_add_ramo, container, false);
    }

    public void updateHorario(){
        clearAllFocuses();
        horarioView.clearModulos();

        for (RamoBuscaCursos ramo : miHorario.getRamos()){
            Log.d("HorarioView", ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
            for (Modulo modulo : ramo.getModulos()){
                horarioView.addModulo(modulo);
            }
        }
        updateRamosHorario();
    }

    public void updateRamosHorario(){
        ramosHorarioContainer.removeAllViews();
        for (final RamoBuscaCursos ramo : miHorario.getRamos()){
            final RamoView ramoView = new RamoView(getContext(), ramo);
            ramoView.getButton().setText("x");
            ramoView.getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Borrar ramo", Toast.LENGTH_SHORT).show();
                    miHorario.getRamos().remove(ramo);
                    DataManager.saveHorarios(getContext());
                    for (Modulo modulo : ramo.getModulos()) {
                        horarioView.removeModulo(modulo);
                    }
                    if (resultViews.containsKey(ramo)){
                        resultViews.get(ramo).getButton().setVisibility(View.VISIBLE);
                    }
                    updateRamosHorario();
                }
            });
            ramosHorarioContainer.addView(ramoView);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = (LinearLayout)view;
        scrollView = (ScrollView)content.findViewById(R.id.horarios_addramo_scrollview);
        resultsContainer = (LinearLayout)view.findViewById(R.id.horarios_addramo_results);
        horarioView = (HorarioView)content.findViewById(R.id.horarios_addramo_hv);
        ramosHorarioContainer = (LinearLayout)content.findViewById(R.id.horarios_addramo_ramoshorario);
        formFragment = (FormFragment)getChildFragmentManager().findFragmentById(R.id.horarios_addramo_form);

        formFragment.setPeriodo(miHorario.getPeriodo());
//        scrollView.setFocusableInTouchMode(true);
//        content.setFocusableInTouchMode(true);
//        resultsContainer.setFocusableInTouchMode(true);
        if (results != null){
            showResults(results);
            results = null;
        }
        updateHorario();
    }

    @Override
    public void onBuscarCurso(FiltroBuscaCursos filtro) {
        BuscarCursoTask task = new BuscarCursoTask(filtro);
        task.execute();
    }

    public void clearAllFocuses(){
        for (int i = 0; i < resultsContainer.getChildCount(); i++){
            View v = resultsContainer.getChildAt(i);
            if (v.hasFocus()){
                v.clearFocus();
            }
        }
    }

    public void showResults(ArrayList<RamoBuscaCursos> results){
        clearAllFocuses();
        resultsContainer.removeAllViews();
        resultViews.clear();
        for (final RamoBuscaCursos ramo : results){
            final RamoView ramoView = new RamoView(getContext(), ramo);
            ramoView.setFocusableInTouchMode(true);
            ramoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.hasFocus()) {
                        v.clearFocus();
                    }
                }
            });
            ramoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!miHorario.getRamos().contains(ramo)) {
                        if (!hasFocus) {
                            for (Modulo modulo : ramo.getModulos()) {
                                horarioView.removeModulo(modulo);
                            }
                        } else {
                            for (Modulo modulo : ramo.getModulos()) {
                                horarioView.addModulo(modulo);
                            }
                        }
                    }
                }
            });
            ramoView.getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    miHorario.getRamos().add(ramo);
                    DataManager.saveHorarios(getContext());
                    updateHorario();
                    v.setVisibility(View.GONE);
                }
            });
            if (miHorario.getRamos().contains(ramo)){
                ramoView.getButton().setVisibility(View.GONE);
            }
            resultViews.put(ramo, ramoView);
            resultsContainer.addView(ramoView);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, resultsContainer.getTop());
                }
            });
        }

    }


    class BuscarCursoTask extends AsyncTask<Void, Void, ArrayList<RamoBuscaCursos>> {

        FiltroBuscaCursos filtro;

        public BuscarCursoTask(FiltroBuscaCursos filtro){
            this.filtro = filtro;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final ProgressBar progressBar = (ProgressBar)content.findViewById(R.id.horarios_addramo_progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, progressBar.getTop());
                }
            });
        }

        @Override
        protected ArrayList<RamoBuscaCursos> doInBackground(Void... params) {
            try {
                return BuscaCursos.buscarCursos(filtro, true);
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<RamoBuscaCursos> ramosBuscaCursos) {
            super.onPostExecute(ramosBuscaCursos);
            content.findViewById(R.id.horarios_addramo_progress_bar).setVisibility(View.GONE);

            if (ramosBuscaCursos.size() > 0){
                try {
                    showResults(ramosBuscaCursos);
                }catch (IllegalStateException | NullPointerException e){
                    results = ramosBuscaCursos;
                }
            }else{
                Snackbar.make(getActivity().findViewById(R.id.fragment_container), "No se encontraron resultados.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
