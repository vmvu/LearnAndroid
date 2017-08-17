package com.minhvu.proandroid.sqlite.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.db.Book;
import com.minhvu.proandroid.sqlite.database.db.BookContract;

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
