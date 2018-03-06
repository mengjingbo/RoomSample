package com.sample.room.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.room.R;
import com.sample.room.bean.PhoneBean;

import java.util.List;

/**
 * 作者：蒙景博
 * 时间：2017/11/16
 * 描述：
 */
public class PhoneListAdapter extends RecyclerView.Adapter<PhoneListAdapter.ViewHolder> {

    private List<PhoneBean> mPhoneList;
    private OnItemClickListener mListener;

    public PhoneListAdapter(List<PhoneBean> phoneBeans) {
        this.mPhoneList = phoneBeans;
        notifyItemChanged(phoneBeans.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_text, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mPhoneTx.setText(mPhoneList.get(position).getName() + ":" + mPhoneList.get(position).getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(mPhoneList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhoneList == null ? 0 : mPhoneList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(PhoneBean phoneBean);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mPhoneTx;

        private ViewHolder(View itemView) {
            super(itemView);
            mPhoneTx = itemView.findViewById(R.id.phone_number_text);
        }
    }
}
