package cl.uc.fipezoa.pucassistant.fragments.buscacursos;


import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.FragmentActivity;
import cl.uc.fipezoa.pucassistant.views.buscacursos.RamoView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultadosFragment extends Fragment {

    ArrayList<RamoBuscaCursos> resultados;

    public ResultadosFragment(){

    }

    public void setResultados(ArrayList<RamoBuscaCursos> resultados){
        this.resultados = resultados;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.buscacursos_fragment_resultados, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.buscacursos_results_container);
        String unidadAcademica = "";
        if (resultados == null){
            ((FragmentActivity)getActivity()).setFragment(new FormFragment(), true);
        }
        for (RamoBuscaCursos ramoBuscaCursos : resultados){
            if (!unidadAcademica.equals(ramoBuscaCursos.getUnidadAcademica())){
                unidadAcademica = ramoBuscaCursos.getUnidadAcademica();
                TextView title = new TextView(getContext());
                title.setText(unidadAcademica);
                title.setTextSize(getResources().getDimension(R.dimen.buscacursos_result_ua_size));
                title.setTypeface(null, Typeface.ITALIC);
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lParams.setMargins(0, 5, 0, 5);
                title.setLayoutParams(lParams);
                title.setGravity(Gravity.CENTER);
                linearLayout.addView(title);
            }
            RamoView ramoView = new RamoView(getContext(), ramoBuscaCursos);
//            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            ramoView.setLayoutParams(lParams);
            linearLayout.addView(ramoView);
        }
    }
}
