package com.minhvu.proandroid.sqlite.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.adapter.Adapterl;
import com.minhvu.proandroid.sqlite.database.db.BookContract;

public class MainActivity extends AppCompatActivity
        implements Adapterl.IBookAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOGTAG = "MainActivity";
    //private BookAdapter bookAdapter;
    private Adapterl bookAdapterl;
    private final int ID_BOOK_LOADER = 101;

    private FloatingActionButton fab;
    private ListView lv;
    private RecyclerView recyclerView;
    private int mPosition = RecyclerView.NO_POSITION;


    public static final String[] colNames = BookContract.BookEntry.getColumnNames();
    private Point point = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupInitialize();

        getSupportLoaderManager().initLoader(ID_BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(int bookID) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.setData(ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookID));
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onLongClick(View view, int bookID, String title, int pin) {
        Toast.makeText(this, "onLongClick:", Toast.LENGTH_SHORT).show();
        Uri uri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookID);
        showPopup(view, this, uri, title, pin);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        point.set((int) ev.getX(), (int) ev.getY());
        return super.dispatchTouchEvent(ev);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopup(View view, Context context, final Uri uri, String popupTile, final int pin) {

        int popupWidth = 600;
        int popupHeigh = 400;

        LinearLayout viewGroup = (LinearLayout) this.findViewById(R.id.popupViewGroup);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_activity_main, viewGroup);

        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setContentView(layout);
        popupWindow.setWidth(popupWidth);
        popupWindow.setHeight(popupHeigh);
        popupWindow.setFocusable(true);
        // clear the default translucent background
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        getPositionPopupDisplay(popupWidth, popupHeigh);
        Log.d(LOGTAG, "width2 = " + point.x + " height2 = " + point.y);
        popupWindow.showAtLocation(layout, Gravity.NO_GRAVITY, point.x, point.y);
        TextView tvTitlePopup = (TextView) layout.findViewById(R.id.tvTitle_Popup);
        tvTitlePopup.setText(popupTile);
        TextView tvDeleltePopup = (TextView) layout.findViewById(R.id.tvDelete_popup);
        tvDeleltePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook(uri);
                cancelNotification(uri, pin);
                popupWindow.dismiss();
            }
        });
    }

    private void getPositionPopupDisplay(int widthPopup, int heightPopup) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightDevice = displayMetrics.heightPixels;
        int widthDevice = displayMetrics.widthPixels;
        Log.d(LOGTAG, "width = " + widthDevice + " height = " + heightDevice);
        if (point.y > heightDevice - heightPopup) {
            point.y = heightDevice - heightPopup / 2;
        }
    }

    private void deleteBook(Uri uri) {
        if (uri == null)
            return;
        ContentResolver resolver = getContentResolver();
        int deleted = resolver.delete(uri, null, null);
        if (deleted > 0) {
            Toast.makeText(this, "Delete successful", Toast.LENGTH_SHORT).show();
        }
    }
    private void cancelNotification(Uri uri, int pin){
        if(pin != 1)
            return;
        String action_broadcast = getString(R.string.broadcast_receiver_pin);
        Intent intent = new Intent(action_broadcast);
        intent.putExtra(getString(R.string.notify_note_uri), uri.toString());
        intent.putExtra(getString(R.string.notify_note_remove), true);
        sendBroadcast(intent);
    }


    private void setupInitialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tieu de toolbar");
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // the third parameter reverse a UI list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //bookAdapter = new BookAdapter(this, this);
        //recyclerView.setAdapter(bookAdapter);
        bookAdapterl = new Adapterl(this, this);
        bookAdapterl.onRecyclerViewAttached(recyclerView);

        recyclerView.setAdapter(bookAdapterl);

        fab = (FloatingActionButton) findViewById(R.id.fabInsert);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id != ID_BOOK_LOADER)
            return null;
        Uri uri = BookContract.BookEntry.CONTENT_URI;
        String sortOrder = BookContract.BookEntry.COLS_PIN + " DESC";
        return new CursorLoader(this, uri, colNames, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(LOGTAG, "onLoadFinished: " + data.getCount());
        int count = data.getCount();
        bookAdapterl.swapData(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookAdapterl.swapData(null);
    }
}
