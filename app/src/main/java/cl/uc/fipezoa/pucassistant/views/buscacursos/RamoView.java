package cl.uc.fipezoa.pucassistant.views.buscacursos;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cl.uc.fipezoa.pucapi.buscacursos.HorarioString;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 1/31/2016.
 */
public class RamoView extends LinearLayout {

    private RamoBuscaCursos ramo;
    private Button button;
    private View content;

    public RamoView(Context context, RamoBuscaCursos ramo) {
        super(context);
        setRamo(ramo);
        setupUi();
    }

    public RamoView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public void setupUi(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        content = inflater.inflate(R.layout.buscacursos_view_resumen_ramo, null);

        button = (Button)content.findViewById(R.id.buscacursos_ramo_button);
        TextView sigla = (TextView)content.findViewById(R.id.buscacursos_ramo_sigla);
        TextView nrc = (TextView)content.findViewById(R.id.buscacursos_ramo_nrc);
        TextView nombre = (TextView)content.findViewById(R.id.buscacursos_ramo_nombre);
        TextView prof = (TextView)content.findViewById(R.id.buscacursos_ramo_prof);
        TextView creditos = (TextView)content.findViewById(R.id.buscacursos_ramo_creditos);
        TextView campus = (TextView)content.findViewById(R.id.buscacursos_ramo_campus);
        TextView vacTotales = (TextView)content.findViewById(R.id.buscacursos_ramo_vac_totales);
        TextView vacDisp = (TextView)content.findViewById(R.id.buscacursos_ramo_vac_disp);
        View usaFlag = content.findViewById(R.id.buscacursos_ramo_english);
        LinearLayout horario = (LinearLayout)content.findViewById(R.id.buscacursos_ramo_horario);

        if (ramo.dictadoEnIngles()){
            usaFlag.setVisibility(View.VISIBLE);
        }else{
            usaFlag.setVisibility(View.INVISIBLE);
        }

        sigla.setText(ramo.getSigla() + "-" + String.valueOf(ramo.getSeccion()));
        nrc.setText(String.valueOf(ramo.getNrc()));
        nombre.setText(ramo.getNombre());
        String profesores = "";
        for (String profName : ramo.getProfesores()){
            profesores = profName + "\n";
        }

        prof.setText(profesores.trim());
        creditos.setText(String.valueOf(ramo.getCreditos()) + " Creditos");
        campus.setText(String.valueOf(ramo.getCampus()));
        vacTotales.setText(String.valueOf(ramo.getVacantesTotales()));
        vacDisp.setText(String.valueOf(ramo.getVacantesDisponibles()));

        LinearLayout messages = (LinearLayout)content.findViewById(R.id.buscacursos_ramo_messages);


        if (!DataManager.user.cumpleRequisito(ramo)){
            messages.addView(newMessage("No cumples los requisitos para este ramo.",
                    ContextCompat.getColor(getContext(), R.color.error)));
        }else{
            if (ramo.getRequisito() == null){
                messages.addView(newMessage("Error cargando los requisitos de este ramo.",
                        ContextCompat.getColor(getContext(), R.color.error)));
            }
            if(!DataManager.user.ramoTieneVacantes(ramo)){
                messages.addView(newMessage("Este ramo no tiene vacantes para ti.",
                        ContextCompat.getColor(getContext(), R.color.error)));
            }else{
                if (ramo.tieneVacantesReservadas()) {
                    messages.addView(newMessage("Este ramo tiene " + String.valueOf(ramo.getVacantesReservadasDisponibles(DataManager.user))
                                    + " vacantes disponibles para ti.",
                            ContextCompat.getColor(getContext(), R.color.ok)));
                }else{
                    messages.addView(newMessage("Este ramo tiene " + String.valueOf(ramo.getVacantesDisponibles())
                                    + " vacantes disponibles para ti.",
                            ContextCompat.getColor(getContext(), R.color.ok)));
                }
            }
        }
        if (DataManager.user.getFichaAcademica().ramosAprobados().contains(ramo)){
            messages.addView(newMessage("Ya aprobaste este ramo.",
                    ContextCompat.getColor(getContext(), R.color.warning)));
        }
        if (DataManager.user.getRamosEnCurso().contains(ramo)) {
            messages.addView(newMessage("Estas cursando este ramo actualmente.",
                    ContextCompat.getColor(getContext(), R.color.warning)));
        }


        for (HorarioString horarioString: ramo.getHorarioStrings()){
            LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.buscacursos_view_ramo_horario, null);
            ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_dias)).setText(horarioString.dias);
            ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_sala)).setText(horarioString.sala);
            ((TextView)linearLayout.findViewById(R.id.buscacursos_modulo_tipo)).setText(horarioString.tipo);
            horario.addView(linearLayout);
        }
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(layoutParams);
        addView(content);
    }

    public TextView newMessage(String text, int color){
        TextView message = new TextView(getContext());
        message.setTypeface(null, Typeface.BOLD);
        message.setText(text);
        message.setTextColor(color);
        return message;
    }

    public void addMessage(String text, int color){
        LinearLayout messages = (LinearLayout)content.findViewById(R.id.buscacursos_ramo_messages);
        messages.addView(newMessage(text, color));
    }

    public RamoBuscaCursos getRamo() {
        return ramo;
    }

    public void setRamo(RamoBuscaCursos ramo) {
        this.ramo = ramo;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        content.setOnClickListener(l);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        super.setOnFocusChangeListener(l);
        content.setOnFocusChangeListener(l);
    }

    @Override
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        super.setFocusableInTouchMode(focusableInTouchMode);
        content.setFocusableInTouchMode(focusableInTouchMode);
    }
}
