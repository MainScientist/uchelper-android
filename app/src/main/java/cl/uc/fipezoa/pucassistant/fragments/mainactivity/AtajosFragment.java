package cl.uc.fipezoa.pucassistant.fragments.mainactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.FragmentActivity;

/**
 * Created by fipezoa on 2/21/2016.
 */
public class AtajosFragment extends Fragment {

    public AtajosFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Atajos");
        return inflater.inflate(R.layout.main_fragment_atajos, container, false);
    }
}
