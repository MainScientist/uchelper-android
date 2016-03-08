package cl.uc.fipezoa.pucassistant.mywebview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 2/2/2016.
 */
public class MyWebViewgressBar extends LinearLayout {

    View bar;
    View emptySpace;


    public MyWebViewgressBar(Context context) {
        super(context);
        setupUi();
    }

    public MyWebViewgressBar(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        setupUi();
    }

    public void setupUi(){
        setOrientation(HORIZONTAL);
        bar = new View(getContext());
        bar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        emptySpace = new View(getContext());

        setProgress(0);

        addView(bar);
        addView(emptySpace);
    }

    public void setProgress(int percent){
        LayoutParams barParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, percent);
        bar.setLayoutParams(barParams);

        LayoutParams emptySpaceParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 100-percent);
        emptySpace.setLayoutParams(emptySpaceParams);
    }
}
