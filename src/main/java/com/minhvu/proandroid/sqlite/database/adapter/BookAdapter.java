package com.minhvu.proandroid.sqlite.database.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minhvu.proandroid.sqlite.database.MainActivity;
import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.db.BookContract;

import java.util.Date;

/**
 * Created by vomin on 8/2/2017.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context mContext;
    private static Cursor mCursorData;
    private IBookAdapterOnClickHandler mClickHandler;


    public interface IBookAdapterOnClickHandler {
        void onClick(int bookID);
    }

    public BookAdapter(Context context, IBookAdapterOnClickHandler onClickHandler) {
        this.mClickHandler = onClickHandler;
        this.mContext = context;
    }


    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // cau hinh
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImediately = false;
        View view = inflater.inflate(R.layout.book_item, parent, shouldAttachToParentImediately);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        //data binding to UI
        mCursorData.moveToPosition(position);

        holder.tvTitle.setText(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[1])));
        holder.tvCreationOn.setText(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[3])));
        String tg = convertDateToString(
                mCursorData.getString(mCursorData.getColumnIndex(MainActivity.colNames[4])));
        holder.tvLastUpdate.setText(tg);

    }


    @Override
    public int getItemCount() {
        return (mCursorData == null) ? 0 : mCursorData.getCount();
    }
    //data binding onto mBook collection

    public void swapData(Cursor newData) {
        mCursorData = newData;
        notifyDataSetChanged();
    }

    private String convertDateToString(String longTimeType) {
        long times = Long.parseLong(longTimeType.trim());
        return BookContract.BookEntry.dateToString(new Date(times));
    }

    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvCreationOn;
        TextView tvLastUpdate;

        public BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvCreationOn = (TextView) itemView.findViewById(R.id.tvCreation_On);
            tvLastUpdate = (TextView) itemView.findViewById(R.id.tvLastUpdate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursorData.moveToPosition(getAdapterPosition());
            int bookID = mCursorData.getInt(mCursorData.getColumnIndex(BookContract.BookEntry._ID));
            mClickHandler.onClick(bookID);
        }
    }
}
