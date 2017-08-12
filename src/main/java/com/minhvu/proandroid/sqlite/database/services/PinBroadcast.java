package com.minhvu.proandroid.sqlite.database.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.ReceiverShareActivity;

/**
 * Created by vomin on 8/11/2017.
 */

public class PinBroadcast extends BroadcastReceiver {
    private static final  String LOGTAG = PinBroadcast.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isCancelPreviousNotify =
                intent.getBooleanExtra(context.getResources().getString(R.string.notify_note_pin_remove), false);
        if(isCancelPreviousNotify){
            cancelNotification(context, intent);
        }else{
            sendNotification(context, intent);
        }

    }
    private void cancelNotification(Context ctx, Intent intent){
        Uri uri = Uri.parse(intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_pin_uri)));
        int id = Integer.parseInt(uri.getPathSegments().get(1).trim());
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }

    private void sendNotification(Context ctx, Intent intent){
        Log.d(LOGTAG, "vao sendNotification");
        Uri uri = Uri.parse(intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_pin_uri)));
        if(uri == null)
            return;
        String title = intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_pin_title));
        String content = intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_pin_content));
        Log.d("Pin", "title: "+ title + "|| content:" + content);
        int id = Integer.parseInt(uri.getPathSegments().get(1).trim());
        Intent i = new Intent(ctx, ReceiverShareActivity.class);
        i.setData(uri);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, i, 0);

        Notification.Builder  builder = new Notification.Builder(ctx)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(content)
                .setTicker(title)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());

    }
}
