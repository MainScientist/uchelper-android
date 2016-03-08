package cl.uc.fipezoa.pucassistant.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.fragments.horarios.HorariosListFragment;

public class HorariosActivity extends FragmentActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horarios_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        DataManager.loadHorarios(this);
        setFragment(new HorariosListFragment(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
