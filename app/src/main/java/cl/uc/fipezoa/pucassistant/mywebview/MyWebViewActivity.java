package cl.uc.fipezoa.pucassistant.mywebview;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucapi.exceptions.LoginException;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.interfaces.LoginRunnable;


public class MyWebViewActivity extends AppCompatActivity {

    public final static String PORTAL ="PORTAL";
    public final static String WEBCURSOS ="WEBCURSOS";
    public final static String LABMAT ="LABMAT";
    public final static String SIBUC ="SIBUC";
    public final static String BUSCACURSOS ="BUSCACURSOS";
    public final static String SIDING ="SIDING";


    MyWebView myWebView;
    MyWebViewHeader header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String url = bundle.getString("url");
        final String page = bundle.getString("page");



        setContentView(setupView());
        if (page != null) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    header.showLogin();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    try{
                        login(page);
                        return true;
                    } catch (LoginException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyWebViewActivity.this, "Error de inicio de sesion", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyWebViewActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        header.hideLogin();
                        syncCookies();
                        myWebView.loadUrl(url);
                    } else {
                        finish();
                    }
                }
            }.execute();
        }else{
            myWebView.loadUrl(url);
        }
    }

    public void syncCookies(){
        Map<String, ArrayList<Cookie>> cookies = new HashMap<>();
        for (Cookie cookie : DataManager.user.getSession().getCookieStore().getCookies()){
            if (cookie.getDomain() != null){
                if (!cookies.keySet().contains(cookie.getDomain())){
                    ArrayList<Cookie> newArray = new ArrayList<>();
                    newArray.add(cookie);
                    cookies.put(cookie.getDomain(), newArray);
                }else{
                    cookies.get(cookie.getDomain()).add(cookie);
                }
            }
        }
        for (String domain : cookies.keySet()){
            CookieManager.getInstance().setCookie(domain, arrayCookieToString(cookies.get(domain)));
        }
        CookieSyncManager.getInstance().sync();
    }

    public String arrayCookieToString(ArrayList<Cookie> array){
        String s = "";
        for (Cookie cookie : array){
            s += cookie.getName() + "=" + cookie.getValue() + "; ";
        }
        return s.substring(0, s.length()-2);
    }

    public LinearLayout setupView(){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        header = new MyWebViewHeader(this);
        header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(header);
        header.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        MyWebViewgressBar progressBar = new MyWebViewgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics())));
        linearLayout.addView(progressBar);

        myWebView = new MyWebView(this);
        myWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        myWebView.setProgressBar(progressBar);
        myWebView.setMyWebViewHeader(header);
        linearLayout.addView(myWebView);
        return linearLayout;
    }

    public void login(String page) throws IOException, LoginException {
        switch (page){
            case PORTAL:
                DataManager.user.portalLogin();
                break;
            case SIBUC:
                DataManager.user.sibucLogin();
                break;
            case SIDING:
                DataManager.user.sidingLogin();
                break;
            case WEBCURSOS:
                DataManager.user.webCursosLogin();
                break;
            case LABMAT:
                DataManager.user.labmatLogin();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()){
            myWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
