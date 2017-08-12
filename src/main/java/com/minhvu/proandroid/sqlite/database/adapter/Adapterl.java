package com.minhvu.proandroid.sqlite.database.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minhvu.proandroid.sqlite.database.MainActivity;
import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.db.BookContract;

import java.util.Date;

/**
 * Created by vomin on 8/6/2017.
 */

public class Adapterl extends ABookApdater<Adapterl.viewHolder> {


    private Context mContext;
    private Cursor mCursorData;
    private Adapterl.IBookAdapterOnClickHandler mClickHandler;


    public interface IBookAdapterOnClickHandler {
        void onClick(int bookID);

        void onLongClick(View view, int bookID, String title, int pin);
    }

    public Adapterl(Context context, Adapterl.IBookAdapterOnClickHandler onClickHandler) {
        this.mClickHandler = onClickHandler;
        this.mContext = context;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // cau hinh
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImediately = false;
        View view = inflater.inflate(R.layout.book_item, parent, shouldAttachToParentImediately);
        return new Adapterl.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        mCursorData.moveToPosition(position);

        holder.tvTitle.setText(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[1])));
        holder.tvContent.setText(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[2])));
        String tgCreationOn  = convertDateToString(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[3])));
        holder.tvCreationOn.setText(tgCreationOn);
        String tgLastUpdate = convertDateToString(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[4])));
        holder.tvLastUpdate.setText(tgLastUpdate);

        int pinTag = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry.COLS_PIN));
        int visible = View.GONE;
        int headerColor = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry.COLS_COLOR));
        if (pinTag == 1) {
            visible = View.VISIBLE;
            holder.imgViewPinTag.setColorFilter(headerColor);
        }
        holder.imgViewPinTag.setVisibility(visible);
        holder.bg.setBackgroundColor(mCursorData.getInt(
                mCursorData.getColumnIndex(BookContract.BookEntry.COLS_COLOR_BACKGROUND)));
        holder.viewBG.setBackgroundColor(headerColor);
    }

    @Override
    public int getItemCount() {
        return (mCursorData == null) ? 0 : mCursorData.getCount();
    }


    public void swapData(Cursor newData) {
        mCursorData = newData;
        notifyDataSetChanged();
    }

    public void onRecyclerViewAttached(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    private String convertDateToString(String longTimeType) {
        long times = Long.parseLong(longTimeType.trim());
        return BookContract.BookEntry.dateToString(new Date(times));
    }

    class viewHolder extends ABookApdater.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvTitle;
        TextView tvContent;
        TextView tvCreationOn;
        TextView tvLastUpdate;
        ImageView imgViewPinTag;
        View bg;
        View viewBG;


        public viewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent2);
            tvCreationOn = (TextView) itemView.findViewById(R.id.tvCreation_On);
            tvLastUpdate = (TextView) itemView.findViewById(R.id.tvLastUpdate);
            imgViewPinTag = (ImageView) itemView.findViewById(R.id.imgViewPinTag);
            viewBG = itemView.findViewById(R.id.viewBG);
            bg = itemView.findViewById(R.id.viewGroupBG);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursorData.moveToPosition(getAdapterPosition());
            int bookID = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry._ID));
            mClickHandler.onClick(bookID);
        }


        @Override
        public boolean onLongClick(View v) {
            mCursorData.moveToPosition(getAdapterPosition());
            int bookID = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry._ID));
            String title = mCursorData.getString(mCursorData.getColumnIndex(BookContract.BookEntry.COLS_TITLE));
            int pin = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry.COLS_PIN));

            mClickHandler.onLongClick(itemView, bookID, title, pin);
            return true;
        }
    }
}
