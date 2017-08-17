package com.minhvu.proandroid.sqlite.database.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.minhvu.proandroid.sqlite.database.PopupActivity;
import com.minhvu.proandroid.sqlite.database.R;

/**
 * Created by vomin on 8/11/2017.
 */

public class PinBroadcast extends BroadcastReceiver {
    private static final  String LOGTAG = PinBroadcast.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        sendNotification(context, intent);
    }


    private void sendNotification(Context ctx, Intent intent){
        Log.d(LOGTAG, "vao sendNotification");
        Uri uri = Uri.parse(intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_uri)));
        if(uri == null)
            return;
        String title = intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_title));
        String content = intent.getStringExtra(ctx.getResources().getString(R.string.notify_note_content));
        int color = intent.getIntExtra(ctx.getString(R.string.notify_note_color),
                ctx.getResources().getColor(R.color.backgroundColor_default));
        boolean onGoing = intent.getBooleanExtra(ctx.getString(R.string.notify_note_pin), false);
        Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_search_black_24dp);
        Log.d("Pin", "title: "+ title + "|| content:" + content);
        int id = Integer.parseInt(uri.getPathSegments().get(1).trim());
        Intent i = new Intent(ctx, PopupActivity.class);
        i.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, id, i, 0);

        Notification.Builder  builder = new Notification.Builder(ctx)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentText(content)
                .setTicker(title)
                .setLargeIcon(icon)
                .setOngoing(onGoing)
                .setContentIntent(pendingIntent)
                .setContentTitle(title);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());

    }
}
