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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyExpenseAdapter extends RecyclerView.Adapter<MyExpenseAdapter.MyViewHolder> {

    Context context;
    ArrayList<Expenses> expArrayList;
    String budgetId;
    double budgetAmount, backAmount;

    public MyExpenseAdapter(Context context, ArrayList<Expenses> expArrayList) {
        this.context = context;
        this.expArrayList = expArrayList;
    }

    @NonNull
    @Override
    public MyExpenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_expense,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyExpenseAdapter.MyViewHolder holder, int position) {

        Expenses expense = expArrayList.get(position);

        holder.expenseAmount.setText(String.valueOf(expense.EXP_Amount));
        holder.expenseCVAmount.setText(String.valueOf(expense.EXPCV_Amount));
        holder.expenseCurrency.setText(expense.currency);
        holder.expenseDate.setText(expense.EXP_date);
        holder.expenseDes.setText(expense.EXP_Des);

        Log.e("TAG","expArrayList size: " + expArrayList.size());

        holder.deleteE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the document ID of the expense you want to delete
                int position = holder.getAdapterPosition();
                Log.e("TAG","POSITION :" + position);
                final String expId = expArrayList.get(position).getEid();
                Log.e("TAG","expId :" + expId);
                String budgetId = expArrayList.get(position).getBid();
                Log.e("TAG","budId :" + budgetId);
                double exAm = expArrayList.get(position).getEXPCV_Amount();
                Log.e("TAG","ExpensesAmount :" + exAm);

                // Get a reference to the Firestore collection that contains the expense
                CollectionReference expensesRef = FirebaseFirestore.getInstance().collection("budget").document(budgetId).collection("expenses");
                DocumentReference expRef = expensesRef.document(expId);

                // Show an alert dialog to confirm the deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Expense Delete Confirmation");
                builder.setMessage("Are you sure you want to delete this expense?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Amount will be added back to budget
                        // perform calculation algorithm

                        // Reference the Firestore collection where you want to store the data
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference budgetsRef = db.collection("budget");
                        Query query = budgetsRef.whereEqualTo("bid", budgetId);

                        // Get the budget document that the expense belongs to
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Get the current amount & usage of the budget
                                        double budgetAmount = document.getDouble("amount");
                                        double budgetUsage = document.getDouble("usage");

                                        // Calculate the new amount of the budget
                                        double newBudgetAmount = budgetAmount + exAm;
                                        double newbudgetUsage = budgetUsage - exAm;

                                        // Update the budget document with the new amount
                                        DocumentReference budgetRef = budgetsRef.document(document.getId());
                                        budgetRef.update("amount", newBudgetAmount,"usage",newbudgetUsage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "Budget updated successfully.");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error updating budget amount", e);
                                            }
                                        });
                                    }
                                } else {
                                    Log.d("TAG", "Error getting budget document: ", task.getException());
                                }
                            }
                        });

                        // Delete the expense document
                        expRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Document successfully deleted
                                        // Remove the deleted expense from the list
                                        expArrayList.remove(position);

                                        Log.d("TAG", "Expense successfully deleted!");
                                        Toast.makeText(context.getApplicationContext(), "Expense successfully deleted!", Toast.LENGTH_SHORT).show();

                                        // Notify the adapter that the data set has changed
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that occur during the deletion process
                                        Log.w("TAG", "Error deleting expense", e);
                                        Toast.makeText(context.getApplicationContext(), "Error deleting expense", Toast.LENGTH_SHORT).show();
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return expArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView expenseAmount, expenseDate, expenseDes, expenseCurrency, expenseCVAmount;

        ImageView deleteE;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            expenseAmount = itemView.findViewById(R.id.expAM);
            expenseCurrency = itemView.findViewById(R.id.expCY);
            expenseDate = itemView.findViewById(R.id.expDT);
            expenseDes = itemView.findViewById(R.id.expDE);
            deleteE = itemView.findViewById(R.id.deleteExp);
            expenseCVAmount = itemView.findViewById(R.id.expAMCV);

        }
    }
}
