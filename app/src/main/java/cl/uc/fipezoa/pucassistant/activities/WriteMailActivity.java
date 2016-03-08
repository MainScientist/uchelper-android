package cl.uc.fipezoa.pucassistant.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import cl.uc.fipezoa.pucassistant.R;

public class WriteMailActivity extends AppCompatActivity {

    MultiAutoCompleteTextView to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_mail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        to = (MultiAutoCompleteTextView)findViewById(R.id.edit_text_to);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        arrayAdapter.add("fipezoa@uc.cl");
        arrayAdapter.add("sdauthievre@uc.cl");
        to.setAdapter(arrayAdapter);
        to.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Para", to.getText().toString());
            }
        });

    }

    public TextView createMailTextView(String text){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setBackgroundResource(R.drawable.bubble);
        tv.setPadding(
                (int)getResources().getDimension(R.dimen.small_padding),
                (int)getResources().getDimension(R.dimen.small_padding),
                (int)getResources().getDimension(R.dimen.small_padding),
                (int)getResources().getDimension(R.dimen.small_padding)
        );
        // tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,android.R.drawable.presence_offline, 0);
        return tv;
    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getResources(), viewBmp);

    }

}
