package fr.esiee.bde.macao.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by delevacw on 07/11/17.
 */

public class WidgetService extends RemoteViewsService {

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    protected void onMessage(Context context, Intent data) {
        Intent intent_meeting_update=new  Intent(context,MacaoAppWidget.class);
        intent_meeting_update.setAction(MacaoAppWidget.UPDATE_MEETING_ACTION);
        sendBroadcast(intent_meeting_update);
    }
}