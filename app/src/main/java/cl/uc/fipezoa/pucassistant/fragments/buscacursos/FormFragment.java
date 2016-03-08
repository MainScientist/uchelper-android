package cl.uc.fipezoa.pucassistant.fragments.buscacursos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioCheckBox;

/**
 * A placeholder fragment containing a simple view.
 */
public class FormFragment extends Fragment {

    OnBuscarCursoListener mCallback;
    Spinner periodoSpinner;

    public interface OnBuscarCursoListener{
        void onBuscarCurso(FiltroBuscaCursos filtro);
    }

    public FormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buscacursos_fragment_form, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!FiltroBuscaCursos.dataLoaded) {
            LoadFormDataTask loadTask = new LoadFormDataTask(view, getContext());
            Log.d("LOADING DATA", "LOADING DATA");
            loadTask.execute();
        }else{
            setAdapter((Spinner)view.findViewById(R.id.buscarcurso_periodo), FiltroBuscaCursos.periodosDisponibles);
            setAdapter((Spinner)view.findViewById(R.id.buscarcurso_campus), FiltroBuscaCursos.campusDisponibles);
            setAdapter((Spinner)view.findViewById(R.id.buscarcurso_escuela), FiltroBuscaCursos.unidadesAcademicasDisponibles);
            setAdapter((Spinner)view.findViewById(R.id.buscarcurso_horario), FiltroBuscaCursos.tiposHorarioDisponibles);
        }

        final EditText sigla = (EditText)view.findViewById(R.id.buscarcurso_sigla);
        final EditText nombre = (EditText)view.findViewById(R.id.buscarcurso_nombre);
        final EditText profesor = (EditText)view.findViewById(R.id.buscarcurso_profesor);

        periodoSpinner = (Spinner)view.findViewById(R.id.buscarcurso_periodo);
        final Spinner campus = (Spinner)view.findViewById(R.id.buscarcurso_campus);
        final Spinner unidadAcademica = (Spinner)view.findViewById(R.id.buscarcurso_escuela);
        final Spinner tipoHorario = (Spinner)view.findViewById(R.id.buscarcurso_horario);

        final HorarioCheckBox horarioCheckBox = (HorarioCheckBox)view.findViewById(R.id.buscarcurso_horario_checkbox);

        Button buscar = (Button)view.findViewById(R.id.buscarcurso_buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltroBuscaCursos filtroBuscaCursos = new FiltroBuscaCursos(periodoSpinner.getSelectedItem().toString());
                if (sigla.getText().length() > 0){
                    filtroBuscaCursos.setSigla(sigla.getText().toString());
                }

                if (nombre.getText().length() > 0){
                    filtroBuscaCursos.setNombreCurso(nombre.getText().toString());
                }

                if (profesor.getText().length() > 0){
                    filtroBuscaCursos.setProfesor(profesor.getText().toString());
                }

                filtroBuscaCursos.setCampus(campus.getSelectedItem().toString());
                filtroBuscaCursos.setUnidadAcademica(unidadAcademica.getSelectedItem().toString());
                filtroBuscaCursos.setTipoHorario(tipoHorario.getSelectedItem().toString());

                String[] checked = horarioCheckBox.getChecked().toArray(new String[horarioCheckBox.getChecked().size()]);
                filtroBuscaCursos.setHorario(checked);

                mCallback.onBuscarCurso(filtroBuscaCursos);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent != null){
            try {
                mCallback = (OnBuscarCursoListener) parent;
            } catch (ClassCastException e) {
                throw new ClassCastException(parent.toString()
                        + " must implement OnBuscarCursoListener");
            }
        }else {

            try {
                mCallback = (OnBuscarCursoListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnBuscarCursoListener");
            }
        }
    }

    public void setPeriodo(String periodo){
        periodoSpinner.setSelection(FiltroBuscaCursos.periodosDisponibles.indexOf(periodo));
        periodoSpinner.setEnabled(false);
    }

    public void setAdapter(Spinner spinner, ArrayList<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String string : strings){
            adapter.add(string);
        }
        spinner.setAdapter(adapter);
    }

    class LoadFormDataTask extends AsyncTask<Void, Void, Boolean>{

        View view;
        Context context;
        ProgressDialog dialog;

        public LoadFormDataTask(View v, Context context){
            view = v;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("AsyncTask", "PREEXECUTE");
            dialog = new ProgressDialog(context);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Loading form");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return FiltroBuscaCursos.loadFormData();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            dialog.dismiss();
            if (success){
                setAdapter((Spinner)view.findViewById(R.id.buscarcurso_periodo), FiltroBuscaCursos.periodosDisponibles);
                setAdapter((Spinner)view.findViewById(R.id.buscarcurso_campus), FiltroBuscaCursos.campusDisponibles);
                setAdapter((Spinner)view.findViewById(R.id.buscarcurso_escuela), FiltroBuscaCursos.unidadesAcademicasDisponibles);
                setAdapter((Spinner)view.findViewById(R.id.buscarcurso_horario), FiltroBuscaCursos.tiposHorarioDisponibles);
            }else{
                Toast.makeText(context, "Error cargando la informacion", Toast.LENGTH_LONG).show();
                ((Activity)context).finish();
            }
        }
    }
}
