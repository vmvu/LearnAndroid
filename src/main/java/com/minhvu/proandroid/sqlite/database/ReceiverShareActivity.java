package com.minhvu.proandroid.sqlite.database;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.db.BookContract;

/**
 * Created by vomin on 8/7/2017.
 */

public class ReceiverShareActivity extends FragmentActivity {
    private static final String LOGTAG = "ReceiverShareActivity";
    private ImageButton btnSave;
    private ImageButton btnClose;
    private BookDetailFragment detailFragment;
    private Uri mCurrentUri = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        FragmentManager fManager = getSupportFragmentManager();
        detailFragment = (BookDetailFragment) fManager.findFragmentByTag(BookDetailFragment.class.getSimpleName());
        if (detailFragment == null) {
            detailFragment = new BookDetailFragment();
        }

        final Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String contentShare = intent.getStringExtra(Intent.EXTRA_TEXT);
            detailFragment.setContentShare(contentShare);
        }
        if (intent.getData() != null) {
            mCurrentUri = intent.getData();
            detailFragment.setCurrentBookUri(mCurrentUri);
        }


        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnClose = (ImageButton) findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailFragment.onPause();
                detailFragment.onDestroy();
                finish();
            }
        });

        fManager.beginTransaction().replace(R.id.frag, detailFragment, BookDetailFragment.class.getSimpleName())
                .commit();

    }


}
