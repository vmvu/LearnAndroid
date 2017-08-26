package com.minhvu.proandroid.sqlite.database.main.view.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minhvu.proandroid.sqlite.database.Utils.DateTimeUtils;
import com.minhvu.proandroid.sqlite.database.main.view.Activity.MainActivity;
import com.minhvu.proandroid.sqlite.database.R;
import com.minhvu.proandroid.sqlite.database.models.data.NoteContract;
import com.minhvu.proandroid.sqlite.database.models.entity.Color;

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
                mCursorData.getString(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_TITLE)));
        holder.tvContent.setText(
                mCursorData.getString(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_CONTENT)));
        String tgCreationOn  = DateTimeUtils.longToStringDate(Long.parseLong(
                mCursorData.getString(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_DATE_CREATED))));
        holder.tvCreationOn.setText(tgCreationOn);
        String tgLastUpdate = DateTimeUtils.longToStringDate(Long.parseLong(
                mCursorData.getString(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_LAST_ON))));
        holder.tvLastUpdate.setText(tgLastUpdate);

        int visible = View.GONE;
        int posColor = mCursorData.getInt(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_COLOR));
        Color color = Color.getColor(mContext, posColor);
        if (notePin(position)) {
            visible = View.VISIBLE;
            holder.imgViewPinTag.setColorFilter(color.getHeaderColor());
        }
        holder.imgViewPinTag.setVisibility(visible);
        holder.bg.setBackgroundColor(color.getBackgroundColor());
        holder.viewBG.setBackgroundColor(color.getHeaderColor());
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



    private boolean notePin(int position){
        SharedPreferences preferences =
                mContext.getSharedPreferences(mContext.getString(R.string.PREFS_ALARM_FILE), Context.MODE_PRIVATE);
        String id = mContext.getString(R.string.PREFS_ALARM_SWITCH_KEY) + position;
        String switchType = preferences.getString(id, "");
        if(switchType.trim().equals("scPin")){
            return true;
        }
        return false;
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
            int bookID = mCursorData.getInt(mCursorData.getColumnIndex(NoteContract.NoteEntry._ID));
            mClickHandler.onClick(bookID);
        }


        @Override
        public boolean onLongClick(View v) {
            mCursorData.moveToPosition(getAdapterPosition());
            int bookID = mCursorData.getInt(mCursorData.getColumnIndex(NoteContract.NoteEntry._ID));
            String title = mCursorData.getString(mCursorData.getColumnIndex(NoteContract.NoteEntry.COL_TITLE));
            int pin =notePin(getAdapterPosition()) ? 1: 0;

            mClickHandler.onLongClick(itemView, bookID, title, pin);
            return true;
        }
    }
}
