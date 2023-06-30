package com.example.finant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyMCLISTAdapter extends RecyclerView.Adapter<MyMCLISTAdapter.MyViewHolder>{
    Context context;
    ArrayList<MISCLIST> MCList;

    public MyMCLISTAdapter(Context context, ArrayList<MISCLIST> MCList) {
        this.context = context;
        this.MCList = MCList;
    }

    @NonNull
    @Override
    public MyMCLISTAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemmisclisted,parent,false);

        return new MyMCLISTAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMCLISTAdapter.MyViewHolder holder, int position) {
        MISCLIST miscls = MCList.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        double formattedAmount = Double.parseDouble(df.format(miscls.MISCEXP_Amount));

        holder.mat.setText(String.valueOf(formattedAmount));
        holder.mdt.setText(miscls.MISCEXP_date);
        holder.mtm.setText(miscls.MISCtimeStamp);
        holder.mdes.setText(miscls.MISCEXP_Des);
    }

    @Override
    public int getItemCount() {
        return MCList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mat, mdt, mtm, mdes;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mat = itemView.findViewById(R.id.MIexpAM);
            mdt = itemView.findViewById(R.id.MIexpDT);
            mtm = itemView.findViewById(R.id.MIexpTM);
            mdes = itemView.findViewById(R.id.MIexpDES);
        }
    }
}
