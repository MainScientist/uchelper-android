package cl.uc.fipezoa.pucassistant.fragments.mainactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.helper.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.HorarioString;
import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoAlumno;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioCheckBox;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioView;
import cl.uc.fipezoa.pucassistant.views.mainactivity.ModuloResumenView;
import cl.uc.fipezoa.pucassistant.views.mainactivity.ModuloView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorarioFragment extends Fragment implements ModuloView.OnModuloFocusListener {

    private ModuloResumenView resumenView;
    ScrollView scrollView;
    HorarioView horarioView;

    public HorarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Horario");
        }
    }

    public void onViewCreated(View view, Bundle bundle){
        scrollView = (ScrollView) view.findViewById(R.id.main_fragment_horario_scrollview);
        horarioView = (HorarioView) view.findViewById(R.id.horario_view);
        horarioView.setParent(this);
        for (RamoBuscaCursos ramo : DataManager.user.getRamosEnCurso()){
            for (Modulo modulo : ramo.getModulos()){
                horarioView.addModulo(modulo);
            }
        }
        horarioView.setFocusableInTouchMode(true);
        LinearLayout ramosContainer = (LinearLayout) view.findViewById(R.id.main_fragment_horario_ramos);
        for (final RamoAlumno ramo : DataManager.user.getRamosEnCurso()){
            LinearLayout layout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.main_horario_ramoview, null);
            TextView nombre = (TextView)layout.findViewById(R.id.main_horario_ramoview_nombre);
            TextView sigla = (TextView)layout.findViewById(R.id.main_horario_ramoview_sigla);
            TextView prof = (TextView)layout.findViewById(R.id.main_horario_ramoview_prof);
            LinearLayout horario = (LinearLayout)layout.findViewById(R.id.main_horario_ramoview_horarios);
            Button button = (Button)layout.findViewById(R.id.main_horario_ramoview_btn);
            final LinearLayout seccionContainer = (LinearLayout)layout.findViewById(R.id.main_horario_ramoview_secciones);
            if (ramo.tieneUnaSeccion()){
                button.setText("Este ramo solo tiene una sección");
                button.setEnabled(false);
            }else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBuscarSecciones(ramo, seccionContainer);
                    }
                });
            }

            nombre.setText(ramo.getNombre());
            sigla.setText(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
            String profesores = "";
            for (String profName : ramo.getProfesores()){
                profesores = profName + "\n";
            }
            prof.setText(profesores.trim());
            for (HorarioString horarioString: ramo.getHorarioStrings()){
                LinearLayout linearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.buscacursos_view_ramo_horario, null);
                ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_dias)).setText(horarioString.dias);
                ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_sala)).setText(horarioString.sala);
                ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_tipo)).setText(horarioString.tipo);
                horario.addView(linearLayout);
            }
            ramosContainer.addView(layout);
        }
        resumenView = (ModuloResumenView) view.findViewById(R.id.resumen_view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment_horario, container, false);
    }

    @Override
    public void onModuleFocusChange(ModuloView moduloView) {
        resumenView.removeAllViews();
        if (moduloView != null){
            for (Modulo modulo : moduloView.getModulos()){
                resumenView.addModulo(modulo);
            }
        }
        scrollView.smoothScrollTo(0, resumenView.getTop());
    }

    public void onBuscarSecciones(final RamoBuscaCursos ramo, final LinearLayout container){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final Map<String, Integer> map = new HashMap<>();
        final Map<String, ArrayList<String>> checked = new HashMap<>();
        builder.setView(getBuscarSeccionesDialog(ramo, map, checked));
        builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BuscarSeccionesTask task = new BuscarSeccionesTask(ramo, container, map, checked);
                task.execute();
            }
        });
        builder.create().show();

        // new BuscarSeccionesTask(ramo, container).execute();
    }

    public View getBuscarSeccionesDialog(RamoBuscaCursos ramo, final Map<String, Integer> map, Map<String, ArrayList<String>> checked){
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        ArrayList<String> tipos = new ArrayList<>();
        for (HorarioString horarioString : ramo.getHorarioStrings()){
            if (!tipos.contains(horarioString.tipo)){
                tipos.add(horarioString.tipo);
            }
        }
        for (final String sTipo : tipos){
            View view = getActivity().getLayoutInflater().inflate(R.layout.main_horario_dialog_otraseccion, null);
            TextView tipo = (TextView)view.findViewById(R.id.main_horario_dialog_otraseccion_tipo);
            tipo.setText(sTipo);

            final CheckBox cualquiera = (CheckBox)view.findViewById(R.id.main_horario_dialog_otraseccion_cualquiera);
            final CheckBox mismo = (CheckBox)view.findViewById(R.id.main_horario_dialog_otraseccion_mismo);
            final CheckBox otro = (CheckBox)view.findViewById(R.id.main_horario_dialog_otraseccion_dif);
            final CheckBox elegir = (CheckBox)view.findViewById(R.id.main_horario_dialog_otraseccion_elegir);
            final HorarioCheckBox horarioCheckBox = (HorarioCheckBox)view.findViewById(R.id.main_horario_dialog_otraseccion_horario);
            checked.put(sTipo, horarioCheckBox.getChecked());
            cualquiera.setChecked(true);
            map.put(sTipo, 0);

            cualquiera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cualquiera.isChecked()) {
                        mismo.setChecked(false);
                        otro.setChecked(false);
                        if (elegir.isChecked()) {
                            elegir.setChecked(false);
                            horarioCheckBox.setVisibility(View.GONE);
                        }
                        map.put(sTipo, 0);
                    } else {
                        cualquiera.setChecked(true);
                    }
                }
            });

            mismo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mismo.isChecked()) {
                        cualquiera.setChecked(false);
                        otro.setChecked(false);
                        map.put(sTipo, 1);
                        if (elegir.isChecked()){
                            elegir.setChecked(false);
                            horarioCheckBox.setVisibility(View.GONE);
                        }
                    } else {
                        mismo.setChecked(true);
                    }
                }
            });

            otro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (otro.isChecked()) {
                        mismo.setChecked(false);
                        cualquiera.setChecked(false);
                        map.put(sTipo, 2);
                        if (elegir.isChecked()){
                            elegir.setChecked(false);
                            horarioCheckBox.setVisibility(View.GONE);
                        }
                    } else {
                        otro.setChecked(true);
                    }
                }
            });

            elegir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (elegir.isChecked()){
                        mismo.setChecked(false);
                        cualquiera.setChecked(false);
                        otro.setChecked(false);
                        map.put(sTipo, 3);
                        horarioCheckBox.setVisibility(View.VISIBLE);
                    }else{
                        elegir.setChecked(true);
                    }
                }
            });

            root.addView(view);
        }
        scrollView.addView(root);
        return scrollView;
    }

    public void onOtraSeccionClicked(View v, RamoBuscaCursos ramo, RamoBuscaCursos otraSeccion){
        v.setSelected(!v.isSelected());
        if (v.isSelected()){
            for (Modulo modulo : otraSeccion.getModulos()){
                horarioView.addModulo(modulo, 70);
            }
            for (Modulo modulo : ramo.getModulos()){
                horarioView.changeAlpha(modulo, 170);
            }
        }else{
            for (Modulo modulo : otraSeccion.getModulos()){
                horarioView.removeModulo(modulo);
            }
            for (Modulo modulo : ramo.getModulos()){
                horarioView.changeAlpha(modulo, 255);
            }
        }
    }

    class BuscarSeccionesTask extends AsyncTask<Void, Void, ArrayList<RamoBuscaCursos>> {

        RamoBuscaCursos ramo;
        LinearLayout otherSectionsContainer;
        Map<String, Integer> map;
        Map<String, ArrayList<String>> checked;

        public BuscarSeccionesTask(RamoBuscaCursos ramo, LinearLayout container, Map<String, Integer> map, Map<String, ArrayList<String>> checked){
            this.ramo = ramo;
            otherSectionsContainer = container;
            this.map = map;
            this.checked = checked;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                    "Buscando otra sección para " + ramo.getSigla(), Snackbar.LENGTH_INDEFINITE).show();
            for (String key : map.keySet()){
                Log.d(key, String.valueOf(map.get(key)));
            }
            for (int i = 0; i < otherSectionsContainer.getChildCount(); i++){
                View v = otherSectionsContainer.getChildAt(i);
                v.setSelected(true);
                v.callOnClick();
            }
            otherSectionsContainer.removeAllViews();
            ProgressBar progressBar = new ProgressBar(getContext());
            progressBar.setIndeterminate(true);
            otherSectionsContainer.addView(progressBar);
        }

        @Override
        protected ArrayList<RamoBuscaCursos> doInBackground(Void... params) {
            FiltroBuscaCursos filtro = new FiltroBuscaCursos();
            filtro.setSigla(ramo.getSigla());
            try {
                return BuscaCursos.buscarCursos(filtro, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RamoBuscaCursos> ramosBuscaCursos) {
            super.onPostExecute(ramosBuscaCursos);
            otherSectionsContainer.removeAllViews();
            if (ramosBuscaCursos == null){
                Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                        "Error cargando otras secciones para " + ramo.getSigla(), Snackbar.LENGTH_SHORT).show();
            }else {
                int results = 0;
                for (final RamoBuscaCursos otroRamo : ramosBuscaCursos){
                    if (!otroRamo.equals(ramo) && ramoCumpleFiltro(otroRamo)) {
                        TextView textView = new TextView(getContext());

                        textView.setText(otroRamo.getSigla() + "-" + String.valueOf(otroRamo.getSeccion()) +
                                " (" + StringUtil.join(Arrays.asList(otroRamo.getProfesores()), "-") + ")");
                        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        textView.setPadding(
                                (int) getResources().getDimension(R.dimen.small_padding),
                                (int) getResources().getDimension(R.dimen.small_padding),
                                (int) getResources().getDimension(R.dimen.small_padding),
                                (int) getResources().getDimension(R.dimen.small_padding)
                        );
                        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_rect_selector));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onOtraSeccionClicked(v, ramo, otroRamo);
                            }
                        });
                        otherSectionsContainer.addView(textView);
                        results ++;
                    }
                }
                if (results == 0) {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                            "No hay otras secciones para " + ramo.getSigla(), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                            "Secciones encontradas! ", Snackbar.LENGTH_SHORT).show();
                }
            }

        }
        public boolean ramoCumpleFiltro(RamoBuscaCursos otroRamo){
            Map<String, Integer> matches = new HashMap<>();
            for (Modulo otroModulo : otroRamo.getModulos()){
                int tipoFiltro = map.get(otroModulo.getTipo());
                boolean cumple = false;

                if (!matches.containsKey(otroModulo.getTipo())){
                    Log.i("added", otroModulo.getTipo());
                    matches.put(otroModulo.getTipo(), 0);
                }

                switch (tipoFiltro){
                    case 1:
                        for (Modulo modulo : ramo.getModulos())  {
                            if (otroModulo.getTipo().equals(modulo.getTipo())) {
                                if (otroModulo.topa(modulo)){
                                    cumple = true;
                                    break;
                                }
                            }
                        }
                        if (!cumple) {
                            return false;
                        }
                    case 2:
                        cumple = true;
                        for (Modulo modulo : ramo.getModulos())  {
                            if (otroModulo.getTipo().equals(modulo.getTipo())) {
                                if (otroModulo.topa(modulo)){
                                    cumple = false;
                                    break;
                                }
                            }
                        }
                        if (!cumple) {
                            return false;
                        }
                        break;
                    case 3:
                        for (String moduloString : checked.get(otroModulo.getTipo())){
                            String dia = String.valueOf(moduloString.charAt(0));
                            int numero = Integer.valueOf(String.valueOf(moduloString.charAt(1)));
                            if (otroModulo.getDia().equals(dia) && otroModulo.getNumero() == numero){
                                cumple = true;
                                matches.put(otroModulo.getTipo(), matches.get(otroModulo.getTipo()) + 1);
                                break;
                            }
                        }
                        if (!cumple && otroRamo.getModulos().count(otroModulo.getTipo()) <= checked.get(otroModulo.getTipo()).size()){
                            return false;
                        }
                        break;
                }
            }
            for (String key : map.keySet()){
                if (map.get(key) == 3 && otroRamo.getModulos().count(key) > checked.get(key).size()){
                    if (matches.get(key) < checked.get(key).size()){
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
