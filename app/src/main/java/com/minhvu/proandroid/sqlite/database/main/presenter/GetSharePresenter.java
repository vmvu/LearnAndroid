package com.minhvu.proandroid.sqlite.database.main.presenter;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.main.model.IGetShareModel;
import com.minhvu.proandroid.sqlite.database.main.view.Activity.GetShareView;
import com.minhvu.proandroid.sqlite.database.models.entity.Note;

/**
 * Created by vomin on 9/12/2017.
 */

public class GetSharePresenter extends MvpPresenter<IGetShareModel, GetShareView> implements IGetSharePresenter {
    private Uri currentUri = null;

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getAppContext() {
        return getView().getAppContext();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        unbindView();
        if(!isChangingConfiguration){
            model = null;
            currentUri = null;
        }
    }

    @Override
    public void setCurrentUri(Uri uri) {
        this.currentUri = uri;
    }

    @Override
    public Uri getCurrentUri() {
        return currentUri;
    }

    @Override
    public void loadNote() {
        if(currentUri == null){
            return;
        }
        Object v = getView();
        Note note = model.loadNote(currentUri.getPathSegments().get(1));
        if(note != null){
            int imageCount= model.loadImage(currentUri.getPathSegments().get(1));
            getView().visibleView();
            updateView(note);
            getView().updateImageCount(imageCount);
        }
    }

    @Override
    public void updateView(Note note) {
        getView().updateView(note.getTitle(), note.getContent());
    }

    @Override
    public void saveNote(TextView title, EditText content) {
        if(currentUri == null){
            boolean success = model.insertNote(title.getText().toString(), content.getText().toString());
            if(success){
                Toast toast = Toast.makeText(getActivityContext(), "saved Data", Toast.LENGTH_SHORT);
                getView().showToast(toast);
            }

        }else{
            boolean success = model.updateNote(currentUri.getPathSegments().get(1), content.getText().toString());
            if(success){
                Toast toast = Toast.makeText(getActivityContext(), "saved Data", Toast.LENGTH_SHORT);
                getView().showToast(toast);
            }
        }
    }

    @Override
    public void updateView() {

    }
}
