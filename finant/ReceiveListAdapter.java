package com.example.finant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReceiveListAdapter extends RecyclerView.Adapter<ReceiveListAdapter.MyViewHolder>{

    private List<RECEIVE> receivelist;

    public ReceiveListAdapter(List<RECEIVE> receivelist) {
        this.receivelist = receivelist;

    }

    @NonNull
    @Override
    public ReceiveListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivedlist, parent, false);

        return new ReceiveListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveListAdapter.MyViewHolder holder, int position) {
        RECEIVE rcv = receivelist.get(position);

        holder.rcvAmount.setText(String.valueOf(rcv.getTransfer_Amount()));
        holder.rcvCurrency.setText(rcv.getWallet_currency());
        holder.rcvDate.setText(rcv.getDate());
        holder.rcvTimeStamp.setText(rcv.getTimeStamp());
        holder.rcvID.setText(rcv.getTid());
        holder.rcvsenderid.setText(rcv.getSender_user_ID());
    }

    @Override
    public int getItemCount() {
        return receivelist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rcvID,rcvCurrency,rcvDate,rcvTimeStamp,rcvsenderid,rcvAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rcvID = itemView.findViewById(R.id.RCVID);
            rcvCurrency = itemView.findViewById(R.id.RCVCY);
            rcvDate = itemView.findViewById(R.id.RCVDT);
            rcvTimeStamp = itemView.findViewById(R.id.RCVTS);
            rcvsenderid = itemView.findViewById(R.id.SDID);
            rcvAmount = itemView.findViewById(R.id.RCVAM);
        }
    }
}
