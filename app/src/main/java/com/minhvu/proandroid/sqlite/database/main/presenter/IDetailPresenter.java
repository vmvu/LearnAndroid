package com.minhvu.proandroid.sqlite.database.main.presenter;

import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.minhvu.proandroid.sqlite.database.main.model.IDetailModel;
import com.minhvu.proandroid.sqlite.database.main.view.Fragment.IDetailShow;

/**
 * Created by vomin on 8/24/2017.
 */

public interface IDetailPresenter {
    void setView(IDetailShow view);
    void onDestroy(boolean isChangeConfiguration);
    void setCurrentUri(Uri currentUri);
    Uri getCurrentUri();

    void settingPasswordForNote(String password);
    void saveNote(EditText title, EditText content, ImageButton color, int typeOfText);

    void handleForAlarms(SwitchCompat[] switchCompatArray);
    void switchCompatOnClick(View view, SwitchCompat[] switchCompatArray);
}
