package com.example.finant;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddBudget extends Fragment {
    Button btnaddBud, BTNmc;
    RecyclerView recyclerView;
    ArrayList<Budget> budgetAryList;
    MyAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v1 = inflater.inflate(R.layout.fragment_add_budget, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FETCHING DATA...");
        progressDialog.show();

        recyclerView = v1.findViewById(R.id.recycler1);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        //fetch firestore data
        db = FirebaseFirestore.getInstance();
        budgetAryList = new ArrayList<Budget>();
        myAdapter = new MyAdapter(getContext(),budgetAryList);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();


        // create budget
        btnaddBud=v1.findViewById(R.id.btn_addBudGET);

        btnaddBud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment secondFrag = new BudgetFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.addBudpage, secondFrag).addToBackStack(null).commit();

            }
        });

        BTNmc = v1.findViewById(R.id.btn_VIEWMISC);

        BTNmc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment thirdFrag = new ViewToMiscBudgFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.addBudpage, thirdFrag).addToBackStack(null).commit();
            }
        });

        // Inflate the layout for this fragment
        return v1;
    }

    private void EventChangeListener(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        db.collection("budget").whereEqualTo("user_ID", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        budgetAryList.add(dc.getDocument().toObject(Budget.class));
                    }
                }

                // Sort the miscList array
                Collections.sort(budgetAryList, new Comparator<Budget>() {
                    @Override
                    public int compare(Budget b1, Budget b2) {
                        // Get the year and month values from the begin_date field
                        String[] parts1 = b1.begin_date.split("/");
                        int year1 = Integer.parseInt(parts1[0]);
                        int month1 = Integer.parseInt(parts1[1]);

                        String[] parts2 = b2.begin_date.split("/");
                        int year2 = Integer.parseInt(parts2[0]);
                        int month2 = Integer.parseInt(parts2[1]);

                        // Compare the year values first
                        if (year1 < year2) {
                            return 1;
                        } else if (year1 > year2) {
                            return -1;
                        } else {
                            // If the year values are equal, compare the month values
                            if (month1 < month2) {
                                return 1;
                            } else if (month1 > month2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
                });

                // check if adapter has any items
                if(myAdapter.getItemCount() == 0){
                    Toast.makeText(getActivity(), "No Budget Found", Toast.LENGTH_SHORT).show();
                }

                myAdapter.notifyDataSetChanged();

                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}