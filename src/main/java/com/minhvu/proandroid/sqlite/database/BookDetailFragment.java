package com.minhvu.proandroid.sqlite.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.adapter.ColorAdapter;
import com.minhvu.proandroid.sqlite.database.db.Book;
import com.minhvu.proandroid.sqlite.database.db.BookContract;
import com.minhvu.proandroid.sqlite.database.db.Color;

/**
 * Created by vomin on 8/5/2017.
 */

public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOGTAG = BookDetailFragment.class.getSimpleName();
    private EditText etTitle;
    private EditText etContent;
    private Book book = null;
    private ImageButton btnPin;
    private ImageButton btnSetting;
    private ViewGroup viewGroup;

    private static final int ID_LOADER = 99;
    private static int TAG_KEY_PIN_COLOR = 66;
    private static int TAG_KEY_PIN_COLOR_BG = 10;
    private boolean mBookHasChanged = false;

    private Uri mCurrentBookUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mBookHasChanged = savedInstanceState.getBoolean("mBookHasChanged");
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void setCurrentBookUri(Uri uri) {
        this.mCurrentBookUri = uri;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        //set background default
        viewGroup = container;
        container.setBackgroundColor(getResources().getColor(R.color.backgroundColor_default));
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        etTitle = (EditText) view.findViewById(R.id.etxtTitle);
        etContent = (EditText) view.findViewById(R.id.etContent);
        btnSetting = (ImageButton) view.findViewById(R.id.btnSetting);
        btnPin = (ImageButton) view.findViewById(R.id.btnPinToStart);
        btnPin.setTag(false);

        int colorID = getActivity().getResources().getColor(R.color.headerColor_default);
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR, colorID);
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR_BG, getActivity().getResources().getColor(R.color.backgroundColor_default));
        //restore
        if (savedInstanceState != null) {
            etTitle.setText(savedInstanceState.getString("title"));
            etContent.setText(savedInstanceState.getString("content"));
            mBookHasChanged = savedInstanceState.getBoolean("mBookHasChanged");
            btnPin.setTag(savedInstanceState.getBoolean("btPin_tag"));
            //btnPin.setColorFilter(savedInstanceState.getInt("btnPin_color"));
        }

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupColorTable(container, btnPin);
            }
        });

        btnPin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTag_Pin();
                saveData();
                pinNoteToNotify();
                return true;
            }
        });

        etTitle.setOnTouchListener(mTouchListener);
        etContent.setOnTouchListener(mTouchListener);

        etContent.addTextChangedListener(new TextWatcher() {
            String charBefore;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                charBefore = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String titleTemp = etTitle.getText().toString();
                if (charBefore != null) {
                    if (titleTemp.equals(charBefore)) {
                        etTitle.setText(s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(contentShare)) {
            etTitle.setText(contentShare);
            etContent.setText(contentShare);
        }
        return view;
    }

    private void setTag_Pin() {
        boolean btnFlag = (boolean) btnPin.getTag();
        if (btnFlag) {
            btnPin.setImageResource(R.drawable.ic_star_border_black_24dp);
            btnPin.setTag(false);
        } else {
            btnPin.setImageResource(R.drawable.ic_star_black_24dp);
            btnPin.setTag(true);
        }
    }

    private void popupColorTable(final ViewGroup viewGroup, View view) {
        int popupWidth = 500;
        int popupHeight = 450;
        int[] local = new int[2];
        view.getLocationInWindow(local);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_color_table, null);

        final PopupWindow popup = new PopupWindow(getActivity());
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setAnimationStyle(R.anim.popup_anim);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.gridColorTable);
        recyclerView.setLayoutManager(layoutManager);
        ColorAdapter adapter = new ColorAdapter(getActivity(), new ColorAdapter.IColorAdapter() {
            @Override
            public void onClick(Color color) {
                setColorForPin(color);
                popup.dismiss();
                mBookHasChanged = true;
            }
        });
        adapter.swapData(Color.getColors(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        popup.showAtLocation(layout, Gravity.NO_GRAVITY, local[0], local[1] + 150);
    }
    private void setColorForPin(Color color){
        btnPin.setColorFilter(color.getHeaderColor());
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR, color.getHeaderColor());
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR_BG, color.getBackgroundColor());
        viewGroup.setBackgroundColor(color.getBackgroundColor());
    }

    private void popupSettingTable() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int popupWith = displayMetrics.widthPixels - 200;
        int popupHeight = displayMetrics.heightPixels - 400;

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentBookUri != null) {
            getLoaderManager().initLoader(ID_LOADER, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBookHasChanged) {
            saveData();
            mBookHasChanged = false;
        }

    }

    private void saveData() {
        String noteTitle = etTitle.getText().toString();
        String noteContent = etContent.getText().toString();
        String currentTime = String.valueOf(BookContract.BookEntry.getCurrentTime());
        int noteColor = (int) btnPin.getTag(R.string.TAG_KEY_PIN_COLOR);
        int noteColorBackground = (int) btnPin.getTag(R.string.TAG_KEY_PIN_COLOR_BG);
        Log.d("Pin", "header: " + noteColor + " bg: " + noteColorBackground);
        boolean btPin_tag = (boolean) btnPin.getTag();
        int notePin = btPin_tag ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(BookContract.BookEntry.COLS_TITLE, noteTitle);
        cv.put(BookContract.BookEntry.COLS_CONTENT, noteContent);
        cv.put(BookContract.BookEntry.COLS_LASTUPDATE_ON, currentTime);
        cv.put(BookContract.BookEntry.COLS_COLOR, noteColor);
        cv.put(BookContract.BookEntry.COLS_COLOR_BACKGROUND, noteColorBackground);
        cv.put(BookContract.BookEntry.COLS_PIN, notePin);
        Log.d("Pin", "pin tag " + notePin);

        ContentResolver contentResolver = getActivity().getContentResolver();
        if (mCurrentBookUri != null) {

            int isSuccess = contentResolver.update(mCurrentBookUri, cv, null, null);
            if (isSuccess > 0) {
                popupSuccessful("Updated successful !");
            }
        } else {
            cv.put(BookContract.BookEntry.COLS_CREATION_ON, currentTime);
            mCurrentBookUri = contentResolver.insert(BookContract.BookEntry.CONTENT_URI, cv);
            if (mCurrentBookUri != null) {
                popupSuccessful("Insert successful!");
            }
        }
    }

    private void popupSuccessful(String context) {
        Toast.makeText(getContext(), context, Toast.LENGTH_SHORT).show();
    }

    private void pinNoteToNotify() {
        boolean isPin = (boolean) btnPin.getTag();
        String action_broadcast = getString(R.string.broadcast_receiver_pin);

        Intent intent = new Intent(action_broadcast);
        intent.putExtra(getString(R.string.notify_note_pin_uri), mCurrentBookUri.toString());
        if (isPin) {
            intent.putExtra(getString(R.string.notify_note_pin_title), etTitle.getText().toString());
            intent.putExtra(getString(R.string.notify_note_pin_content), etContent.getText().toString());
        } else {
            intent.putExtra(getString(R.string.notify_note_pin_remove), true);
        }
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        boolean btPin_tag = (boolean) btnPin.getTag();

        // Log.d("mau-",btnPin.getColorFilter().hashCode() + "");
        //int btnPin_color = btnPin.getColorFilter().hashCode();
        outState.putString("title", title);
        outState.putString("content", content);
        outState.putBoolean("mBookHasChanged", mBookHasChanged);
        outState.putBoolean("btPin_tag", btPin_tag);
        //outState.putInt("btnPin_color", btnPin_color);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] colNames_Book = BookContract.BookEntry.getColumnNames();
        return new CursorLoader(getContext(), mCurrentBookUri, colNames_Book, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;
        data.moveToFirst();
        etTitle.setText(data.getString(data.getColumnIndex(BookContract.BookEntry.COLS_TITLE)));
        etContent.setText(data.getString(data.getColumnIndex(BookContract.BookEntry.COLS_CONTENT)));
        btnPin.setTag(data.getInt(data.getColumnIndex(BookContract.BookEntry.COLS_PIN)) == 1 ? false : true);
        setTag_Pin();
        Color c = new Color();
        c.setHeaderColor(data.getInt(data.getColumnIndex(BookContract.BookEntry.COLS_COLOR)));
        c.setBackgroundColor(data.getInt(data.getColumnIndex(BookContract.BookEntry.COLS_COLOR_BACKGROUND)));
        setColorForPin(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etTitle.setText("");
        etContent.setText("");
        btnPin.setTag(null);
    }

    private String contentShare;

    public void setContentShare(String contentShare) {
        this.contentShare = contentShare;
        mBookHasChanged = true;
    }


}
