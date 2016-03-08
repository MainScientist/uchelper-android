package cl.uc.fipezoa.pucassistant.classes;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.splunk.mint.Mint;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import cl.uc.fipezoa.pucapi.AlumnoUC;
import cl.uc.fipezoa.pucapi.buscacursos.BuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.FiltroBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.RamoBuscaCursos;
import cl.uc.fipezoa.pucapi.buscacursos.Ramos;
import cl.uc.fipezoa.pucassistant.R;
import cl.uc.fipezoa.pucassistant.activities.LoginActivity;
import cl.uc.fipezoa.pucassistant.classes.mail.Mail;
import cl.uc.fipezoa.pucassistant.classes.mail.Mails;

/**
 * Created by fipezoa on 1/28/2016.
 */
public class DataManager {
    public static AlumnoUC user;
    public static ArrayList<MiHorario> horarios;
    public static Map<String, ArrayList<String>> horarioBackup;
    public static Mails savedInboxMails;
    public static Mails cachedInboxMails;
    public static Message[] inboxMessages;
    public static Store mailStore;
    public static Folder inboxFolder;
    private static int cachedMessages;
    final private static int MAILS_SAVED = 20;
    final private static int MAILS_TO_ADD = 20;

    public static void saveUser(Context mContext){
        if (user != null) {
            try {
                FileOutputStream fos = mContext.openFileOutput("user.puc", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(DataManager.user);
                fos.getFD().sync();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", DataManager.user.getUsername());
            editor.putString("password", DataManager.user.getPassword());
            editor.apply();
        }
    }

    public static void loadUser(Context mContext){
        try{
            FileInputStream fis = mContext.openFileInput("user.puc");
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (AlumnoUC)ois.readObject();
            if (user != null) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String segundaClave = sharedPreferences.getString("segunda_clave", null);
                if (segundaClave == null) {
                    user.setSegundaClave(null);
                } else {
                    if (segundaClave.length() == 0) {
                        user.setSegundaClave(null);
                    } else {
                        user.setSegundaClave(segundaClave);
                    }
                }
            }
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // Its k
        }
    }

    public static boolean deleteUser(Context mContext){
        boolean deleted = mContext.getFileStreamPath("user.puc").delete();
        if (deleted){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("password");
            editor.remove("segunda_clave");
            editor.apply();
            user = null;
        }
        return deleted;
    }

    public static void loadHorarios(Context mContext){
        loadHorarioBackup(mContext);
        try{
            FileInputStream fis = mContext.openFileInput("horarios.puc");
            ObjectInputStream ois = new ObjectInputStream(fis);
            DataManager.horarios = (ArrayList<MiHorario>)ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // Its k
        }
        if (horarios == null){
            horarios = new ArrayList<>();
        }
    }

    public static void loadHorarioBackup(Context mContext){
        try{
            FileInputStream fis = mContext.openFileInput("horarioBackup.puc");
            ObjectInputStream ois = new ObjectInputStream(fis);
            DataManager.horarioBackup = (Map<String, ArrayList<String>>)ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // Its k
        }
        if (horarioBackup == null){
            horarioBackup = new HashMap<>();
        }
    }

    public static void saveHorarios(Context mContext) {
        horarioBackup = new HashMap<>();
        for (MiHorario miHorario : horarios){
            if (!miHorario.getName().equals("Oficial")) {
                ArrayList<String> ramos = new ArrayList<>();
                for (RamoBuscaCursos ramo : miHorario.getRamos()) {
                    ramos.add(ramo.getSigla() + "/" + String.valueOf(ramo.getSeccion()) + "/" + miHorario.getPeriodo());
                }
                horarioBackup.put(miHorario.getName(), ramos);
            }
        }
        saveHorarioBackup(mContext);
        try {
            FileOutputStream fos = mContext.openFileOutput("horarios.puc", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(DataManager.horarios);
            fos.getFD().sync();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveHorarioBackup(Context mContext) {
        try {
            FileOutputStream fos = mContext.openFileOutput("horarioBackup.puc", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(DataManager.horarioBackup);
            fos.getFD().sync();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean asyncBackup(Context context){
        for (String key : horarioBackup.keySet()){
            String periodo = "";
            Ramos<RamoBuscaCursos> ramos = new Ramos<>();
            for (String siglaSeccionPeriodo : horarioBackup.get(key)){
                String[] data = siglaSeccionPeriodo.split("/");
                String sigla = data[0];
                int seccion = Integer.valueOf(data[1]);
                periodo = data[2];
                FiltroBuscaCursos filtro = new FiltroBuscaCursos(periodo);
                filtro.setSigla(sigla);
                try {
                    RamoBuscaCursos ramo = BuscaCursos.buscarCurso(filtro, seccion, true);
                    ramos.add(ramo);
                } catch (IOException e) {
                    e.printStackTrace();
                    horarios.clear();
                    return false;
                }
            }
            MiHorario horario = new MiHorario(key, ramos, periodo);
            horarios.add(horario);
        }
        saveHorarios(context);
        return true;
    }

    public static NotificationCompat.Builder ongoingNotificationBuilder(Context context){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(DataManager.user.getNumeroDeAlumno())
                .setContentText(DataManager.user.getNombre() + ", " +
                        DataManager.user.getCarrera())
                .setSmallIcon(R.drawable.ic_stat_uchelper)
                .setOngoing(true)
                .setStyle(getNotificationStyle());
        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(LoginActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        // Start of a loop that processes data and then notifies the user
        return mBuilder;
    }

    public static NotificationCompat.BigTextStyle getNotificationStyle(){
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        return style.bigText(user.getNombre() + "\n" + user.getCarrera());
    }

    public static void saveInboxMails(Context context){
        if (savedInboxMails == null){
            savedInboxMails = new Mails();
        }
        try{
            Log.d("saved mails size", String.valueOf(savedInboxMails.size()));
            FileOutputStream fos = context.openFileOutput("savedInbox.puc", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(savedInboxMails);
            fos.getFD().sync();
            oos.close();
        } catch (IOException e) {
            Mint.logException(e);
            Log.d("exception", "Handled", e);
        }
    }

    public static void loadSavedInboxMails(Context context){
        if (cachedInboxMails == null){
            cachedInboxMails = new Mails();
        }
        ObjectInputStream ois = null;
        try{
            FileInputStream fis = context.openFileInput("savedInbox.puc");
            ois = new ObjectInputStream(fis);
            savedInboxMails = (Mails)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            savedInboxMails = new Mails();
        }finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }
    }

    public static void loadMailStore() throws MessagingException {
        mailStore = user.getMailStore();
    }

    public static void loadInboxFolder() throws MessagingException {
        loadMailStore();
        inboxFolder = mailStore.getFolder("INBOX");
        inboxFolder.open(Folder.READ_WRITE);
        setInboxFolder();
    }

    public static void syncInboxMail(Context context) throws MessagingException {
        if (savedInboxMails == null){
            savedInboxMails = new Mails();
        }
        if (cachedInboxMails == null){
            cachedInboxMails = new Mails();
        }

        loadInboxFolder();
        inboxMessages = inboxFolder.getMessages();
        Log.d("messages size", String.valueOf(inboxMessages.length));

        removeDeleted();
        if (savedInboxMails.size() == 0){
            for (int i = inboxMessages.length - (MAILS_SAVED + 1); i < inboxMessages.length; i++){
                Message message = inboxMessages[i];
                if (!savedInboxMails.containsMessage(message.getMessageNumber())){
                    final Mail mail;
                    try {
                        Log.d("new message", message.getSubject());
                        mail = new Mail(message);
                        addInboxMail(mail);
                    } catch (IOException e) {
                        Log.d("Handled exception", "new mail from message failed", e);
                    }
                }
            }
        }else {
            Mail lastSavedMail = savedInboxMails.get(0);
            int i = inboxMessages.length - 1;
            Message message = inboxMessages[i];
            while (lastSavedMail.messageNumber != message.getMessageNumber()){
                final Mail mail;
                try {
                    Log.d("new message", message.getSubject());
                    mail = new Mail(message);
                    addInboxMail(mail);
                } catch (IOException e) {
                    Log.d("Handled exception", "new mail from message failed", e);
                }
                i--;
                message = inboxMessages[i];
            }
        }
        saveInboxMails(context);
    }

    public static void removeDeleted() throws MessagingException {
        for (Mail mail : (Mails)savedInboxMails.clone()){
            mail.sync();
            if (mail.isSet(Flags.Flag.DELETED)){
                savedInboxMails.remove(mail);
            }
        }
    }

    public static void setInboxFolder(){
        if (inboxFolder != null) {
            for (Mail mail : savedInboxMails) {
                mail.folder = inboxFolder;
            }
            for (Mail mail : cachedInboxMails) {
                mail.folder = inboxFolder;
            }
        }
    }

    private static void addInboxMail(Mail newMail){
        for (Mail mail : (Mails)savedInboxMails.clone()) {
            if (newMail.receivedDate.after(mail.receivedDate)){
                savedInboxMails.add(savedInboxMails.indexOf(mail), newMail);
                if (savedInboxMails.size() > MAILS_SAVED ) addCachedInboxMail(savedInboxMails.remove(savedInboxMails.size() - 1));
                break;
            }
        }
        if (savedInboxMails.size() >= MAILS_SAVED ){
            addCachedInboxMail(newMail);
        }else{
            savedInboxMails.add(savedInboxMails.size(), newMail);
        }

    }

    private static void addCachedInboxMail(Mail newMail){
        for (Mail mail : (Mails)cachedInboxMails.clone()) {
            if (newMail.receivedDate.after(mail.receivedDate)){
                cachedInboxMails.add(cachedInboxMails.indexOf(mail), newMail);
                break;
            }
        }
        cachedInboxMails.add(cachedInboxMails.size(), newMail);
    }

    public static void loadMoreMessages() throws MessagingException {
        int added = 0;
        if (inboxMessages == null){
            loadInboxFolder();
            inboxMessages = inboxFolder.getMessages();
        }
        for (int i = inboxMessages.length - (savedInboxMails.size() + cachedInboxMails.size() + 1); i >= 0; i--){
            if (i < 0 || added == MAILS_TO_ADD){
                break;
            }
            Message message = inboxMessages[i];
            if (!cachedInboxMails.containsMessage(message.getMessageNumber()) && !savedInboxMails.containsMessage(message.getMessageNumber())){
                try {
                    addCachedInboxMail(new Mail(message));
                    added += 1;
                } catch (MessagingException | IOException e) {
                    Log.d("handled error", "error while loading more mails", e);
                }
            }
        }
    }
}
