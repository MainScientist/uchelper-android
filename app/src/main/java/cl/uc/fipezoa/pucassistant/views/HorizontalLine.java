package cl.uc.fipezoa.pucassistant.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 2/27/2016.
 */
public class HorizontalLine extends View {

    public HorizontalLine(Context context) {
        this(context, 1);
    }

    public HorizontalLine(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
    }

    public HorizontalLine(Context context, int height){
        super(context);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics())));
    }
}
