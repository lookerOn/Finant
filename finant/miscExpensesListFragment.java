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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class miscExpensesListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<MISCLIST> MCList;
    MyMCLISTAdapter myMCLISTAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    public miscExpensesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v3 = inflater.inflate(R.layout.fragment_misc_expenses_list, container, false);

        // Fetch expenses List
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FETCHING DATA...");
        progressDialog.show();

        recyclerView = v3.findViewById(R.id.recyclerMISC);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //fetch firestore data
        db = FirebaseFirestore.getInstance();
        MCList = new ArrayList<MISCLIST>();
        myMCLISTAdapter = new MyMCLISTAdapter(getContext(),MCList);

        recyclerView.setAdapter(myMCLISTAdapter);

        TextView bgdate = v3.findViewById(R.id.begindte);
        //show progress bar
        ProgressBar pbuu = v3.findViewById(R.id.budgetProgress2);

        //display specific budget data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

//          Reference the Firestore collection where you want to store the data
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mid = getArguments().getString("mid");

            if (mid == null || mid.isEmpty()) {
                Log.d("TAG", "budgetId is null or empty");
            }else{
                db.collection("MISC").document(mid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                // Extract the data from the document
                                double Amount = documentSnapshot.getDouble("amount");
                                String beginDate = documentSnapshot.getString("begin_date");
                                double Usage = documentSnapshot.getDouble("usage");
                                bgdate.setText(beginDate);

                                // get progressbar
                                double budgetLimit = (Amount+Usage); // replace with your budget limit
                                double currentBudgetUsage = Usage; // replace with your current budget usage
                                pbuu.setProgress((int) ((double) currentBudgetUsage / budgetLimit * 100));
                            }
                        });
            }
        }

        EventChangeListener();

        return v3;
    }

    private void EventChangeListener() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

//          Reference the Firestore collection where you want to store the data
            String mid = getArguments().getString("mid");

            if (mid == null || mid.isEmpty()) {
                Log.d("TAG", "mId is null or empty");
            } else {
                // Add the expense to the selected budget
                DocumentReference budgetRef = db.collection("MISC").document(mid);
                budgetRef.collection("MISCexpenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                MCList.add(dc.getDocument().toObject(MISCLIST.class));
                            }
                        }


                        // Sort the expenses array
                        Collections.sort(MCList, new Comparator<MISCLIST>() {
                            @Override
                            public int compare(MISCLIST q1, MISCLIST q2) {
                                // Get the year, month, and day values from the MISCEXP_date field
                                String[] parts1 = q1.MISCEXP_date.split("/");
                                int year1 = Integer.parseInt(parts1[2]);
                                int month1 = Integer.parseInt(parts1[1]);
                                int day1 = Integer.parseInt(parts1[0]);

                                String[] parts2 = q2.MISCEXP_date.split("/");
                                int year2 = Integer.parseInt(parts2[2]);
                                int month2 = Integer.parseInt(parts2[1]);
                                int day2 = Integer.parseInt(parts2[0]);

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
                                            // If the day values are equal, compare the timestamps
                                            String[] timeParts1 = q1.MISCtimeStamp.split(":");
                                            int hour1 = Integer.parseInt(timeParts1[0]);
                                            int minute1 = Integer.parseInt(timeParts1[1]);
                                            int second1 = Integer.parseInt(timeParts1[2]);

                                            String[] timeParts2 = q2.MISCtimeStamp.split(":");
                                            int hour2 = Integer.parseInt(timeParts2[0]);
                                            int minute2 = Integer.parseInt(timeParts2[1]);
                                            int second2 = Integer.parseInt(timeParts2[2]);

                                            if (hour1 > hour2) {
                                                return -1;
                                            } else if (hour1 < hour2) {
                                                return 1;
                                            } else {
                                                if (minute1 > minute2) {
                                                    return -1;
                                                } else if (minute1 < minute2) {
                                                    return 1;
                                                } else {
                                                    if (second1 > second2) {
                                                        return -1;
                                                    } else if (second1 < second2) {
                                                        return 1;
                                                    } else {
                                                        return 0;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });




                        // check if adapter has any items
                        if(myMCLISTAdapter.getItemCount() == 0){
                            Toast.makeText(getActivity(), "No MISC Expenses Found", Toast.LENGTH_SHORT).show();
                        }

                        myMCLISTAdapter.notifyDataSetChanged();

                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}