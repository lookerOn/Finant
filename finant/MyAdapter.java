package com.example.finant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<Budget> budgetAryList;

    public MyAdapter(Context context, ArrayList<Budget> budgetAryList) {
        this.context = context;
        this.budgetAryList = budgetAryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Budget budget = budgetAryList.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        double formattedAmount = Double.parseDouble(df.format(budget.usage));
        double formattedAmount2 = Double.parseDouble(df.format(budget.amount));

        holder.budgetname.setText(budget.budget_name);
        holder.budgetamount.setText(String.valueOf(formattedAmount2));
        holder.budgetcurrency.setText(budget.bcurrency);
        holder.budgetusage.setText(String.valueOf(formattedAmount));

        // get progressbar
        double budgetLimit = (budget.getAmount()+budget.getUsage()); // replace with your budget limit
        double currentBudgetUsage = budget.getUsage(); // replace with your current budget usage
        holder.pbu.setProgress((int) ((double) currentBudgetUsage / budgetLimit * 100));

        holder.editB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for vibration
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(80);

                // Handle edit action here
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    Log.e("EditBudgetFragment", "Invalid position");
                    return;
                }

                String budgetId = budgetAryList.get(position).getBid();
                if (budgetId == null) {
                    Log.e("EditBudgetFragment", "Budget ID not found");
                    return;
                }

                Fragment newFragment = new EditBudgetFragment();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("budgetId", budgetId);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.addBudpage, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        holder.deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for vibration
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(80);

                // Get the document ID of the budget you want to delete
                int position = holder.getAdapterPosition();
                String budgetId = budgetAryList.get(position).getBid();

                // Get a reference to the Firestore collection that contains the budget
                CollectionReference budgetsRef = FirebaseFirestore.getInstance().collection("budget");

                if (budgetId != null) {
                    DocumentReference budgetRef = budgetsRef.document(budgetId);

                    // Show an alert dialog to confirm the deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("BUDGET DELETE CONFIRMATION");
                    builder.setMessage("Are you sure you want to delete this budget?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Call the delete() method on the document reference to delete the budget document
                            budgetRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document successfully deleted
                                            // Remove the deleted budget from the list
                                            budgetAryList.remove(position);

                                            Log.d("TAG", "Budget successfully deleted!");
                                            Toast.makeText(context.getApplicationContext(), "Budget successfully deleted!", Toast.LENGTH_SHORT).show();

                                            // Notify the adapter that the data set has changed
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors that occur during the deletion process
                                            Log.w("TAG", "Error deleting budget", e);
                                            Toast.makeText(context.getApplicationContext(), "Error deleting budget", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    });
                    builder.show();
                }else {
                    Log.e("TAG", "Invalid budget ID at position " + position);
                }
            }
        });

        holder.vieweXP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for vibration
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(80);

                // Handle edit action here
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    Log.e("ExpensesListFragment", "Invalid position");
                    return;
                }

                String budgetId = budgetAryList.get(position).getBid();
                if (budgetId == null) {
                    Log.e("ExpensesListFragment", "Budget ID not found");
                    return;
                }

                Fragment newFragment = new ExpensesListFragment();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("budgetId", budgetId);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.addBudpage, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetAryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView budgetname, budgetamount, budgetcurrency,budgetusage;
        ImageView editB,deleteB;
        CardView vieweXP;
        ProgressBar pbu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            budgetname = itemView.findViewById(R.id.budgetNM);
            budgetamount = itemView.findViewById(R.id.budgetAM);
            budgetcurrency = itemView.findViewById(R.id.budgetCY);
            budgetusage = itemView.findViewById(R.id.budgetUS);
            editB = itemView.findViewById(R.id.editBudget);
            deleteB = itemView.findViewById(R.id.deleteBudget);
            vieweXP = itemView.findViewById(R.id.budgetview);
            pbu = itemView.findViewById(R.id.budgetProgress);
        }

    }

}
