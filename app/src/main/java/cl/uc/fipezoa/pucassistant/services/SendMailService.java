package cl.uc.fipezoa.pucassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendMailService extends IntentService {

    public static String MAIL_SENT = "SendMailService.MAIL_SENT";
    public static String SUCCESS = "SendMailService.SUCCESS";
    public static boolean running = false;

    public SendMailService() {
        super("SendMailService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ArrayList<InternetAddress> to = (ArrayList<InternetAddress>) intent.getSerializableExtra("to");
            String content = intent.getStringExtra("content");
            String subject = intent.getStringExtra("subject");
            for (InternetAddress address : to){
                Log.d("to", address.getAddress());
            }
            Log.d("subject", subject);
            Log.d("content", content);
        }
        running = false;
    }

    public static void startMailSyncService(Context context){
        if (!running) {
            Intent intent = new Intent(context, SendMailService.class);
            context.startService(intent);
        }

    }
}
