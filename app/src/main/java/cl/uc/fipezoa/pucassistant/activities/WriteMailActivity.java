package cl.uc.fipezoa.pucassistant.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.services.SendMailService;

public class WriteMailActivity extends AppCompatActivity {

    MultiAutoCompleteTextView to;
    EditText subjectEditText;
    EditText contentEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_mail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        to = (MultiAutoCompleteTextView)findViewById(R.id.edit_text_to);
        subjectEditText = (EditText)findViewById(R.id.subject);
        contentEditText = (EditText)findViewById(R.id.mail_content);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
//        arrayAdapter.add("fipezoa@uc.cl");
//        arrayAdapter.add("sdauthievre@uc.cl");
        to.setAdapter(arrayAdapter);
        to.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<InternetAddress> addresses = new ArrayList<>();
                for (String mail : to.getText().toString().split(",")){
                    if (mail.trim().length() > 0){
                        try {
                            addresses.add(new InternetAddress(mail));
                        } catch (AddressException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String subject = subjectEditText.getText().toString();
                String content =contentEditText.getText().toString();

                Intent intent = new Intent(WriteMailActivity.this, SendMailService.class);
                intent.putExtra("subject", subject);
                intent.putExtra("content", content);
                intent.putExtra("to", addresses);
                startService(intent);
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
