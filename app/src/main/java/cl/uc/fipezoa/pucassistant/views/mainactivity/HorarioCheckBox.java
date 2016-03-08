package cl.uc.fipezoa.pucassistant.views.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.uc.fipezoa.pucassistant.R;

/**
 * Created by fipezoa on 2/5/2016.
 */
public class HorarioCheckBox extends LinearLayout {

    Map<String, CheckBox> map = new HashMap<>();
    private ArrayList<String> checked = new ArrayList<>();
    static String[] days = new String[]{"", "L", "M", "W", "J", "V", "S"};

    public HorarioCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUi();
    }

    public void setupUi(){
        setOrientation(VERTICAL);

        // setFocusable(true);
        for (int modulo = 0; modulo < 9; modulo++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(HORIZONTAL);
            // row.setMinimumHeight((int) getResources().getDimension(R.dimen.min_row_height));
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);
            for (String day : days){
                if (day.equals("")){
                    if (modulo == 0){
                        View view = new View(getContext());
                        LinearLayout.LayoutParams lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                        lParams.weight = 1;
                        lParams.gravity = Gravity.CENTER;
                        view.setLayoutParams(lParams);
                        row.addView(view);
                    }else{
                        TextView dayView = new TextView(getContext());
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                        lParams.gravity = Gravity.CENTER_VERTICAL;
                        lParams.weight = 1;
                        dayView.setGravity(Gravity.CENTER);
                        dayView.setLayoutParams(lParams);
                        dayView.setText(String.valueOf(modulo));
                        dayView.setTextSize(getResources().getDimension(R.dimen.label_text_size));
                        row.addView(dayView);
                    }
                }else{
                    if (modulo == 0){
                        TextView dayView = new TextView(getContext());
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                        lParams.weight = 4;
                        lParams.gravity = Gravity.CENTER_VERTICAL;
                        lParams.setMargins(2, 0, 2, 0);
                        dayView.setPadding((int)getResources().getDimension(R.dimen.buscacursos_checkbox_correct_padding), 0, 0, 0);
                        // dayView.setGravity(Gravity.CENTER);
                        dayView.setLayoutParams(lParams);
                        dayView.setText(day);
                        dayView.setTextSize(getResources().getDimension(R.dimen.label_text_size));
                        row.addView(dayView);
                    }else{
                        final String key = day + String.valueOf(modulo);
                        CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked){
                                    checked.add(key);
                                }else{
                                    checked.remove(key);
                                }
                            }
                        });
                        LayoutParams lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                        lParams.gravity = Gravity.CENTER;
                        lParams.weight = 4;
                        lParams.setMargins(2, 0, 2, 0);
                        checkbox.setLayoutParams(lParams);
                        checkbox.setGravity(Gravity.CENTER);
                        map.put(key, checkbox);
                        row.addView(checkbox);
                    }
                }
            }
            addView(row);
            View hLine = new View(getContext());
            LayoutParams lineParams;
            if (modulo == 3){
                lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.altura_barra_break));
            }else {
                lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.altura_barra));
            }
            hLine.setLayoutParams(lineParams);
            hLine.setBackgroundColor(Color.parseColor("#AAAAAA"));
            addView(hLine);
        }
    }

    public ArrayList<String> getChecked(){
        return checked;
    }
}
