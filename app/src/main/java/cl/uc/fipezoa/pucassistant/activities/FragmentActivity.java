package cl.uc.fipezoa.pucassistant.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.splunk.mint.Mint;

import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 2/18/2016.
 */
public class FragmentActivity extends AppCompatActivity {

    public void setFragment(Fragment fragment, boolean backstack){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (backstack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void setFragment(Fragment fragment, boolean backstack, String eventName){
        setFragment(fragment, backstack);
        Mint.logEvent(eventName);
    }
}
