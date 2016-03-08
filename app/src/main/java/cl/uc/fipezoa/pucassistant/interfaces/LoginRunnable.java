package cl.uc.fipezoa.pucassistant.interfaces;

import java.io.Serializable;

/**
 * Created by fipezoa on 2/22/2016.
 */
public abstract class LoginRunnable implements Serializable {

    public LoginRunnable(){

    }

    public abstract boolean run();
}
