package com.example.finant;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyMISCAdapter extends RecyclerView.Adapter<MyMISCAdapter.MyViewHolder>{

    Context context;
    ArrayList<MISC> miscList;

    public MyMISCAdapter(Context context, ArrayList<MISC> miscList) {
        this.context = context;
        this.miscList = miscList;
    }

    @NonNull
    @Override
    public MyMISCAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemisc,parent,false);

        return new MyMISCAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMISCAdapter.MyViewHolder holder, int position) {

        MISC misc = miscList.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        double formattedAmount = Double.parseDouble(df.format(misc.usage));
        double formattedAmount2 = Double.parseDouble(df.format(misc.amount));

        holder.MIamt.setText(String.valueOf(formattedAmount2));
        holder.MIbgd.setText(misc.begin_date);
        holder.MIusg.setText(String.valueOf(formattedAmount));

        // get progressbar
        double budgetLimit = (misc.getAmount()+misc.getUsage()); // replace with your budget limit
        double currentBudgetUsage = misc.getUsage(); // replace with your current budget usage
        holder.pbu.setProgress((int) ((double) currentBudgetUsage / budgetLimit * 100));


        holder.vieweXP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for vibration
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(80);

                // Handle edit action here
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    Log.e("MISCExpensesList", "Invalid position");
                    return;
                }

                String mid = miscList.get(position).getMid();
                if (mid == null) {
                    Log.e("MISCExpensesList", "MID ID not found");
                    return;
                }

                Fragment newFragment = new miscExpensesListFragment();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("mid", mid);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.viewtomisc, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return miscList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView MIamt,MIbgd,MIusg;
        CardView vieweXP1;
        ProgressBar pbu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

           MIamt = itemView.findViewById(R.id.MIAM);
           MIbgd = itemView.findViewById(R.id.MIBD);
           MIusg = itemView.findViewById(R.id.MIUS);
           pbu = itemView.findViewById(R.id.miscProgress);
           vieweXP1 = itemView.findViewById(R.id.miscview);
        }
    }
}
