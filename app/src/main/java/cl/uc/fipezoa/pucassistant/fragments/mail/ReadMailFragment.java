package cl.uc.fipezoa.pucassistant.fragments.mail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;

import javax.mail.internet.InternetAddress;

import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.FragmentActivity;
import cl.uc.fipezoa.pucassistant.classes.mail.Mail;

/**
 * Created by fipezoa on 3/1/2016.
 */
public class ReadMailFragment extends Fragment {

    Mail mail;

    public ReadMailFragment(){

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mail = (Mail)args.getSerializable("mail");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mail_fragment_readmail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).hide();
        ((TextView)view.findViewById(R.id.mail_from)).setText(((InternetAddress) mail.from[0]).getAddress());
        ((TextView)view.findViewById(R.id.mail_subject)).setText(mail.subject);
        if (mail.mailContent.isHtml){
            view.findViewById(R.id.readmail_scrollview).setVisibility(View.GONE);
            view.findViewById(R.id.html_content).setVisibility(View.VISIBLE);

            WebView myWebView = (WebView)view.findViewById(R.id.html_content);
            WebSettings settings = myWebView.getSettings();
            settings.setSupportZoom(true);
            settings.setDisplayZoomControls(false);
            settings.setBuiltInZoomControls(true);
            settings.setUseWideViewPort(true);
            settings.setDefaultTextEncodingName("utf-8");
            myWebView.loadDataWithBaseURL(null, mail.mailContent.content, "text/html", "utf-8", null);
            myWebView.setInitialScale(100);

            // view.findViewById(R.id.html_content).setBackgroundColor(Color.TRANSPARENT);
        }else {
            ((TextView) view.findViewById(R.id.string_content)).setText(mail.mailContent.content);
        }
        view.findViewById(R.id.trashcan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.reply_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
