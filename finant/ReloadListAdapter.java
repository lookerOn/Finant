package com.example.finant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReloadListAdapter extends RecyclerView.Adapter<ReloadListAdapter.MyViewHolder>{

    private List<RELOAD> reloadlist;

    public ReloadListAdapter(List<RELOAD> reloadlist) {
        this.reloadlist = reloadlist;

    }

    @NonNull
    @Override
    public ReloadListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reloadlist, parent, false);

        return new ReloadListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReloadListAdapter.MyViewHolder holder, int position) {

        RELOAD rld = reloadlist.get(position);

        holder.rdAmount.setText(String.valueOf(rld.getReload_Amount()));
        holder.rdCurrency.setText(String.valueOf(rld.getWallet_currency()));
        holder.rdDate.setText(rld.getDate());
        holder.rdTimeStamp.setText(rld.getTimeStamp());
        holder.rdid.setText(rld.getRid());

    }

    @Override
    public int getItemCount() {
        return reloadlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView rdAmount,rdCurrency, rdDate, rdTimeStamp, rdid;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rdAmount = itemView.findViewById(R.id.RDAM);
            rdCurrency = itemView.findViewById(R.id.RDCY);
            rdDate = itemView.findViewById(R.id.RDDT);
            rdTimeStamp = itemView.findViewById(R.id.RDTS);
            rdid = itemView.findViewById(R.id.RDID);
        }
    }
}
