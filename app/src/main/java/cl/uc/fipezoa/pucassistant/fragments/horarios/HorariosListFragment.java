package cl.uc.fipezoa.pucassistant.fragments.horarios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Ramos;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.classes.MiHorario;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.FragmentActivity;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioView;


public class HorariosListFragment extends Fragment {

    LinearLayout content;
    FloatingActionButton fab;
    TextView noHayHorarios;
    ArrayList<MiHorario> cargados = new ArrayList<>();

    public HorariosListFragment() {
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
        return inflater.inflate(R.layout.horarios_fragment_lista, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Mis horarios");
        content = (LinearLayout)view.findViewById(R.id.fragment_content);

        noHayHorarios = new TextView(getContext());
        noHayHorarios.setText("Aun no tienes horarios guardados...");
        noHayHorarios.setPadding(
                (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin));


        fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNuevoHorario();
            }
        });
        if (FiltroBuscaCursos.dataLoaded) {
            fab.show();
        }else{
            fab.hide();
            LoadFormDataTask loadFormDataTask = new LoadFormDataTask();
            loadFormDataTask.execute();
        }
        if (DataManager.horarios.size() != DataManager.horarioBackup.keySet().size()){
            LoadBackupTask loadBackupTask = new LoadBackupTask();
            loadBackupTask.execute();
        }else {
            showHorarios();
        }
    }

    public void showHorarios(){
        content.removeAllViews();
        if (DataManager.horarios.size() == 0){
            content.addView(noHayHorarios);
        }else{
            for (MiHorario miHorario : DataManager.horarios){
                addHorario(miHorario);
            }
        }
    }

    public void addHorario(final MiHorario horario){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final LinearLayout view = (LinearLayout)inflater.inflate(R.layout.horarios_view_horario, null);
        final HorarioView horarioView = (HorarioView)view.findViewById(R.id.horarios_view_horario_hv);
        final TextView nombre = (TextView)view.findViewById(R.id.horarios_view_horario_nombre);
        TextView periodo = (TextView)view.findViewById(R.id.horarios_view_horario_periodo);
        Button button = (Button)view.findViewById(R.id.horarios_view_horario_button);

        periodo.setText(horario.getPeriodo());
        nombre.setText(horario.getName());
        for (RamoBuscaCursos ramo : horario.getRamos()){
            for (Modulo modulo : ramo.getModulos()){
                horarioView.addModulo(modulo);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.horarios_popup_agr_ramo:
                                AddRamoFragment fragment = new AddRamoFragment();
                                fragment.setMiHorario(horario);
                                ((FragmentActivity) getActivity()).setFragment(fragment, true);
                                break;
                            case R.id.horarios_popup_borrar:
                                DataManager.horarios.remove(horario);
                                DataManager.saveHorarios(getContext());
                                content.removeView(view);
                                cargados.remove(horario);
                                if (DataManager.horarios.size() == 0){
                                    content.addView(noHayHorarios);
                                }
                                break;
                            case R.id.horarios_popup_camb_nombre:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                LinearLayout linearLayout = new LinearLayout(getContext());
                                final TextView setNombre = new TextView(getContext());
                                setNombre.setText("Nombre: ");

                                final EditText editText = new EditText(getContext());
                                editText.setText(horario.getName());
                                linearLayout.addView(setNombre);
                                linearLayout.addView(editText);
                                linearLayout.setGravity(Gravity.CENTER);
                                linearLayout.setPadding(
                                        (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                                        (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                                        (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                                        (int) getResources().getDimension(R.dimen.activity_vertical_margin)
                                );
                                editText.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                builder.setView(linearLayout);
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        horario.setName(editText.getText().toString());
                                        DataManager.saveHorarios(getContext());
                                        nombre.setText(editText.getText().toString());
                                    }
                                });
                                builder.create().show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.horario_popup);
                popupMenu.show();
            }
        });
        content.addView(view);
        cargados.add(horario);
    }

    public void setAdapter(Spinner spinner, ArrayList<String> strings){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String string : strings){
            adapter.add(string);
        }
        spinner.setAdapter(adapter);
    }

    public void onNuevoHorario(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Horario nuevo");

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.horarios_dialog_nuevohorario, null);

        Spinner periodo = (Spinner)view.findViewById(R.id.horarios_dialog_nuevohorario_periodo);
        setAdapter(periodo, FiltroBuscaCursos.periodosDisponibles);

        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nombreEditText = (EditText) view.findViewById(R.id.horarios_dialog_nuevohorario_nombre);
                Spinner periodo = (Spinner) view.findViewById(R.id.horarios_dialog_nuevohorario_periodo);
                String nombre = nombreEditText.getText().toString();
                boolean repeated = false;
                for (MiHorario horario : DataManager.horarios) {
                    if (horario.getName().equals(nombre)) {
                        repeated = true;
                        break;
                    }
                }
                if (!repeated) {
                    MiHorario miHorario = new MiHorario(nombreEditText.getText().toString(), new Ramos<>(), periodo.getSelectedItem().toString());
                    DataManager.horarios.add(miHorario);
                    DataManager.saveHorarios(getContext());
                    if (DataManager.horarios.size() == 1){
                        content.removeView(noHayHorarios);
                    }
                    addHorario(miHorario);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.horario_nombre_repetido), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab.hide();
    }

    class LoadBackupTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setMessage(getResources().getString(R.string.cargar_horario_actualizacion));
            dialog.setTitle(getResources().getString(R.string.cargar_horario_title));
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container), getResources().getString(R.string.cargar_horario_segundoplano), Snackbar.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DataManager.asyncBackup(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            for (MiHorario horario : DataManager.horarios){
                try {
                    if (!cargados.contains(horario)) {
                        addHorario(horario);
                    }
                }catch (IllegalStateException e){
                    // dont care
                }
            }
        }
    }

    class LoadFormDataTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            FiltroBuscaCursos.loadFormData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fab.show();
        }
    }
}
