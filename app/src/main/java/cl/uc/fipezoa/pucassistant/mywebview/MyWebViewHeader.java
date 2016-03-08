package cl.uc.fipezoa.pucassistant.mywebview;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 2/21/2016.
 */
public class MyWebViewHeader extends LinearLayout {

    TextView titleTextView;
    TextView urlTextView;
    Button backButton;
    Button forwardButton;
    Button closeButton;
    MyWebView webView;
    TextView loginTextView;

    public MyWebViewHeader(Context context) {
        super(context);
        setupUi();
    }

    public MyWebViewHeader(Context context, AttributeSet set) {
        super(context, set);
        setupUi();
    }

    public void setupUi(){
        setOrientation(VERTICAL);

        LinearLayout container = new LinearLayout(getContext());
        container.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titles = new LinearLayout(getContext());
        titles.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5));
        titles.setOrientation(VERTICAL);

        titleTextView = new TextView(getContext());
        titleTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleTextView.setTextSize(titleTextView.getTextSize() * .4f);
        titleTextView.setGravity(Gravity.CENTER);
        titles.addView(titleTextView);

        urlTextView = new TextView(getContext());
        urlTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        urlTextView.setTextSize(urlTextView.getTextSize() * .2f);
        urlTextView.setGravity(Gravity.CENTER);

        titles.addView(urlTextView);

        backButton = new Button(getContext());
        backButton.setLayoutParams(new LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));

        backButton.setText("<");
        backButton.setEnabled(false);

        forwardButton = new Button(getContext());
        forwardButton.setLayoutParams(new LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));
        forwardButton.setText(">");
        forwardButton.setEnabled(false);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        forwardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null && webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        closeButton = new Button(getContext());
        closeButton.setLayoutParams(new LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));
        closeButton.setText("x");

        container.addView(backButton);
        container.addView(forwardButton);
        container.addView(titles);
        container.addView(closeButton);
        addView(container);

        View horLine = new View(getContext());
        horLine.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics())));
        horLine.setBackgroundColor(Color.parseColor("#444444"));
        addView(horLine);

        loginTextView = new TextView(getContext());
        loginTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loginTextView.setPadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics())
        );
        loginTextView.setText("Iniciando Sesion...");
        loginTextView.setTextColor(Color.WHITE);
        loginTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        loginTextView.setVisibility(View.GONE);

        addView(loginTextView);
    }

    public void showLogin(){
        loginTextView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        loginTextView.setAlpha(0f);
        loginTextView.setTranslationY(-loginTextView.getMeasuredHeight());
        loginTextView.setVisibility(View.VISIBLE);
        loginTextView.animate().translationY(0).alpha(1f).setDuration(1000);
    }

    public void hideLogin(){
        loginTextView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        loginTextView.setVisibility(View.VISIBLE);
        loginTextView.animate().translationY(-loginTextView.getMeasuredHeight()).alpha(0f).setDuration(1000)
        .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loginTextView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setUrl(String url){
        urlTextView.setText(url);
    }

    public void setTitle(String title){
        titleTextView.setText(title);
    }

    public void setMyWebView(MyWebView webView){
        this.webView = webView;
    }

    public void disableForward(){
        forwardButton.setEnabled(false);
    }

    public void enableForward(){
        forwardButton.setEnabled(true);
    }

    public void disableBack(){
        backButton.setEnabled(false);
    }

    public void enableBack(){
        backButton.setEnabled(true);
    }
}
