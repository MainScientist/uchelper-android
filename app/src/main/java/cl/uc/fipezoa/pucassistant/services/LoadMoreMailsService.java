package cl.uc.fipezoa.pucassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import javax.mail.MessagingException;

import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.fragments.mail.InboxFragment;


public class LoadMoreMailsService extends IntentService {

    public static boolean running = false;
    public static String ACTION = "services.LoadMoreMails.ACTION";
    final public static String EVENT_STARTED = "services.LoadMoreMails.EVENT_STARTED";
    final public static String EVENT_ENDED = "services.LoadMoreMails.EVENT_ENDED";

    public LoadMoreMailsService() {
        super("LoadMoreMailsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            broadcastEventStarted();
            try {
                DataManager.loadMoreMessages();
                InboxFragment.syncPending = true;
                broadcastEventEnded(true);
            } catch (MessagingException e) {
                e.printStackTrace();
                broadcastEventEnded(false);
            }
        }
        running = false;
        stopSelf();
    }

    public static void startMailSyncService(Context context){
        if (!running) {
            running = true;
            Intent intent = new Intent(context, LoadMoreMailsService.class);
            context.startService(intent);
        }
    }

    public void broadcastEventEnded(boolean result){
        Intent intent = new Intent(ACTION);
        intent.putExtra("event", EVENT_ENDED);
        intent.putExtra("success", result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void broadcastEventStarted(){
        Intent intent = new Intent(ACTION);
        intent.putExtra("event", EVENT_STARTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
