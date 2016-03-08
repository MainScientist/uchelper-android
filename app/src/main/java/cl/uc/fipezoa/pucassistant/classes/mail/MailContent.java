package cl.uc.fipezoa.pucassistant.classes.mail;

import java.io.Serializable;

/**
 * Created by fipezoa on 2/29/2016.
 */
public class MailContent implements Serializable {

    public String content;
    public boolean isHtml;

    public MailContent(boolean isHtml, String content) {
        this.isHtml = isHtml;
        this.content = content;
    }
}
