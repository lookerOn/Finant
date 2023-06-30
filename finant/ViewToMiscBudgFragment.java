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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewToMiscBudgFragment extends Fragment {
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    ArrayList<MISC> miscList;
    MyMISCAdapter myAdapterM;

    RecyclerView recyclerView;
    public ViewToMiscBudgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_to_misc_budg, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FETCHING DATA...");
        progressDialog.show();

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //fetch firestore data
        db = FirebaseFirestore.getInstance();
        miscList = new ArrayList<MISC>();

        myAdapterM = new MyMISCAdapter(getContext(),miscList);
        recyclerView.setAdapter(myAdapterM);
        EventChangeListener();

        return v;
    }
    private void EventChangeListener(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        db.collection("MISC").whereEqualTo("user_ID", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        miscList.add(dc.getDocument().toObject(MISC.class));
                    }
                }

                // Sort the miscList array
                Collections.sort(miscList, new Comparator<MISC>() {
                    @Override
                    public int compare(MISC m1, MISC m2) {
                        // Get the year and month values from the begin_date field
                        String[] parts1 = m1.begin_date.split("/");
                        int year1 = Integer.parseInt(parts1[0]);
                        int month1 = Integer.parseInt(parts1[1]);

                        String[] parts2 = m2.begin_date.split("/");
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
                if(myAdapterM.getItemCount() == 0){
                    Toast.makeText(getActivity(), "No Budget Found", Toast.LENGTH_SHORT).show();
                }

                myAdapterM.notifyDataSetChanged();

                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}