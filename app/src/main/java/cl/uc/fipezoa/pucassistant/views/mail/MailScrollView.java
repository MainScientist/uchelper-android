package cl.uc.fipezoa.pucassistant.views.mail;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import cl.uc.fipezoa.pucassistant.services.LoadMoreMailsService;

/**
 * Created by fipezoa on 3/3/2016.
 */
public class MailScrollView extends ScrollView {

    public MailScrollView(Context context) {
        super(context);
    }

    public MailScrollView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        View view = getChildAt(getChildCount()-1);
        int distanceToBottom = view.getBottom() - (getHeight() + scrollY);
        if (distanceToBottom == 0 && scrollY != oldScrollY && !LoadMoreMailsService.running){
            LoadMoreMailsService.startMailSyncService(getContext());
            Log.d("MailScrollView", "Reached bottom");
        }

    }
}
