package com.minhvu.proandroid.sqlite.database.services;

import com.minhvu.proandroid.sqlite.database.services.impl.ALongRunningReceiver;

/**
 * Created by vomin on 8/7/2017.
 */

public class ReceiverDataBroadcast extends ALongRunningReceiver {
    @Override
    public Class getLRSClass() {
        return ReceiverDataService.class;
    }
}
