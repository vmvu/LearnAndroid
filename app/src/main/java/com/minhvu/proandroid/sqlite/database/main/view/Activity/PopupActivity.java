package com.minhvu.proandroid.sqlite.database.main.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;

import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.main.view.Fragment.BookDetailFragment;

/**
 * Created by vomin on 8/7/2017.
 */

public class PopupActivity extends FragmentActivity {
    private static final String LOGTAG = PopupActivity.class.getSimpleName();
    private ImageButton btnSave;
    private ImageButton btnClose;
    private BookDetailFragment detailFragment;

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
            String uriString = intent.getData().toString();
            Bundle bundle =new Bundle();
            bundle.putString(getString(R.string.note_uri), uriString);
            detailFragment.setArguments(bundle);
        }


        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailFragment.setBookHasChanged(false);
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
