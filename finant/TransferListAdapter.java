package com.example.finant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransferListAdapter extends RecyclerView.Adapter<TransferListAdapter.MyViewHolder>{

    private List<TRANSFER> transferlist;

    public TransferListAdapter(List<TRANSFER> transferlist) {
        this.transferlist = transferlist;

    }

    @NonNull
    @Override
    public TransferListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transferlist, parent, false);

        return new TransferListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferListAdapter.MyViewHolder holder, int position) {
        TRANSFER trn = transferlist.get(position);

        holder.trAmount.setText(String.valueOf(trn.getTransfer_Amount()));
        holder.trCurrency.setText(trn.getWallet_currency());
        holder.trDate.setText(trn.getDate());
        holder.trTimeStamp.setText(trn.getTimeStamp());
        holder.trID.setText(trn.getTid());
        holder.trrecieverid.setText(trn.getReceiver_ID());
    }

    @Override
    public int getItemCount() {
        return transferlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView trID, trCurrency, trDate, trTimeStamp, trrecieverid, trAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            trID = itemView.findViewById(R.id.TRID);
            trCurrency = itemView.findViewById(R.id.TRCY);
            trDate = itemView.findViewById(R.id.TRDT);
            trTimeStamp = itemView.findViewById(R.id.TRTS);
            trrecieverid = itemView.findViewById(R.id.TRRID);
            trAmount = itemView.findViewById(R.id.TRAM);
        }
    }
}
