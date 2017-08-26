package com.minhvu.proandroid.sqlite.database.main.presenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.main.model.IDetailModel;
import com.minhvu.proandroid.sqlite.database.main.view.Fragment.IDetailShow;
import com.minhvu.proandroid.sqlite.database.models.data.NoteContract;
import com.minhvu.proandroid.sqlite.database.models.entity.Note;

import java.lang.ref.WeakReference;

/**
 * Created by vomin on 8/24/2017.
 */

public class DetailPresenter implements IDetailPresenter {
    private static final String LOGTAG = "DetailPresenter";
    private WeakReference<IDetailShow> mDetailView;
    private IDetailModel mDetailModel;
    private Uri mCurrentUri = null;
    public DetailPresenter(IDetailModel model){
        this.mDetailModel = model;
    }


    @Override
    public void onDestroy(boolean isChangeConfiguration) {
        mDetailView = null;
        mDetailModel.onDestroy(isChangeConfiguration);
        if(!isChangeConfiguration){
            mDetailModel = null;
            mCurrentUri = null;

        }
    }

    private IDetailShow getView(){
        if(mDetailView != null){
            return mDetailView.get();
        }else{
            throw new NullPointerException("View is unavailable");
        }
    }

    @Override
    public void setView(IDetailShow detailShow){
        mDetailView = new WeakReference<>(detailShow);
    }


    @Override
    public void setCurrentUri(Uri currentUri) {
        this.mCurrentUri = currentUri;
    }

    @Override
    public Uri getCurrentUri() {
        return mCurrentUri;
    }

    @Override
    public void settingPasswordForNote(String password) {

    }
    private int getColor(int color){


        int[] headerColors = getView().getActivityContext().getResources().getIntArray(R.array.header_color);
        int c = 0;
        for(int i= 0 ; i < headerColors.length; i++){
            if(headerColors[i] == color)
                return i;
        }
        return c;
    }
    private void printLog(EditText title, EditText content, ImageButton color, int typeOfText){
        Log.d(LOGTAG, "================ note =============");
        Log.d(LOGTAG, "title:" + title.getText().toString());
        Log.d(LOGTAG, "content: " + content.getText().toString());
        Log.d(LOGTAG, "color: " + getColor((int)color.getTag()));
        Log.d(LOGTAG, "typeOfText: " + typeOfText);
        Log.d(LOGTAG, "=============== end note ==========");
    }

    @Override
    public void saveNote(EditText title, EditText content, ImageButton color, int typeOfText) {
        printLog(title, content, color, typeOfText);
        Note note = new Note();
        note.setTitle(title.getText().toString() + "");
        note.setContent(content.getText().toString() + "");
        note.setIdColor(getColor((int)color.getTag()));
        note.setIdTypeOfText(typeOfText);
        note.setLastOn(System.currentTimeMillis());
        if(mCurrentUri == null){
            note.setDateCreated(System.currentTimeMillis());
            insertNote(note);
        }else{
            updateNote(note);
        }
    }
    private void insertNote(final Note note){
        final ContentResolver contentResolver = getView().getActivityContext().getContentResolver();
        new AsyncTask<Void, Void, Uri>() {
            @Override
            protected Uri doInBackground(Void... params) {
                return contentResolver.insert(NoteContract.NoteEntry.CONTENT_URI, note.getValues());
            }

            @Override
            protected void onPostExecute(Uri uri) {
                if(uri != null){
                    mCurrentUri = uri;
                    getView().showToast(
                            Toast.makeText(getView().getActivityContext(), "save data", Toast.LENGTH_SHORT)
                    );

                }
            }
        }.execute();
    }

    private void updateNote(final Note note){
        final ContentResolver resolver = getView().getActivityContext().getContentResolver();
        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                return resolver.update(mCurrentUri, note.getValues(), null, null);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer > 0){
                    getView().showToast(
                            Toast.makeText(getView().getActivityContext(), "save data", Toast.LENGTH_SHORT)
                    );
                }
            }
        }.execute();
    }
    private void deleteNote(){
        final ContentResolver resolver = getView().getActivityContext().getContentResolver();
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return resolver.delete(mCurrentUri, null, null);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer > 0){
                    getView().showToast(
                            Toast.makeText(getView().getActivityContext(), "delete data", Toast.LENGTH_SHORT)
                    );
                }
            }
        }.execute();
    }

    private void confirmDeleteNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getActivityContext());
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Delete note");
        builder.setMessage("Can you delete note?");

        AlertDialog dialog = builder.create();
        try{
            getView().showAlert(dialog);
        }catch (NullPointerException e){
            e.getMessage();
        }



    }

    @Override
    public void handleForAlarms(SwitchCompat[] switchCompatArray) {
        //[0]pin - [1]sc15Min - [2]sc30Min - [3]scWhen - [4]scAllDay - [5]scReset
        String noteID = mCurrentUri.getPathSegments().get(1);
        String switchState = mDetailModel.getDataSharePreference(
                getView().getActivityContext().getString(R.string.PREFS_ALARM_SWITCH_KEY));
        if(TextUtils.isEmpty(switchState) || switchState.equals(switchCompatArray[5].getTag().toString())){
            switchCompatArray[5].setChecked(true);//scReset
        }else{
            setCheckForSwitch(switchCompatArray, switchState );
        }

    }
    private void setCheckForSwitch(SwitchCompat[] sc, String switchState) {
        for (SwitchCompat s: sc) {
            if (s.getTag().toString().equals(switchState)) {
                s.setChecked(true);
                saveStateSwitch(getView().getActivityContext().getString(R.string.PREFS_ALARM_SWITCH_KEY) , switchState);
            } else {
                s.setChecked(false);
            }
        }
    }

    private void saveStateSwitch(String key, String data){
        key+= mCurrentUri.getPathSegments().get(1);
        mDetailModel.setDataSharePreference(key, data);
    }

    @Override
    public void switchCompatOnClick(View view, SwitchCompat[] switchCompatArray) {
        SwitchCompat sc = (SwitchCompat)view;
        if (sc.isChecked()) {
            Toast.makeText(getView().getActivityContext(), sc.getTag() + "/" + switchCompatArray[5].getTag(), Toast.LENGTH_SHORT).show();
            if (sc.getTag().equals(switchCompatArray[4].getTag())) {
                sc.setChecked(false);
                getView().showAlarmSpecial(true, switchCompatArray, sc.getTag().toString());
            }
            else if (sc.getTag().equals(switchCompatArray[3].getTag())) {
                sc.setChecked(false);
                getView().showAlarmSpecial(false, switchCompatArray, sc.getTag().toString());
            }
            else{
                setCheckForSwitch(switchCompatArray, sc.getTag().toString());
                activeNotification(pref_file);

            }
        } else {
            switchCompatArray[5].setChecked(true);
            saveStateSwitch(getView().getActivityContext().getString(R.string.PREFS_ALARM_SWITCH_KEY), switchCompatArray[5].toString());
            activeNotification(pref_file);
        }
    }
}
