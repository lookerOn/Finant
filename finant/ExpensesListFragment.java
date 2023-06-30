package com.example.finant;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExpensesListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Expenses> expArrayList;
    MyExpenseAdapter myAdapter1;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 =  inflater.inflate(R.layout.fragment_expenses_list, container, false);

        // Fetch expenses List
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FETCHING DATA...");
        progressDialog.show();

        recyclerView = v1.findViewById(R.id.recyclerExp);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //fetch firestore data
        db = FirebaseFirestore.getInstance();
        expArrayList = new ArrayList<Expenses>();
        myAdapter1 = new MyExpenseAdapter(getContext(),expArrayList);

        recyclerView.setAdapter(myAdapter1);

        TextView budgetNAME = v1.findViewById(R.id.budgetNME);
        //show progress bar
        ProgressBar pbu = v1.findViewById(R.id.budgetProgress1);

        //display specific budget data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

//          Reference the Firestore collection where you want to store the data
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String budgetId = getArguments().getString("budgetId");

            if (budgetId == null || budgetId.isEmpty()) {
                Log.d("TAG", "budgetId is null or empty");
            }else{
                db.collection("budget").document(budgetId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                // Extract the data from the document
                                String budgetName = documentSnapshot.getString("budget_name");
                                double Amount = documentSnapshot.getDouble("amount");
                                String Currency = documentSnapshot.getString("currency");
                                String beginDate = documentSnapshot.getString("begin_date");
                                String repeatStatus = documentSnapshot.getString("repeat_status");
                                double Usage = documentSnapshot.getDouble("usage");
                                budgetNAME.setText(budgetName);

                                // get progressbar
                                double budgetLimit = (Amount+Usage); // replace with your budget limit
                                double currentBudgetUsage = Usage; // replace with your current budget usage
                                pbu.setProgress((int) ((double) currentBudgetUsage / budgetLimit * 100));
                            }
                        });
            }
        }

        EventChangeListener();

        return v1;
    }

    private void EventChangeListener() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

//          Reference the Firestore collection where you want to store the data
            String budgetId = getArguments().getString("budgetId");

            if (budgetId == null || budgetId.isEmpty()) {
                Log.d("TAG", "budgetId is null or empty");
            } else {
                // Add the expense to the selected budget
                DocumentReference budgetRef = db.collection("budget").document(budgetId);
                budgetRef.collection("expenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error!=null){

                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }

                            Log.e("FireStore Error", error.getMessage());
                            return;
                        }

                        for(DocumentChange dc : value.getDocumentChanges()){

                            if(dc.getType() == DocumentChange.Type.ADDED){
                                expArrayList.add(dc.getDocument().toObject(Expenses.class));
                            }
                        }

                        // Sort the miscList array
                        Collections.sort(expArrayList, new Comparator<Expenses>() {
                            @Override
                            public int compare(Expenses e1, Expenses e2) {
                                // Get the year, month, and day values from the begin_date field
                                String[] parts1 = e1.EXP_date.split("/");
                                int year1 = Integer.parseInt(parts1[0]);
                                int month1 = Integer.parseInt(parts1[1]);
                                int day1 = Integer.parseInt(parts1[2]);

                                String[] parts2 = e2.EXP_date.split("/");
                                int year2 = Integer.parseInt(parts2[0]);
                                int month2 = Integer.parseInt(parts2[1]);
                                int day2 = Integer.parseInt(parts2[2]);

                                // Compare the year values first
                                if (year1 > year2) {
                                    return -1;
                                } else if (year1 < year2) {
                                    return 1;
                                } else {
                                    // If the year values are equal, compare the month values
                                    if (month1 > month2) {
                                        return -1;
                                    } else if (month1 < month2) {
                                        return 1;
                                    } else {
                                        // If the month values are equal, compare the day values
                                        if (day1 > day2) {
                                            return -1;
                                        } else if (day1 < day2) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                }
                            }
                        });


                        // check if adapter has any items
                        if(myAdapter1.getItemCount() == 0){
                            Toast.makeText(getActivity(), "No Expenses Found", Toast.LENGTH_SHORT).show();
                        }

                        myAdapter1.notifyDataSetChanged();

                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}