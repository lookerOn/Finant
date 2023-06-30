package com.example.finant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ChartExpenseAdapter extends RecyclerView.Adapter<ChartExpenseAdapter.ExpenseViewHolder> {

    private List<Expenses2forchart> expensesByMonthYear;

    public ChartExpenseAdapter(List<Expenses2forchart> expensesByMonthYear) {
        this.expensesByMonthYear = expensesByMonthYear;

        // Sort the list by month and year in ascending order
        Collections.sort(this.expensesByMonthYear, new Comparator<Expenses2forchart>() {
            @Override
            public int compare(Expenses2forchart o1, Expenses2forchart o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date d1 = sdf.parse(o1.EXP_date);
                    Date d2 = sdf.parse(o2.EXP_date);
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chartexpenseslist, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expenses2forchart expense = expensesByMonthYear.get(position);

        holder.expenseAmount.setText(String.valueOf(expense.EXP_Amount));
        holder.expenseCVAmount.setText(String.valueOf(expense.EXPCV_Amount));
        holder.expenseCurrency.setText(expense.currency);
        holder.expenseDate.setText(expense.EXP_date);
        holder.expenseDes.setText(expense.EXP_Des);

        Log.e("TAG","expArrayList size: " + expensesByMonthYear.size());
    }

    @Override
    public int getItemCount() {
        return expensesByMonthYear.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseAmount, expenseDate, expenseDes, expenseCurrency, expenseCVAmount;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            expenseAmount = itemView.findViewById(R.id.expAM);
            expenseCurrency = itemView.findViewById(R.id.expCY);
            expenseDate = itemView.findViewById(R.id.expDT);
            expenseDes = itemView.findViewById(R.id.expDE);
            expenseCVAmount = itemView.findViewById(R.id.expAMCV);
        }
    }
}
