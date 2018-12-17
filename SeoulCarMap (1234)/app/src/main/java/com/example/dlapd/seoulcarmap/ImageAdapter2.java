package com.example.dlapd.seoulcarmap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageAdapter2 extends RecyclerView.Adapter<ImageAdapter2.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter2(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    private String searchString = "";

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item2, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewName2.setText(uploadCurrent.getName());
        holder.textViewAddress2.setText(uploadCurrent.getParkingAddress());
        holder.textViewTime2.setText(uploadCurrent.getParkingTimeStart() + uploadCurrent.getParkingTimeFinish());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.nophoto3)
                .fit()
                .centerCrop()
                .into(holder.imageView2);

        String name = uploadCurrent.getName().toLowerCase(Locale.getDefault());
        String address = uploadCurrent.getParkingAddress().toLowerCase(Locale.getDefault());
        String time = (uploadCurrent.getParkingTimeStart() + uploadCurrent.getParkingTimeFinish()).toLowerCase(Locale.getDefault());

        if (name.contains(searchString)) {

            int startPos = name.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.textViewName2.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.textViewName2.setText(spanString);
        }

        if (address.contains(searchString)) {

            int startPos = address.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.textViewAddress2.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.textViewAddress2.setText(spanString);
        }

        if (time.contains(searchString)) {

            int startPos = time.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.textViewTime2.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.textViewTime2.setText(spanString);
        }


//        final SpannableStringBuilder sp = new SpannableStringBuilder(holder.textViewName.getText());
////sp.setSpan(new ForegroundColorSpan(Color.rgb(255, 255, 255)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new ForegroundColorSpan(Color.RED), searchString.indexOf(searchString), searchString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.textViewName.setText(sp);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName2;
        public TextView textViewAddress2;
        public TextView textViewTime2;
        public ImageView imageView2;


        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName2 = itemView.findViewById(R.id.text_view_name2);
            textViewAddress2 = itemView.findViewById(R.id.text_view_address2);
            textViewTime2 = itemView.findViewById(R.id.text_view_time2);
            imageView2 = itemView.findViewById(R.id.image_view_upload2);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "삭제");

            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void updateList(List<Upload> newList, String searchString) {

        mUploads = new ArrayList<>();
        mUploads.addAll(newList);
        this.searchString = searchString;
        notifyDataSetChanged();

    }


}