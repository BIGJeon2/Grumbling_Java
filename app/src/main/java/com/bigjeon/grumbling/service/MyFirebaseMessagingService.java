package com.bigjeon.grumbling.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bigjeon.grumbling.App_Main_Activity;
import com.bigjeon.grumbling.Chatting_Activity;
import com.bigjeon.grumbling.MainActivity;
import com.bigjeon.grumbling.P2P_Chatting_Activity;
import com.bigjeon.grumbling.Show_Selected_Post_Activity;
import com.bigjeon.grumbling.User_Profile_View_activity;
import com.bigjeon.grumbling.data.Chat_Noti;
import com.bigjeon.grumbling.data.Favorite_Noti;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;
import com.example.grumbling.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessage";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "onMessageReceived: 1");
            String click_action = remoteMessage.getNotification().getClickAction();
            if (click_action.equals(".P2P")){
                sendNotification_Chat(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTag(), remoteMessage.getNotification().getTag(), null);
            }else if (click_action.equals(".Post")){
                sendNotification_Favorite(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTag(), remoteMessage.getNotification().getTag(), null);
            }else{
                sendNotification_Post_Chat(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTag(), remoteMessage.getNotification().getTag(), null);
            }
        }
        if (remoteMessage.getData() != null){
            Log.d(TAG, "onMessageReceived: 2" + remoteMessage.getData().get("click_action"));
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String data = remoteMessage.getData().get("data");
            String click_action = remoteMessage.getData().get("click_action");
            String tag = remoteMessage.getData().get("tag");
            String img = remoteMessage.getData().get("img");
            if (click_action.equals(".P2P")){
                sendNotification_Chat(title, body, data, tag, img);
            }else if (click_action.equals(".Post")){
                sendNotification_Favorite(title, body, data, tag, img);
            }else if (click_action.equals(".Friend")){
                sendNotification_Friend(title, body, data, tag, img);
            }else {
                sendNotification_Post_Chat(title, body, data, tag, img);
            }
        }
    }



    public void sendNotification_Chat(String tittle, String text, String data, String tag, String img) {
        Log.d(TAG, "sendNotification: ");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent Main_intent = new Intent(this, App_Main_Activity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(Main_intent);

        Intent P2P_Chat_intent = new Intent(this, P2P_Chatting_Activity.class);
        P2P_Chat_intent.putExtra("USER_UID", data);
        stackBuilder.addNextIntent(P2P_Chat_intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent( 0 /* Request code */,
                PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.default_notification_channel_id);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.noti_title, tittle);
            remoteViews.setTextViewText(R.id.noti_message, text);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.main_app_icon))
                    .setSmallIcon(R.mipmap.main_app_icon)
                    .setContent(remoteViews)
                    .setContentTitle(tittle)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

        NotificationTarget notificationTarget = new NotificationTarget(this.getApplicationContext(), R.id.noti_icon, remoteViews, notificationBuilder.build(), 0, tag);

        Glide.with(this.getApplicationContext()).asBitmap().circleCrop().load(Uri.parse(img)).into(notificationTarget);

            //notificationManager.notify(tag, 0 /* ID of notification */, notificationBuilder.build());
    }
    public void sendNotification_Favorite(String tittle, String text, String data, String tag, String img) {
        Log.d(TAG, "sendNotification: ");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent Main_intent = new Intent(this, App_Main_Activity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(Main_intent);

        Intent Favorite_intent = new Intent(this, Show_Selected_Post_Activity.class);
        Favorite_intent.putExtra("TITLE", data);
        stackBuilder.addNextIntent(Favorite_intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent( 0 /* Request code */,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.main_app_icon))
                .setSmallIcon(R.mipmap.main_app_icon)
                .setContentTitle(tittle)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(tag, 0 /* ID of notification */, notificationBuilder.build());
    }

    public void sendNotification_Friend(String tittle, String text, String data, String tag, String img) {
        Log.d(TAG, "sendNotification: ");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent Main_intent = new Intent(this, App_Main_Activity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(Main_intent);

        Intent Friend_intent = new Intent(this, User_Profile_View_activity.class);
        Friend_intent.putExtra("USER_UID", data);
        stackBuilder.addNextIntent(Friend_intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent( 0 /* Request code */,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, tittle);
        remoteViews.setTextViewText(R.id.noti_message, text);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.main_app_icon))
                .setSmallIcon(R.mipmap.main_app_icon)
                .setContent(remoteViews)
                .setContentTitle(tittle)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationTarget notificationTarget = new NotificationTarget(this.getApplicationContext(), R.id.noti_icon, remoteViews, notificationBuilder.build(), 0, tag);

        Glide.with(this.getApplicationContext()).asBitmap().circleCrop().load(Uri.parse(img)).into(notificationTarget);

        //notificationManager.notify(tag, 0 /* ID of notification */, notificationBuilder.build());
    }

    public void sendNotification_Post_Chat(String tittle, String text, String data, String tag, String img) {
        Log.d(TAG, "sendNotification: ");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent Main_intent = new Intent(this, App_Main_Activity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(Main_intent);

        Intent Chat_intent = new Intent(this, Chatting_Activity.class);
        Chat_intent.putExtra("TITLE", data);
        stackBuilder.addNextIntent(Chat_intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent( 0 /* Request code */,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, tittle);
        remoteViews.setTextViewText(R.id.noti_message, text);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.main_app_icon))
                .setSmallIcon(R.mipmap.main_app_icon)
                .setContent(remoteViews)
                .setContentTitle(tittle)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationTarget notificationTarget = new NotificationTarget(this.getApplicationContext(), R.id.noti_icon, remoteViews, notificationBuilder.build(), 0, tag);

        Glide.with(this.getApplicationContext()).asBitmap().circleCrop().load(Uri.parse(img)).into(notificationTarget);
    }
}
