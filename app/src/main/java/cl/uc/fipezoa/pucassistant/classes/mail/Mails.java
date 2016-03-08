package cl.uc.fipezoa.pucassistant.classes.mail;

import java.util.ArrayList;

/**
 * Created by fipezoa on 3/1/2016.
 */
public class Mails extends ArrayList<Mail> {

    @Override
    public boolean contains(Object object) {
        for (Mail mail : this){
            if (mail.equals(object)){
                return true;
            }
        }
        return false;
    }

    public boolean containsMessage(int messageNumber){
        for (Mail mail : this){
            if (mail.messageNumber == messageNumber){
                return true;
            }
        }
        return false;
    }

    public Mail getMessage(int messageId){
        for (Mail mail : this){
            if (mail.messageNumber == messageId){
                return mail;
            }
        }
        return null;
    }
}
