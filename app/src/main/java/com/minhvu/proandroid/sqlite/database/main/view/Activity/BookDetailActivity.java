package com.minhvu.proandroid.sqlite.database.main.view.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.minhvu.proandroid.sqlite.database.main.view.Fragment.BookDetailFragment;
import com.minhvu.proandroid.sqlite.database.R;

/**
 * Created by vomin on 8/2/2017.
 */

public class BookDetailActivity extends FragmentActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Uri mCurrentBookUri = null;
        if (intent != null) {
            mCurrentBookUri = intent.getData();
        }

        BookDetailFragment bookDetailFragment;
        FragmentManager fManager = getSupportFragmentManager();
        bookDetailFragment = (BookDetailFragment) fManager.findFragmentByTag(BookDetailFragment.class.getSimpleName());
        if (bookDetailFragment == null) {
            bookDetailFragment = new BookDetailFragment();
        }
        bookDetailFragment.setCurrentBookUri(mCurrentBookUri);
        FragmentTransaction transaction = fManager.beginTransaction();
        transaction.replace(R.id.fragment_content, bookDetailFragment, BookDetailFragment.class.getSimpleName());
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
