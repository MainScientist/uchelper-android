package cl.uc.fipezoa.pucassistant.classes.mail;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import cl.uc.fipezoa.pucassistant.R;


public class Mail implements Serializable {

    private Flags trueFlags;
    private Flags falseFlags;
    public String subject;
    public String folderName;
    public Address[] from;
    public Address[] to;
    public Address[] cc;
    public Address[] bcc;
    public Date receivedDate;
    public int messageNumber;
    public transient Folder folder;
    public transient View messageView;
    public boolean pendingSync = false;
    public MailContent mailContent;

    public Mail(Message message) throws MessagingException, IOException {
        this.trueFlags = message.getFlags();
        this.falseFlags = new Flags();
        this.subject = message.getSubject();
        this.from = message.getFrom();
        this.to = message.getRecipients(Message.RecipientType.TO);
        this.cc = message.getRecipients(Message.RecipientType.CC);
        this.bcc = message.getRecipients(Message.RecipientType.BCC);
        this.messageNumber = message.getMessageNumber();
        this.folder = message.getFolder();
        this.folderName = folder.getFullName();
        this.mailContent = MailUtils.getText(message);
        this.receivedDate = message.getReceivedDate();
    }

    public void loadView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        messageView = inflater.inflate(R.layout.mail_main_message, null);
        updateView(context);
    }

    public void updateView(final Context context){
        if (messageView != null){
            TextView sender = (TextView)messageView.findViewById(R.id.mail_sender);
            final TextView subject = (TextView)messageView.findViewById(R.id.mail_subject);
            TextView content = (TextView)messageView.findViewById(R.id.mail_content);
            final View leido = messageView.findViewById(R.id.leido);
            final ImageButton favButton = (ImageButton)messageView.findViewById(R.id.fav_button);
            final ImageButton button = (ImageButton)messageView.findViewById(R.id.button);
            if (isSet(Flags.Flag.SEEN)){
                leido.setVisibility(View.GONE);
                subject.setTypeface(null, Typeface.NORMAL);
            }else{
                leido.setVisibility(View.VISIBLE);
                subject.setTypeface(null, Typeface.BOLD);
            }
            if (isSet(Flags.Flag.FLAGGED)){
                favButton.setSelected(true);
            }else{
                favButton.setSelected(false);
            }
            Address from = this.from[0];
            if (from instanceof InternetAddress) {
                sender.setText(((InternetAddress) from).getAddress());
            }else{
                sender.setText(from.getType());
            }
            subject.setText(this.subject);
            MailContent mailContent = this.mailContent;
            if (mailContent != null) {
                if (mailContent.isHtml) {
                    content.setText(Jsoup.parse(mailContent.content).text());
                } else {
                    content.setText(mailContent.content);
                }
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context, button);
                    if (isSet(Flags.Flag.SEEN)) {
                        menu.getMenu().add(0, R.id.mark_as_unread, Menu.FIRST, "Marcar como no leido");
                    } else {
                        menu.getMenu().add(0, R.id.mark_as_read, Menu.FIRST, "Marcar como leido");
                    }
                    if (isSet(Flags.Flag.FLAGGED)) {
                        menu.getMenu().add(0, R.id.mark_as_unflagged, Menu.FIRST, "Quitar destacado");
                    } else {
                        menu.getMenu().add(0, R.id.mark_as_flagged, Menu.FIRST, "Destacar");
                    }
                    menu.getMenu().add(0, R.id.delete, Menu.FIRST, "Eliminar");
                    menu.show();
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id){
                                case R.id.mark_as_flagged:
                                    setFlag(Flags.Flag.FLAGGED, true);
                                    favButton.setSelected(true);
                                    break;
                                case R.id.mark_as_unflagged:
                                    setFlag(Flags.Flag.FLAGGED, false);
                                    favButton.setSelected(false);
                                    break;
                                case R.id.mark_as_read:
                                    setFlag(Flags.Flag.SEEN, true);
                                    leido.setVisibility(View.GONE);
                                    subject.setTypeface(null, Typeface.NORMAL);
                                    break;
                                case R.id.mark_as_unread:
                                    setFlag(Flags.Flag.SEEN, false);
                                    leido.setVisibility(View.VISIBLE);
                                    subject.setTypeface(null, Typeface.BOLD);
                                    break;
                                case R.id.delete:
                                    Toast.makeText(context, "Aun no implementado", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                }
            });
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    if (v.isSelected()){
                        setFlag(Flags.Flag.FLAGGED, true);
                        Log.d("Flag set to true", "true");
                    }else{
                        setFlag(Flags.Flag.FLAGGED, false);
                    }
                }
            });
        }
    }

    public void setFlag(Flags.Flag flag, boolean set){
        if (set){
            if (!isSet(flag)) {
                this.trueFlags.add(flag);
            }
            falseFlags.remove(flag);
        }else{
            if (!this.falseFlags.contains(flag)){
                falseFlags.add(flag);
            }
            this.trueFlags.remove(flag);
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    pendingSync = true;
                    sync();
                } catch(MessagingException e){
                    Log.d("ERROR", "Error in sync", e);
                }
            }
        });
    }

    public boolean isSet(Flags.Flag flag){
        return this.trueFlags.contains(flag);
    }

    public void sync() throws MessagingException {
        if (folder != null){
            Message message = folder.getMessage(messageNumber);
            if (pendingSync) {
                message.setFlags(trueFlags, true);
                message.setFlags(falseFlags, false);
                pendingSync = false;
            }else{
                this.trueFlags = message.getFlags();
            }
        }else{
            Log.d("folder", "IS NULL");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Mail){
            Mail mail = (Mail)o;
            return folderName.equals(mail.folderName) && messageNumber == mail.messageNumber;
        }else if (o instanceof Message){
            Message mail = (Message)o;
            return folderName.equals(mail.getFolder().getFullName()) && messageNumber == mail.getMessageNumber();
        }else{
            return false;
        }
    }
}
