package cl.uc.fipezoa.pucassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import cl.uc.fipezoa.pucapi.callbacks.LoadingCallback;
import cl.uc.fipezoa.pucapi.callbacks.Progress;
import cl.uc.fipezoa.pucapi.exceptions.LoginException;
import cl.uc.fipezoa.pucassistant.classes.DataManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UserReloadService extends IntentService {

    public static String PROGRESS_CHANGE = "userreloadservice.PROGRESS_CHANGE";
    public static String RELOAD_ENDED = "userreloadservice.RELOAD_ENDED";

    public static String LOGIN_EXCEPTION = "userreloadservice.LOGIN_EXCEPTION";
    public static String IO_EXCEPTION = "userreloadservice.IO_EXCEPTION";

    public static boolean running = false;


    public UserReloadService() {
        super("UserReloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            running = true;
            if (DataManager.user != null){
                try {
                    DataManager.user.reload(new LoadingCallback() {
                        @Override
                        public void onProgressChange(Progress progress) {
                            Intent broadcastIntent = new Intent(PROGRESS_CHANGE);
                            broadcastIntent.putExtra("message", progress.message);
                            LocalBroadcastManager.getInstance(UserReloadService.this).sendBroadcast(broadcastIntent);
                        }
                    });
                    Intent broadcastIntent = new Intent(RELOAD_ENDED);
                    broadcastIntent.putExtra("exception", "");
                    LocalBroadcastManager.getInstance(UserReloadService.this).sendBroadcast(broadcastIntent);
                } catch (LoginException e) {
                    Intent broadcastIntent = new Intent(RELOAD_ENDED);
                    broadcastIntent.putExtra("exception", LOGIN_EXCEPTION);
                    LocalBroadcastManager.getInstance(UserReloadService.this).sendBroadcast(broadcastIntent);
                } catch (IOException e) {
                    Intent broadcastIntent = new Intent(RELOAD_ENDED);
                    broadcastIntent.putExtra("exception", IO_EXCEPTION);
                    LocalBroadcastManager.getInstance(UserReloadService.this).sendBroadcast(broadcastIntent);
                }
            }
            running = false;
        }
    }

}
