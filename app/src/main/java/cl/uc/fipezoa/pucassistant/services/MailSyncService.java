package cl.uc.fipezoa.pucassistant.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import javax.mail.MessagingException;

import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.pucassistant.fragments.mail.InboxFragment;


public class MailSyncService extends IntentService {

    public static String MAIL_SYNCHRONIZED = "MailSyncService.MAIL_SYNCHRONIZED";
    public static String SUCCESS = "MailSyncService.SUCCESS";
    public static boolean running = false;

    public MailSyncService() {
        super("MailSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            running = true;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean show_ongoing_notification = sharedPreferences.getBoolean("notifications_show_ongoing", true);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (show_ongoing_notification){
                NotificationCompat.Builder builder = DataManager.ongoingNotificationBuilder(this);
                builder.setProgress(0, 0, true);
                builder.setStyle(DataManager.getNotificationStyle().setSummaryText("Sincronizando mail"));
                mNotificationManager.notify(R.id.ongoing_notification, builder.build());
            }
            try {
                DataManager.syncInboxMail(this);

                InboxFragment.syncPending = true;

                Intent broadcastIntent = new Intent(MAIL_SYNCHRONIZED);
                broadcastIntent.putExtra(SUCCESS, true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } catch (MessagingException e) {
                Log.d("Handled exception", "Error sync mails", e);
                Intent broadcastIntent = new Intent(MAIL_SYNCHRONIZED);
                broadcastIntent.putExtra(SUCCESS, false);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            }
            if (show_ongoing_notification){
                mNotificationManager.notify(R.id.ongoing_notification, DataManager.ongoingNotificationBuilder(this).build());
            }
            running = false;
        }
        stopSelf();
    }

    public static void startMailSyncService(Context context){
        if (!running) {
            Intent intent = new Intent(context, MailSyncService.class);
            context.startService(intent);
        }

    }
}
