package com.minhvu.proandroid.sqlite.database.receiver;

import com.minhvu.proandroid.sqlite.database.receiver.ALongRunningReceiver;
import com.minhvu.proandroid.sqlite.database.services.ReceiverDataService;

/**
 * Created by vomin on 8/7/2017.
 */

public class ReceiverDataBroadcast extends ALongRunningReceiver {
    @Override
    public Class getLRSClass() {
        return ReceiverDataService.class;
    }
}
