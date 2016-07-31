package cl.uc.fipezoa.pucassistant.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.Ramo;
import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Modulo;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Ramos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.views.mainactivity.HorarioView;

public class HorarioMakerActivity extends AppCompatActivity {

    ArrayList<String> siglas = new ArrayList<>();
    ArrayList<Ramos<RamoBuscaCursos>> horarios = new ArrayList<>();
    LinearLayout siglaContainer;
    LinearLayout resultsContainer;
    boolean searching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horario_maker_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("HorarioMaker Alpha");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        siglaContainer = (LinearLayout)findViewById(R.id.horario_maker_siglas);
        resultsContainer = (LinearLayout)findViewById(R.id.horario_maker_results);
//        addSigla("MAT1630");
//        addSigla("FIS1523");
//        addSigla("IIC1005");
//        addSigla("MAT1640");
    }

    public void onAddSigla(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout root = new LinearLayout(this);
        root.setPadding(
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin)
        );
        TextView sigla = new TextView(this);
        sigla.setText("Sigla: ");
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(sigla);
        root.addView(editText);

        builder.setView(root);
        builder.setPositiveButton("Agregar", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sigla = editText.getText().toString().trim();
                        if (sigla.length() < 6) {
                            Toast.makeText(HorarioMakerActivity.this, "Sigla inválida.", Toast.LENGTH_SHORT).show();
                        } else if (siglas.contains(sigla)) {
                            Toast.makeText(HorarioMakerActivity.this, "Ya has agregado esta sigla.", Toast.LENGTH_SHORT).show();
                        } else {
                            addSigla(sigla);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void addSigla(final String sigla){
        final LinearLayout siglaLayout = new LinearLayout(this);
        TextView siglaTextView = new TextView(this);
        Button button = new Button(this);
        button.setText("x");
        siglaTextView.setText(sigla.toUpperCase());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        siglaTextView.setLayoutParams(params);
        siglaLayout.addView(siglaTextView);
        siglaLayout.addView(button);
        siglas.add(sigla.toUpperCase());

        button.setLayoutParams(new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())
        ));


        final View horLine = new View(this);
        horLine.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics())));
        horLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siglaContainer.removeView(siglaLayout);
                siglaContainer.removeView(horLine);
                siglas.remove(sigla.toUpperCase());
            }
        });
        siglaContainer.addView(siglaLayout);
        siglaContainer.addView(horLine);
    }



    public void onMake(View view) {
        searching = true;
        findViewById(R.id.horario_maker_progressbar).setVisibility(View.VISIBLE);
        horarios = new ArrayList<>();
        resultsContainer.removeAllViews();
        for (String sigla : siglas){
            Log.d("sigla", sigla);
        }
        new HacerHorarios().execute();
    }

    public void addResult(Ramos<RamoBuscaCursos> horario){
        LinearLayout root = new LinearLayout(this);
        HorarioView horarioView = new HorarioView(this);
        for (RamoBuscaCursos ramo : horario){
            for (Modulo modulo : ramo.getModulos()){
                horarioView.addModulo(modulo);
            }
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                0, (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.activity_vertical_margin)
        );
        root.setLayoutParams(params);
        root.addView(horarioView);
        resultsContainer.addView(root);
    }

    class HacerHorarios extends AsyncTask<Void, Void, ArrayList<Ramos<RamoBuscaCursos>>>{

        Map<RamoBuscaCursos, Ramos<RamoBuscaCursos>> mismoHorario = new HashMap<>();

        @Override
        protected ArrayList<Ramos<RamoBuscaCursos>> doInBackground(Void... params) {
            ArrayList<Ramos<RamoBuscaCursos>> ramos = new ArrayList<>();
            for (final String sigla : siglas){
                FiltroBuscaCursos filtroBuscaCursos = new FiltroBuscaCursos();
                filtroBuscaCursos.setSigla(sigla);
                try {
                    Ramos resultados = BuscaCursos.buscarCursos(filtroBuscaCursos, false);
                    if (resultados.size() == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HorarioMakerActivity.this, "Error buscando ramo " + sigla, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    }else {
                        ramos.add(BuscaCursos.buscarCursos(filtroBuscaCursos, false));
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HorarioMakerActivity.this, "Error buscando ramo " + sigla, Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
            return ramos;
        }

        @Override
        protected void onPostExecute(ArrayList<Ramos<RamoBuscaCursos>> listaDeRamos) {
            if (listaDeRamos != null) {
                ArrayList<Ramos<RamoBuscaCursos>> filtrados = new ArrayList<>();
                for (Ramos<RamoBuscaCursos> ramos : listaDeRamos) {
                    Ramos<RamoBuscaCursos> horariosDistintos = new Ramos<>();
                    for (RamoBuscaCursos ramo : ramos) {
                        if (!horariosDistintos.mismoHorario(ramo)) {
                            horariosDistintos.add(ramo);
                            mismoHorario.put(ramo, new Ramos<>());
                        } else {
                            for (RamoBuscaCursos key : mismoHorario.keySet()) {
                                if (key.mismoHorario(ramo)) {
                                    mismoHorario.get(key).add(ramo);
                                }
                            }
                        }
                    }
                    filtrados.add(horariosDistintos);
                }
                for (Ramos<RamoBuscaCursos> ramos : filtrados) {
                    if (ramos.size() == 0) {

                    }
                }
                combinarHorarios(filtrados, new Ramos<>());
                searching = false;
                findViewById(R.id.horario_maker_progressbar).setVisibility(View.GONE);
                for (Ramos<RamoBuscaCursos> horario : horarios) {
                    addResult(horario);
                }
            }
        }

        public void combinarHorarios(ArrayList<Ramos<RamoBuscaCursos>> listaDeRamos, Ramos<RamoBuscaCursos> seleccionados){
            if (listaDeRamos.size() > 0) {
                Ramos<RamoBuscaCursos> ramos = listaDeRamos.remove(0);
                for (RamoBuscaCursos ramo : ramos) {
                    if (!seleccionados.topa(ramo)) {
                        seleccionados.add(ramo);
                        combinarHorarios(listaDeRamos, seleccionados);
                        seleccionados.remove(ramo);
                    }
                }
                listaDeRamos.add(ramos);
            }else{
                if (seleccionados.size() == siglas.size()) {
                    horarios.add((Ramos<RamoBuscaCursos>) seleccionados.clone());
                }
            }
        }
    }
}
