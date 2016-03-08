package cl.uc.fipezoa.pucassistant.fragments.mainactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Información General");
        }
    }

    public void onViewCreated(View v, Bundle bundle){
        if (DataManager.user != null){
            TextView nombre = (TextView)v.findViewById(R.id.user_info_nombre);
            TextView nAlumno = (TextView)v.findViewById(R.id.user_info_nro_alumno);
            TextView carrera = (TextView)v.findViewById(R.id.user_info_carrera);
            TextView situacion = (TextView)v.findViewById(R.id.user_info_situacion);
            TextView generacion = (TextView)v.findViewById(R.id.user_info_generacion);
            TextView credTotales = (TextView)v.findViewById(R.id.user_info_cred_totales);
            TextView pga = (TextView)v.findViewById(R.id.user_info_pga);
            TextView rut = (TextView)v.findViewById(R.id.user_info_rut);
            TextView fNacimiento = (TextView)v.findViewById(R.id.user_info_f_nacimiento);
            TextView pais = (TextView)v.findViewById(R.id.user_info_pais);
            TextView genero = (TextView)v.findViewById(R.id.user_info_genero);
            ImageView fotoView = (ImageView)v.findViewById(R.id.foto_view);

            Bitmap bmap = BitmapFactory.decodeByteArray(DataManager.user.getFotoPortal(), 0, DataManager.user.getFotoPortal().length);
            fotoView.setImageBitmap(bmap);

            nombre.setText(DataManager.user.getNombre());
            nAlumno.setText(DataManager.user.getNumeroDeAlumno());
            carrera.setText(DataManager.user.getCarrera());
            situacion.setText(DataManager.user.getSituacion());
            generacion.setText(String.valueOf(DataManager.user.getGeneracion()));
            credTotales.setText(String.valueOf(DataManager.user.creditosTotales()));
            pga.setText(String.valueOf(DataManager.user.getPga()));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            fNacimiento.setText(dateFormat.format(DataManager.user.getFechaDeNacimiento()));
            rut.setText(DataManager.user.getRut());
            pais.setText(DataManager.user.getPais());
            genero.setText(DataManager.user.getGenero());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment_user_info, container, false);
    }
}
