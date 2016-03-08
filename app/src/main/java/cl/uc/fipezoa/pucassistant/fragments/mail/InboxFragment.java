package cl.uc.fipezoa.pucassistant.fragments.mail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.FragmentActivity;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.classes.mail.Mail;
import cl.uc.fipezoa.pucassistant.classes.mail.Mails;
import cl.uc.fipezoa.pucassistant.services.LoadMoreMailsService;
import cl.uc.fipezoa.pucassistant.services.MailSyncService;


public class InboxFragment extends Fragment {

    BroadcastReceiver syncBroadcastReceiver;
    BroadcastReceiver loadMoreMailsReceiver;

    Folder folder;

    Message[] messages;
    LinearLayout mailContainer;
    ScrollView scrollView;
    Mails showedMails = new Mails();
    ProgressBar progressBar;
    public static boolean syncPending = false;

    public int scrollX = 0;
    public int scrollY = 0;

    public InboxFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DataManager.loadSavedInboxMails(getContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (DataManager.inboxFolder == null) {
                    try {
                        DataManager.loadInboxFolder();
                        DataManager.removeDeleted();
                        Log.d("fodler loaded", "loaded");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        showedMails = new Mails();
        Log.d("fragment", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mail_fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).show();
        ((FragmentActivity)getActivity()).getSupportActionBar().setTitle("Inbox");
        mailContainer = (LinearLayout)view.findViewById(R.id.mail_container);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        showMails();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
        if (MailSyncService.running){
            Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Sincronizando inbox", Snackbar.LENGTH_INDEFINITE).show();
        }

        if (LoadMoreMailsService.running){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }

        if (syncPending){
            showMails();
            Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Inbox sincronizado", Snackbar.LENGTH_SHORT).show();
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(scrollX, scrollY);
            }
        });
        Log.d("fragment", "onResume");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mail_inbox, menu);
    }

    public void showMails(){

        for(int i = DataManager.savedInboxMails.size() - 1; i >= 0; i--){
            Mail mail = DataManager.savedInboxMails.get(i);
            if (!showedMails.contains(mail)){
                addMessage(mail, 0);
            }else{
                mail.updateView(getContext());
            }
        }

        for (Mail mail : DataManager.cachedInboxMails){
            if (showedMails.contains(mail)) {
                mail.updateView(getContext());
            }else{
                addMessage(mail);
            }
        }

        for (Mail showedMail : (Mails)showedMails.clone()){
            if (!DataManager.savedInboxMails.contains(showedMail) && !DataManager.cachedInboxMails.contains(showedMail)){
                mailContainer.removeView(showedMail.messageView);
                showedMails.remove(showedMail);
            }
        }
        syncPending = false;
    }

    public void addMessage(final Mail mail){
        mail.loadView(getContext());
        mail.messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollX = scrollView.getScrollX();
                scrollY = scrollView.getScrollY();
                ReadMailFragment fragment = new ReadMailFragment();
                Bundle args = new Bundle();
                args.putSerializable("mail", mail);
                fragment.setArguments(args);
                mail.setFlag(Flags.Flag.SEEN, true);
                mail.updateView(getContext());
                ((FragmentActivity) getActivity()).setFragment(fragment, true);
            }
        });
        mailContainer.addView(mail.messageView);
        showedMails.add(mail);
    }

    public void addMessage(final Mail mail, int index){
        mail.loadView(getContext());
        mail.messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollX = scrollView.getScrollX();
                scrollY = scrollView.getScrollY();
                ReadMailFragment fragment = new ReadMailFragment();
                Bundle args = new Bundle();
                args.putSerializable("mail", mail);
                fragment.setArguments(args);
                mail.setFlag(Flags.Flag.SEEN, true);
                mail.updateView(getContext());
                ((FragmentActivity) getActivity()).setFragment(fragment, true);
            }
        });
        mailContainer.addView(mail.messageView, index);
        showedMails.add(mail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showedMails = new Mails();
        Log.d("fragment", "onDestroyView");
    }

    public void addMessage(final Mail mail, final boolean last){
        addMessage(mail);
        if (last){
            Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Mensajes cargados", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void addMessage(final Mail mail, int index, final boolean last){
        addMessage(mail, index);
        if (last){
            Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Mensajes cargados", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("fragment", "onPause");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(syncBroadcastReceiver);
        DataManager.saveInboxMails(getContext());
        scrollX = scrollView.getScrollX();
        scrollY = scrollView.getScrollY();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sync:
                MailSyncService.startMailSyncService(getContext());
                Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Sincronizando inbox", Snackbar.LENGTH_INDEFINITE).show();
                break;
        }
        return true;
    }

    public void registerBroadcastReceiver(){
        syncBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(MailSyncService.SUCCESS, false);
                if (success) {
                    showMails();
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Inbox sincronizado", Snackbar.LENGTH_SHORT).show();
                }else{
                    syncPending = false;
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container), "Error sincronizando inbox", Snackbar.LENGTH_SHORT).show();
                }
            }
        };
        loadMoreMailsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String event = intent.getStringExtra("event");
                switch (event){
                    case LoadMoreMailsService.EVENT_STARTED:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case LoadMoreMailsService.EVENT_ENDED:
                        boolean success = intent.getBooleanExtra("success", false);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (!success){
                            syncPending = false;
                            Toast.makeText(getContext(), "Error cargando mas mensajes", Toast.LENGTH_SHORT).show();
                        }else showMails();
                        break;
                }
            }
        };
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(syncBroadcastReceiver,
                new IntentFilter(MailSyncService.MAIL_SYNCHRONIZED));
        manager.registerReceiver(loadMoreMailsReceiver, new IntentFilter(LoadMoreMailsService.ACTION));
    }
}
