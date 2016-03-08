package cl.uc.fipezoa.pucassistant.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;

import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.fragments.buscacursos.FormFragment;
import cl.uc.fipezoa.pucassistant.fragments.buscacursos.ResultadosFragment;

public class BuscarCursoActivity extends FragmentActivity implements FormFragment.OnBuscarCursoListener {

    FragmentManager fragmentManager;
    ArrayList<RamoBuscaCursos> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscacursos_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BuscaCursos");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        setFragment(new FormFragment(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (results != null){
            if (results.size() > 0){
                ResultadosFragment fragment = new ResultadosFragment();
                fragment.setResultados(results);
                setFragment(fragment, true);
            }else{
                Snackbar.make(findViewById(R.id.fragment_container), "No se encontraron resultados.", Snackbar.LENGTH_SHORT).show();
            }
            results = null;
        }
    }

    @Override
    public void onBuscarCurso(FiltroBuscaCursos filtro) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Buscando cursos");

        BuscarCursoTask buscarCursoTask = new BuscarCursoTask(progressDialog, filtro, this);
        buscarCursoTask.execute();
    }

    class BuscarCursoTask extends AsyncTask<Void, Void, ArrayList<RamoBuscaCursos>>{

        BuscarCursoActivity activity;
        FiltroBuscaCursos filtro;
        ProgressDialog dialog;

        public BuscarCursoTask(ProgressDialog dialog, FiltroBuscaCursos filtro, BuscarCursoActivity activity){
            this.activity = activity;
            this.dialog = dialog;
            this.filtro = filtro;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
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
            dialog.dismiss();
            if (ramosBuscaCursos.size() > 0){
                activity.results = ramosBuscaCursos;
                try {
                    ResultadosFragment fragment = new ResultadosFragment();
                    fragment.setResultados(ramosBuscaCursos);
                    setFragment(fragment, true);
                    activity.results = null;
                }catch (IllegalStateException e){
                    // If app is on background
                }
            }else{
                Snackbar.make(activity.findViewById(R.id.fragment_container), "No se encontraron resultados.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
