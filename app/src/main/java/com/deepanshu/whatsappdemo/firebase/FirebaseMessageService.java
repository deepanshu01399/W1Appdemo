package com.deepanshu.whatsappdemo.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.activity.MainActivity;
import com.deepanshu.whatsappdemo.model.NotificationData;
import com.deepanshu.whatsappdemo.model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.deepanshu.whatsappdemo.firebase.NotificationChannelUtil.NOTIFICATION_CHANNEL_ID;
import static com.deepanshu.whatsappdemo.firebase.NotificationChannelUtil.createnotificationChannel;


public class FirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", token);//only for see the token
        showNotifications(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendTokenToServer(s);
    }

    private void sendTokenToServer(String s) {
        //Todo this method is used to send the token to server
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Tokens");
        Token token=new Token(s);
        reference.child(firebaseUser.getUid()).setValue(token);

    }

    public void showNotifications(RemoteMessage remoteMessage) {
        NotificationCompat.Builder builder;
        Bitmap bitmap = null;
        Date now = new Date();
        //date,time is only used to generate an unique id for sending  multiple notification .
        long differentNotificationId = now.getTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createnotificationChannel(this);
        }
        //Todo if we want to send notification using FCM
        if (remoteMessage.getNotification() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                    .setGroupSummary(true)
                    .setContentIntent(pendingIntent);
            String imageurl = remoteMessage.getNotification().getImageUrl().toString();
            if (imageurl != null) {
                bitmap = getBitmapfromUrl(imageurl);
            }
        }

        //Todo send push notification from the postman
        else {
            String remoteMess=remoteMessage.getData().get("title");
            String data=remoteMessage.getFrom();
            String d=remoteMessage.getMessageId();
            String de=remoteMessage.getData().get("data");
            String  msg=remoteMessage.getData().get("body");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    /*.setContentTitle(remoteMessage.getData().get("title"))
            .setContentText(remoteMessage.getData().get("body"))*/
                    .setContentTitle("new Message ")
                        .setContentText("New message from  Wechat conversation")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                    .setContentIntent(pendingIntent);
            //String imageurl = remoteMessage.getData().get(getString(R.string.image));
            String imageurl="https://images-na.ssl-images-amazon.com/images/I/41NxGNlKzwL.png";
            if (imageurl != null) {
                bitmap = getBitmapfromUrl(imageurl);
            }
        }

        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)).setLargeIcon(bitmap);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) differentNotificationId, builder.build());

    }

    private Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
