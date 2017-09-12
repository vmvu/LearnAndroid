package com.minhvu.proandroid.sqlite.database.main.view.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.main.model.GetShareModel;
import com.minhvu.proandroid.sqlite.database.main.model.IGetShareModel;
import com.minhvu.proandroid.sqlite.database.main.presenter.GetSharePresenter;
import com.minhvu.proandroid.sqlite.database.main.presenter.IGetSharePresenter;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by vomin on 9/11/2017.
 */

public class GetShareActivity extends AppCompatActivity implements View.OnClickListener, GetShareView{
    TextView tvImageCount;
    TextView tvTitle;
    EditText etContent;
    ImageButton btnSave;
    Button btnDetail;

    private IGetSharePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_share_activity);
        setupView();
        setup();

        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            String stringsShare = intent.getStringExtra(Intent.EXTRA_TEXT);
            handleShare(stringsShare);
        }

        if(intent.getData() != null){
            Uri noteUri = intent.getData();
            loadNote(noteUri);
        }

    }
    void setupView(){
        tvImageCount = (TextView) findViewById(R.id.tvImageCount);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        btnSave = (ImageButton) findViewById(R.id.btnInsert);
        btnSave.setOnClickListener(this);
        btnDetail = (Button) findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetShareActivity.this, BookDetailActivity.class);
                intent.setData(presenter.getCurrentUri());
                startActivity(intent);
            }
        });

        invisibleView();
    }
    void setup(){
        IGetShareModel model = new GetShareModel();
        presenter = new GetSharePresenter();
        presenter.bindView(this);
        presenter.setModel(model);
        model.setPresenter(presenter);
    }


    @Override
    public void onClick(View v) {
        presenter.saveNote(tvTitle, etContent);
        finish();
    }

    private void handleShare(String stringsShare){
        if(stringsShare == null){
            return;
        }
        tvTitle.setText(stringsShare);
        etContent.setText(stringsShare);
    }

    private void loadNote(Uri uri){
        if(uri == null){
            return;
        }
        presenter.setCurrentUri(uri);
        presenter.loadNote();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public Context getAppContext() {
        return getAppContext();
    }

    @Override
    public void visibleView() {
        tvImageCount.setVisibility(View.VISIBLE);
        btnDetail.setVisibility(View.VISIBLE);
    }

    @Override
    public void invisibleView() {
        tvImageCount.setVisibility(View.GONE);
        btnDetail.setVisibility(View.GONE);
    }

    @Override
    public void updateView(String title, String content) {
        tvTitle.setText(title);
        etContent.setText(content);
    }

    @Override
    public void finishThis() {
        finish();
    }

    @Override
    public void showToast(Toast toast) {
        toast.show();
    }

    @Override
    public void updateImageCount(int count) {
        String data = count + " Images";
        tvImageCount.setText(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());
        presenter = null;
    }
}
