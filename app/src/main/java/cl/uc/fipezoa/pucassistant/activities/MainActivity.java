package cl.uc.fipezoa.pucassistant.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.splunk.mint.Mint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.callbacks.LoadingCallback;
import cl.uc.fipezoa.pucapi.callbacks.Progress;
import cl.uc.fipezoa.pucapi.exceptions.LoginException;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.AtajosFragment;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.BuscaCursosFragment;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.FichaFragment;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.HorarioFragment;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.RamoFragment;
import cl.uc.fipezoa.pucassistant.fragments.mainactivity.UserInfoFragment;
import cl.uc.fipezoa.pucassistant.mywebview.MyWebViewActivity;

public class MainActivity extends FragmentActivity
        implements
        NavigationView.OnNavigationItemSelectedListener {

    int lastId = 0;
    Map<Integer, Fragment> menuFragment;
    Map<Integer, String> fragmentNames = new HashMap<>();
    static boolean reloaded = false;

    FragmentManager fragmentManager;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragmentManager = getSupportFragmentManager();

        setupToolBar();
        setupFloatingActionButton();
        setupDrawer();
        setFragment(new UserInfoFragment(), false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_ongoing_notification = sharedPreferences.getBoolean("notifications_show_ongoing", true);
        try {
            setShowOngoingNotification(show_ongoing_notification);
        }catch (NullPointerException e){
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_fab = sharedPreferences.getBoolean("show_fab", true);
        if (!show_fab) {
            ((FloatingActionButton) findViewById(R.id.fab)).hide();
        } else {
            ((FloatingActionButton) findViewById(R.id.fab)).show();
        }
        if (reloaded) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setFragment(new UserInfoFragment(), false);
            reloaded = false;
        }
    }

    public void setupToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (DataManager.user != null) {
            loadDrawerMenu();
        }
    }

    public void setupFloatingActionButton() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReloadAsyncTask reloadTask = new ReloadAsyncTask(fab.getContext(), getSupportFragmentManager(), fab);
                reloadTask.execute();
                DataManager.saveUser(MainActivity.this);
            }
        });
    }

    public void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadDrawerMenu() {
        menuFragment = new HashMap<>();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu navMenu = navigationView.getMenu();
        SubMenu ramos = navMenu.addSubMenu(Menu.FIRST, lastId, Menu.NONE, "Ramos");
        lastId += 1;
        for (RamoBuscaCursos ramo : DataManager.user.getRamosEnCurso()) {
            ramos.add(Menu.FIRST, lastId, Menu.NONE, ramo.getNombre());
            menuFragment.put(lastId, RamoFragment.newInstance(ramo));
            fragmentNames.put(lastId, "ramo_overview");
            lastId += 1;
        }
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "Horario").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_schedule));
        menuFragment.put(lastId, new HorarioFragment());
        fragmentNames.put(lastId, "horario");
        lastId += 1;
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "Ficha Académica").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_ficha));
        menuFragment.put(lastId, new FichaFragment());
        fragmentNames.put(lastId, "ficha_Academica");
        lastId += 1;
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "BuscaCursos").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_buscacursos));
        fragmentNames.put(lastId, "buscacursos");
        menuFragment.put(lastId, new BuscaCursosFragment());
        lastId += 1;
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "Atajos").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_atajos));
        fragmentNames.put(lastId, "atajos");
        menuFragment.put(lastId, new AtajosFragment());
        lastId += 1;
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "MailUC Alpha").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_mail));
        fragmentNames.put(lastId, "mail_uc");
        lastId += 1;
        navMenu.add(Menu.FIRST, lastId, Menu.NONE, "Cerrar Sesión").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_logout));


        LinearLayout header = (LinearLayout) navigationView.getHeaderView(0);
        TextView nombre = (TextView) header.getChildAt(0);
        TextView carrera = (TextView) header.getChildAt(1);
        nombre.setText(DataManager.user.getNombre());
        carrera.setText(DataManager.user.getCarrera());
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                setFragment(new UserInfoFragment(), true);
            }
        });
    }

    public void setShowOngoingNotification(boolean show){
        if (show){
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(R.id.ongoing_notification, DataManager.ongoingNotificationBuilder(this).build());
        }else{
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(R.id.ongoing_notification);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == lastId) {
            boolean deleted = DataManager.deleteUser(this);
            if (deleted) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error loggin out", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == lastId - 1) {
            Intent intent = new Intent(this, MailMainActivity.class);
            startActivity(intent);
            Mint.logEvent("mail_uc");
            return true;
        }else{
            setFragment(menuFragment.get(id), true, fragmentNames.get(id));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    public void onBuscarCurso(View view) {

        Intent intent = new Intent(this, BuscarCursoActivity.class);
        startActivity(intent);
    }

    public void onMisHorarios(View view) {

        Intent intent = new Intent(this, HorariosActivity.class);
        startActivity(intent);

    }

    public void onPortalUC(View view) {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        cookieSyncMngr.sync();
        Intent intent = new Intent(MainActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "https://portal.uc.cl");
        bundle.putString("page", MyWebViewActivity.PORTAL);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onWebCursos(View view) {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        cookieSyncMngr.sync();
        Intent intent = new Intent(MainActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://webcurso.uc.cl/portal");
        bundle.putString("page", MyWebViewActivity.WEBCURSOS);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onLabmat(View view) {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        cookieSyncMngr.sync();
        Intent intent = new Intent(MainActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://labmat.puc.cl/dashboard");
        bundle.putString("page", MyWebViewActivity.LABMAT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onSibuc(View view) {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        cookieSyncMngr.sync();
        Intent intent = new Intent(MainActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://bibliotecas.uc.cl");
        bundle.putString("page", MyWebViewActivity.SIBUC);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onSiding(View view) {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        cookieSyncMngr.sync();
        Intent intent = new Intent(MainActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "https://intrawww.ing.puc.cl/siding/index.phtml");
        bundle.putString("page", MyWebViewActivity.SIDING);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onBuscaCursos(View view) {
        final Intent intent = new Intent(this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://buscacursos.uc.cl");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onHorarioMaker(View view) {
        Intent intent = new Intent(this, HorarioMakerActivity.class);
        startActivity(intent);
    }

    private class ReloadAsyncTask extends AsyncTask<Void, Progress, Boolean> {

        Context mContext;
        FloatingActionButton fab;
        FragmentManager fragmentManager;


        public ReloadAsyncTask(Context context, FragmentManager fragmentManager, FloatingActionButton fab) {
            mContext = context;
            this.fragmentManager = fragmentManager;
            this.fab = fab;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.hide();
        }

        @Override
        protected void onProgressUpdate(final Progress... values) {
            super.onProgressUpdate(values);

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(fab, values[0].message, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DataManager.user.reload(new LoadingCallback() {
                    @Override
                    public void onProgressChange(Progress s) {
                        onProgressUpdate(s);
                    }
                });
                DataManager.saveUser(mContext);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (LoginException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                try {
                    Snackbar.make(fab, "Datos cargados exitosamente.", Snackbar.LENGTH_SHORT).show();
                    UserInfoFragment userInfo = new UserInfoFragment();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, userInfo).commit();
                } catch (IllegalStateException e) {
                    // If app is on background
                    reloaded = true;
                }
            } else {
                Snackbar.make(fab, "Hubo un error al cargar los datos.", Snackbar.LENGTH_SHORT).show();
            }
            fab.show();
        }
    }
}
