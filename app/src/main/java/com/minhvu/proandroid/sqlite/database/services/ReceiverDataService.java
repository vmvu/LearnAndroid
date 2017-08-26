package com.minhvu.proandroid.sqlite.database.services;

import android.content.Intent;
import android.util.Log;

/**
 * Created by vomin on 8/7/2017.
 */

public class ReceiverDataService extends ALongRunningNonStickyBroadcastService {
    private static final String LOGTAG = ReceiverDataService.class.getSimpleName();

    public ReceiverDataService() {
        super("com.minhvu.proandroid.sqlite.database" + LOGTAG);
    }

    @Override
    public void handIntentBroadcast(Intent intentBroadcast) {
        String content = intentBroadcast.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(LOGTAG, "date receiver: " + content);
    }
}
