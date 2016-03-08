package cl.uc.fipezoa.pucassistant.classes.mail;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 * Created by fipezoa on 2/29/2016.
 */
public class MailUtils {


    public static MailContent getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            boolean textIsHtml = p.isMimeType("text/html");
            return new MailContent(textIsHtml, s);
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            MailContent text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    MailContent s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                MailContent s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    public static MailContent readMessage(Message message) throws IOException, MessagingException {
        if (message.isMimeType("text/*")){
            String s = (String)message.getContent();
            boolean textIsHtml = message.isMimeType("text/html");
            return new MailContent(textIsHtml, s);
        }else{
            return getText((Part)message.getContent());
        }
    }
}
