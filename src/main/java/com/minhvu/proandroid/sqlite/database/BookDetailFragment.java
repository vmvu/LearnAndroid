package com.minhvu.proandroid.sqlite.database;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.adapter.ColorAdapter;
import com.minhvu.proandroid.sqlite.database.db.BookContract;
import com.minhvu.proandroid.sqlite.database.db.Color;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vomin on 8/5/2017.
 */

public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOGTAG = BookDetailFragment.class.getSimpleName();
    private EditText etTitle;
    private EditText etContent;
    private ImageButton btnPin;
    private ImageButton btnSetting;
    private ViewGroup viewGroup;

    private static final int ID_LOADER = 99;

    private boolean mBookHasChanged = false;

    private static final String ALARM_SWITCH_KEY = "remind_to_me";


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
        if(container != null){
            container.setBackgroundColor(getResources().getColor(R.color.backgroundColor_default));
        }
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
                popupColorTable(btnPin);
            }
        });


        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSettingTable(btnSetting);
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


    private void popupColorTable(View view) {
        int popupWidth = 500;
        int popupHeight = 450;
        int[] local = new int[2];
        view.getLocationInWindow(local);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_color_table, null);

        final PopupWindow popup =
                popupConfiguration(layout, popupWidth, popupHeight, local[0], local[1] + 150, Gravity.NO_GRAVITY);

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

    }

    private void setColorForPin(Color color) {
        btnPin.setColorFilter(color.getHeaderColor());
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR, color.getHeaderColor());
        btnPin.setTag(R.string.TAG_KEY_PIN_COLOR_BG, color.getBackgroundColor());
        viewGroup.setBackgroundColor(color.getBackgroundColor());
    }

    private void popupSettingTable(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int popupWith = 150;
        int popupHeight = 1200;
        Toast.makeText(getContext(), popupHeight + "", Toast.LENGTH_SHORT).show();

        int[] local = new int[2];
        view.getLocationInWindow(local);

        final LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_setting_table, null);

        popupConfiguration(layout, popupWith, popupHeight, local[0], local[1] + 150, Gravity.NO_GRAVITY);

        final ImageView ivAlarm = (ImageView) layout.findViewById(R.id.ivAlarm);
        ivAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = layoutInflater.inflate(R.layout.popup_alarm_choose, null);
                popupAtParentPosition(ivAlarm, layout, 800, 850);
                saveData();
                chooseAlarmMode(layoutInflater, layout);
            }
        });

        final ImageView ivDelete = (ImageView) layout.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = layoutInflater.inflate(R.layout.popup_delete, null);
                popupAtParentPosition(ivDelete, layout, 600, 300);
            }
        });
        final ImageView ivPassWord = (ImageView) layout.findViewById(R.id.ivPassword);
        ivPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = layoutInflater.inflate(R.layout.popup_password_set, null);
                popupAtParentPosition(ivPassWord, layout, 600, 400);
            }
        });
    }


    private void popupAtParentPosition(View parentView, View layout, int popWidth, int popHeight) {

        int[] localView = new int[2];
        parentView.getLocationOnScreen(localView);
        Log.d(LOGTAG, "width:" + localView[0] + " height:" + localView[1]);
        int x = localView[0] - popWidth;
        int y = localView[1] - popHeight / 2;

        popupConfiguration(layout, popWidth, popHeight, x, y, Gravity.NO_GRAVITY);
    }

    private PopupWindow popupConfiguration(View layout, int width, int height, int x, int y, int gravity) {
        PopupWindow popup = new PopupWindow(getActivity());
        popup.setContentView(layout);
        popup.setWidth(width);
        popup.setHeight(height);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, gravity, x, y);
        return popup;
    }

    private void chooseAlarmMode(final LayoutInflater layoutInflater, View layout) {
        final SwitchCompat scPin = (SwitchCompat) layout.findViewById(R.id.scPin);
        final SwitchCompat sc15Min = (SwitchCompat) layout.findViewById(R.id.sc15Minute);
        final SwitchCompat sc30Min = (SwitchCompat) layout.findViewById(R.id.sc30Minute);
        final SwitchCompat scWhen = (SwitchCompat) layout.findViewById(R.id.scWhen);
        final SwitchCompat scAllDay = (SwitchCompat) layout.findViewById(R.id.scAllDay);
        final SwitchCompat scReset = (SwitchCompat) layout.findViewById(R.id.scReset);

        scPin.setTag("scPin");
        sc15Min.setTag("sc15Min");
        sc30Min.setTag("sc30Min");
        scWhen.setTag("scWhen");
        scAllDay.setTag("scAllDay");
        scReset.setTag("scReset");

        final String pref_file = getString(R.string.PREFS_ALARM_FILE);

        final SharedPreferences pref = getActivity().getSharedPreferences(pref_file, Context.MODE_PRIVATE);
        String id = BookDetailFragment.ALARM_SWITCH_KEY + mCurrentBookUri.getPathSegments().get(1);
        String switchState = pref.getString(id, null);
        final SwitchCompat[] sc = new SwitchCompat[]{scPin, sc15Min, sc30Min, scWhen, scAllDay, scReset};
        if (TextUtils.isEmpty(switchState) || switchState.equals(scReset.getTag().toString())) {
            scReset.setChecked(true);
        } else {
            setCheckForSwitch(sc, switchState, pref_file);
        }

        View.OnClickListener scOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat scTemps = (SwitchCompat) v;
                if (scTemps.isChecked()) {
                    Toast.makeText(getContext(), scTemps.getTag() + "/" + scAllDay.getTag(), Toast.LENGTH_SHORT).show();
                    if (scTemps.getTag().equals(scAllDay.getTag())) {
                        scTemps.setChecked(false);
                        popupAlarmSetTime(layoutInflater,  pref_file, true, sc, scTemps.getTag().toString());
                    }
                   else if (scTemps.getTag().equals(scWhen.getTag())) {
                        scTemps.setChecked(false);
                        popupAlarmSetTime(layoutInflater, pref_file, false, sc, scTemps.getTag().toString());
                    }
                    else{
                        setCheckForSwitch(sc, scTemps.getTag().toString(), pref_file);
                    }
                } else {
                    scReset.setChecked(true);
                    saveStateSwitch(pref_file, BookDetailFragment.ALARM_SWITCH_KEY, "scReset");
                    activeNotification(pref_file);
                }
            }
        };

        scPin.setOnClickListener(scOnClickListener);
        sc15Min.setOnClickListener(scOnClickListener);
        sc30Min.setOnClickListener(scOnClickListener);
        scWhen.setOnClickListener(scOnClickListener);
        scAllDay.setOnClickListener(scOnClickListener);
        scReset.setOnClickListener(scOnClickListener);
    }


    private void setCheckForSwitch(SwitchCompat[] sc, String switchType, String fileName) {
        for (SwitchCompat s: sc) {
            if (s.getTag().toString().equals(switchType)) {
                s.setChecked(true);
                saveStateSwitch(fileName, BookDetailFragment.ALARM_SWITCH_KEY, switchType);
            } else {
                s.setChecked(false);
            }
        }
        activeNotification(fileName);
    }

    private void saveStateSwitch(String fileName, String key, String switchType) {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();

        key += mCurrentBookUri.getPathSegments().get(1);
        editor.putString(key, switchType);
        editor.apply();
    }

    private void activeNotification(String fileName) {
        String id =  mCurrentBookUri.getPathSegments().get(1);
        int idIntType = Integer.parseInt(id.trim());
        SharedPreferences pref = getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String switchType = pref.getString(BookDetailFragment.ALARM_SWITCH_KEY + id, null);
        if (switchType == null) {
            return;
        }
        String action_broadcast = getString(R.string.broadcast_receiver_pin);

        Intent intent = new Intent(action_broadcast);
        intent.putExtra(getString(R.string.notify_note_uri), mCurrentBookUri.toString());
        intent.putExtra(getString(R.string.notify_note_title), etTitle.getText().toString());
        intent.putExtra(getString(R.string.notify_note_content), etContent.getText().toString());
        intent.putExtra(getString(R.string.notify_note_color), (int) btnPin.getTag(R.string.TAG_KEY_PIN_COLOR_BG));
        intent.putExtra(getString(R.string.notify_note_pin), false);
        switch (switchType) {
            case "scPin":
                intent.putExtra(getString(R.string.notify_note_pin), true);
                getActivity().sendBroadcast(intent);
                return;
            case "sc15Min":
                alarm(intent, System.currentTimeMillis() + 15 * 60000, idIntType);
                break;
            case "sc30Min":
                alarm(intent, System.currentTimeMillis() + 30 * 60000, idIntType);
                break;
            case "scWhen" :
                alarmForWhenAndAddDay(pref, intent, id, idIntType);
                break;
            case "scAllDay":
                intent.putExtra(getString(R.string.PREFS_ALARM_TO_DATE),
                        pref.getString(getString(R.string.PREFS_ALARM_TO_DATE), "0"));
                alarmForWhenAndAddDay(pref, intent, id, idIntType);
                break;
            default:
                cancelAlarmAndNotification(intent, idIntType, fileName);
                break;
        }
    }
    private void alarmForWhenAndAddDay(SharedPreferences pref,Intent intent, String id, int idIntType){
        intent.putExtra(getString(R.string.PREFS_ALARM_FROM_DATE),
                pref.getString(getString(R.string.PREFS_ALARM_FROM_DATE), "0"));
        intent.putExtra(getString(R.string.PREFS_ALARM_WHEN),
                pref.getString(getString(R.string.PREFS_ALARM_WHEN), "0"));

        String date = pref.getString( getString(R.string.PREFS_ALARM_FROM_DATE) + id, "0");
        String time  = pref.getString(getString(R.string.PREFS_ALARM_WHEN) +id,"0");
        long timeLongType = Long.parseLong(time);
        int minute = (int)((timeLongType / 60000) % 60);
        int hour = (int)((timeLongType/ (60*60000)) % 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        Log.d(LOGTAG, calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) +
                "-" + calendar.get(Calendar.YEAR));
        Log.d(LOGTAG, calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        alarm(intent, calendar.getTime().getTime(), idIntType);
    }

    private void alarm(Intent intent, long setTime, int requestCode) {

        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), requestCode, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(setTime);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }
    private void cancelAlarmAndNotification(Intent intent , int requestCode, String fileName){
        Log.d("Pin", "cancelAlarmAndNotification");
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        PendingIntent piActivity = PendingIntent.getActivity(getActivity(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        if(pi != null){
            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
            Log.d("Pin", "cancelAlarmAndNotification - pinActivity");
            NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(requestCode);

        //clear
        saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_FROM_DATE), "");
        saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_TO_DATE), "");
        saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_WHEN), "");
    }

    private void getDateFromDatePicker(long timeMillis, final TextView textView, final long minDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int nYear = calendar.get(Calendar.YEAR);
        int nMonth = calendar.get(Calendar.MONTH);
        int nDay = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(LOGTAG, nDay+"-"+nMonth+"-"+nYear);
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        },nYear, nMonth, nDay);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - minDate);
        dialog.show();
    }

    private void getTimeFromTimePicker(final String fileName, final String key) {
        Calendar newTime = Calendar.getInstance();
        final int hour = newTime.get(Calendar.HOUR_OF_DAY);
        int min = newTime.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                long time = (hourOfDay * 60 + minute) * 60000;
                saveStateSwitch(fileName, key, time + "");
            }
        }, hour, min, true);
        dialog.show();
    }

    private void popupAlarmSetTime(LayoutInflater layoutInflater, final String fileName,
                                   final boolean isAllDayType, final SwitchCompat[] sc, final String switchType) {

        View layout = layoutInflater.inflate(R.layout.alarm_allday, null);
        final TextView tvFromDate = (TextView) layout.findViewById(R.id.tvFromDate);
        final TextView tvToDate = (TextView) layout.findViewById(R.id.tvToDate);
        ImageButton btnFromDate = (ImageButton) layout.findViewById(R.id.btnFromDate);
        ImageButton btnToDate = (ImageButton) layout.findViewById(R.id.btnToDate);
        final ImageButton btnYes = (ImageButton) layout.findViewById(R.id.btnYes);
        ImageButton btnNo = (ImageButton) layout.findViewById(R.id.btnNo);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.layoutToDate);
        View blackLine1dp = layout.findViewById(R.id.blackLine1dp);
        final TimePicker tpWhen = (TimePicker) layout.findViewById(R.id.tpWhen);
        tpWhen.setIs24HourView(true);
        btnYes.setTag(false);

        long currentTimeMillis = System.currentTimeMillis();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        SharedPreferences pref = getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String noteID = mCurrentBookUri.getPathSegments().get(1);

        String fromDate = pref.getString(getString(R.string.PREFS_ALARM_FROM_DATE) + noteID, "");

        if (TextUtils.isEmpty(fromDate)) {
            tvFromDate.setText(dateFormat.format(currentTimeMillis));
        } else {
            tvFromDate.setText(dateFormat.format(new Date(Long.parseLong(fromDate))));
        }

        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeMillis = 0;
                try {
                    timeMillis = dateFormat.parse(tvFromDate.getText().toString()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getDateFromDatePicker(timeMillis,tvFromDate, 0);
            }
        });


        if (isAllDayType) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            long toDate = calendar.getTimeInMillis();
            if (!TextUtils.isEmpty(fromDate)) {
                toDate = Long.parseLong(pref.getString(
                          getString(R.string.PREFS_ALARM_TO_DATE) + noteID, toDate + ""));
            }
            tvToDate.setText(dateFormat.format(toDate));

            btnToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long timeMillis = 0;
                    try {
                        timeMillis = dateFormat.parse(tvToDate.getText().toString()).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    getDateFromDatePicker(timeMillis,tvToDate, 0);
                }
            });

        } else {
            linearLayout.setVisibility(View.GONE);
            blackLine1dp.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);


        String when = pref.getString(getString(R.string.PREFS_ALARM_WHEN) + noteID, "");
        long atTime;
        if(TextUtils.isEmpty(when)){
            atTime = ((calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)) * 60000);
        }else{
            atTime = Long.parseLong(when);
        }
        int minute = (int) ((atTime / (60 * 1000)) % 60);
        int hour = (int) ((atTime / (60 * 60 * 1000)) % 24);
        Log.d(LOGTAG,"when:" + when + "|current:" +currentTimeMillis +" ---  hour: "+ hour + "||minute: " + minute);
        tpWhen.setMinute(minute);
        tpWhen.setHour(hour);

        final PopupWindow popup = popupConfiguration(layout, 1000, isAllDayType ? 1100:1000, 0, 0, Gravity.CENTER);

        setCheckForSwitch(sc, "scReset", fileName);


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYes.setTag(false);
                popup.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYes.setTag(true);
                int minute = tpWhen.getMinute();
                int hour = tpWhen.getHour();
                long atTime = (hour * 60 + minute) * 60 * 1000;
                saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_WHEN), atTime + "");

                try {
                    String fromDate = tvFromDate.getText().toString();
                    fromDate = String.valueOf(dateFormat.parse(fromDate).getTime());
                    saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_FROM_DATE), fromDate);
                    if (isAllDayType) {
                        String toDate = tvToDate.getText().toString();
                        toDate = String.valueOf(dateFormat.parse(toDate).getTime());
                        saveStateSwitch(fileName, getString(R.string.PREFS_ALARM_TO_DATE), toDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                popup.dismiss();
                setCheckForSwitch(sc, switchType, fileName);
            }
        });
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

    public void setBookHasChanged(boolean bookHasChanged) {
        this.mBookHasChanged = bookHasChanged;
    }


}
