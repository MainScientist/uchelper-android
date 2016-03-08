package cl.uc.fipezoa.pucassistant.fragments.mainactivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RamoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RamoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RamoBuscaCursos ramo;
    TextView programa;

    public RamoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ramo RamoFragment.
     * @return A new instance of fragment RamoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RamoFragment newInstance(RamoBuscaCursos ramo) {
        RamoFragment fragment = new RamoFragment();
        fragment.setRamo(ramo);
        return fragment;
    }

    private void setRamo(RamoBuscaCursos ramo){
        this.ramo = ramo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment_ramo, container, false);
    }

    public void onViewCreated(View view, Bundle bundle){
        if (ramo != null) {
            HorarioView horarioView = (HorarioView)view.findViewById(R.id.resumen_ramo_horario);
            TextView sigla = (TextView)view.findViewById(R.id.resumen_ramo_sigla);
            TextView nombre = (TextView)view.findViewById(R.id.resumen_ramo_nombre);
            TextView nrc = (TextView)view.findViewById(R.id.resumen_ramo_nrc);
            TextView campus = (TextView)view.findViewById(R.id.resumen_ramo_campus);
            TextView prof = (TextView)view.findViewById(R.id.resumen_ramo_prof);
            TextView creditos = (TextView)view.findViewById(R.id.resumen_ramo_creditos);
            TextView salas = (TextView)view.findViewById(R.id.resumen_ramo_salas);
            TextView permiteRetiro = (TextView)view.findViewById(R.id.resumen_ramo_retiro);
            TextView ingles = (TextView)view.findViewById(R.id.resumen_ramo_ingles);
            TextView aprobEspecial = (TextView)view.findViewById(R.id.resumen_ramo_aprob_especial);
            programa = (TextView)view.findViewById(R.id.resumen_ramo_programa);
            TextView uAcademica = (TextView)view.findViewById(R.id.resumen_ramo_u_academica);

            sigla.setText(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
            nombre.setText(ramo.getNombre());
            nrc.setText(String.valueOf(ramo.getNrc()));
            campus.setText(ramo.getCampus());

            String profesores = "";
            for (String profName : ramo.getProfesores()){
                profesores = profName + "\n";
            }

            prof.setText(profesores.trim());
            creditos.setText(String.valueOf(ramo.getCreditos()));

            Map<String, String> salasMap = new HashMap<>();
            for (Modulo modulo : ramo.getModulos()){
                horarioView.addModulo(modulo);
                if (!salasMap.containsKey(modulo.getTipo())){
                    salasMap.put(modulo.getTipo(), modulo.getSala());
                }
            }
            String salasText = "";
            for (String tipo : salasMap.keySet()){
                salasText += tipo + ": " + salasMap.get(tipo) + "\n";
            }

            salas.setText(salasText.trim());
            uAcademica.setText(ramo.getUnidadAcademica());
            programa.setText(ramo.getPrograma());
            permiteRetiro.setText(ramo.permiteRetiro() ? "Si" : "No");
            ingles.setText(ramo.dictadoEnIngles() ? "Si" : "No");
            aprobEspecial.setText(ramo.requiereAprobEspecial() ? "Si" : "No");
            if (!ramo.programaCargado()){
                new CargarProgramaTask().execute();
            }
        }
    }

    class CargarProgramaTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ramo.cargarPrograma();
                DataManager.saveUser(getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ramo.programaCargado()) {
                programa.setText(ramo.getPrograma());
            }else{
                Toast.makeText(getContext(), "Error cargando el programa", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
