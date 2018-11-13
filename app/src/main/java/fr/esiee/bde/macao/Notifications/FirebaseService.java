package fr.esiee.bde.macao.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Field;

import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "Firebase";
    public static final String NOTIFICATION_CHANNEL_NAME = "Notifications Push";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications push";


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getColor(), remoteMessage.getNotification().getIcon());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        Log.d(TAG, "Schedule Job");
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String messageTitle, String messageBody, String messageColor, String messageIcon) {
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker(getResources().getString(R.string.app_name))
                .setSmallIcon(getNotificationIcon(messageIcon))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText(messageBody))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setShowWhen(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_NAME);
        }

        /*if(messageIcon != null){
            if(!messageIcon.equals("default")) {
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(messageIcon, "drawable", "fr.esiee.bde.macao")));
            }
            else {
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
        }*/

        if(messageColor != null){
            builder.setColor(Color.parseColor(messageColor));
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setColor(getColor(R.color.colorPrimary));
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        }*/

        mNotification.notify(0, builder.build());
    }

    private int getNotificationIcon(String messageIcon) {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        //If the build version is higher than kitkat we need to create Silhouette icon.
        int iconId = R.drawable.fuego_notification_icon;
        if(messageIcon != null) {
            iconId = getResources().getIdentifier(messageIcon, "drawable", "fr.esiee.bde.macao");
        }
        //return useWhiteIcon ? R.drawable.fuego_notification_icon : iconId;
        return iconId;
    }
}

