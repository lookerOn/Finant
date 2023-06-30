package com.example.finant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ViewTransHistFragment extends Fragment {

    RecyclerView recyclerView;
    TextView rd,tr,rcv;
    String userId,wid;


    public ViewTransHistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_trans_hist, container, false);

        recyclerView = v.findViewById(R.id.recycler);
        rd = v.findViewById(R.id.reloading);
        tr = v.findViewById(R.id.transfering);
        rcv = v.findViewById(R.id.receiving);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");
        wid = args.getString("wid");

        if(wid==null){
            Log.e("No wid passed","");
        }

        rd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<RELOAD> reloadlist = new ArrayList<>();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reloadRef = db.collection("e-wallet").document(wid);
                CollectionReference reloadref = reloadRef.collection("reload");

                reloadref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();

                        if (!snapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : snapshot) {
                                double amount = document.getDouble("Reload_Amount").floatValue();
                                String dtte = document.getString("date");
                                String rldid = document.getString("rid");
                                String timestamp = document.getString("timeStamp");
                                String wcurr = document.getString("wallet_currency");

                                reloadlist.add(new RELOAD(amount, dtte, rldid, timestamp, wcurr));
                            }

                            // Sort the reloadlist in descending order by year, month, and timestamp
                            Collections.sort(reloadlist, new Comparator<RELOAD>() {
                                @Override
                                public int compare(RELOAD r1, RELOAD r2) {
                                    // Parse the date and time strings to LocalDateTime objects
                                    LocalDateTime dt1 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt1 = LocalDateTime.parse(r1.getDate() + " " + r1.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }
                                    LocalDateTime dt2 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt2 = LocalDateTime.parse(r2.getDate() + " " + r2.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }

                                    // Compare the year in descending order
                                    int yearCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        yearCompare = dt2.getYear() - dt1.getYear();
                                    }
                                    if (yearCompare != 0) {
                                        return yearCompare;
                                    }

                                    // Compare the month in descending order
                                    int monthCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        monthCompare = dt2.getMonthValue() - dt1.getMonthValue();
                                    }
                                    if (monthCompare != 0) {
                                        return monthCompare;
                                    }

                                    // Compare the day in descending order
                                    int dayCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dayCompare = dt2.getDayOfMonth() - dt1.getDayOfMonth();
                                    }
                                    if (dayCompare != 0) {
                                        return dayCompare;
                                    }

                                    // Compare the timestamp in descending order
                                    return r2.getTimeStamp().compareTo(r1.getTimeStamp());
                                }
                            });

                            // Create and set up the RecyclerView adapter
                            ReloadListAdapter adapter = new ReloadListAdapter(reloadlist);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });


        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<TRANSFER> transferlist = new ArrayList<>();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reloadRef = db.collection("e-wallet").document(wid);
                CollectionReference reloadref = reloadRef.collection("transfer");

                reloadref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();

                        if (!snapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : snapshot) {
                                double amount = document.getDouble("Transfer_Amount").floatValue();
                                String dtre = document.getString("date");
                                String rceivid = document.getString("receiver_ID");
                                String trnid = document.getString("tid");
                                String timestamp = document.getString("timeStamp");
                                String wcurr = document.getString("wallet_currency");

                                transferlist.add(new TRANSFER(amount, dtre, rceivid, trnid, timestamp, wcurr));
                            }

                            // Sort the reloadlist in descending order by year, month, and timestamp
                            Collections.sort(transferlist, new Comparator<TRANSFER>() {
                                @Override
                                public int compare(TRANSFER t1, TRANSFER t2) {
                                    // Parse the date and time strings to LocalDateTime objects
                                    LocalDateTime dt1 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt1 = LocalDateTime.parse(t1.getDate() + " " + t1.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }
                                    LocalDateTime dt2 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt2 = LocalDateTime.parse(t2.getDate() + " " + t2.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }

                                    // Compare the year in descending order
                                    int yearCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        yearCompare = dt2.getYear() - dt1.getYear();
                                    }
                                    if (yearCompare != 0) {
                                        return yearCompare;
                                    }

                                    // Compare the month in descending order
                                    int monthCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        monthCompare = dt2.getMonthValue() - dt1.getMonthValue();
                                    }
                                    if (monthCompare != 0) {
                                        return monthCompare;
                                    }

                                    // Compare the day in descending order
                                    int dayCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dayCompare = dt2.getDayOfMonth() - dt1.getDayOfMonth();
                                    }
                                    if (dayCompare != 0) {
                                        return dayCompare;
                                    }

                                    // Compare the timestamp in descending order
                                    return t2.getTimeStamp().compareTo(t1.getTimeStamp());
                                }
                            });


                            // Create and set up the RecyclerView adapter
                            TransferListAdapter adapter = new TransferListAdapter(transferlist);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });

        rcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<RECEIVE> receivelist = new ArrayList<>();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reloadRef = db.collection("e-wallet").document(wid);
                CollectionReference reloadref = reloadRef.collection("receive");

                reloadref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();

                        if (!snapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : snapshot) {
                                double amount = document.getDouble("Transfer_Amount").floatValue();
                                String dtre = document.getString("date");
                                String senderid = document.getString("sender_user_ID");
                                String rcvid = document.getString("tid");
                                String timestamp = document.getString("timeStamp");
                                String wcurr = document.getString("wallet_currency");

                                receivelist.add(new RECEIVE(amount, dtre, senderid, rcvid, timestamp, wcurr));
                            }

                            // Sort the reloadlist in descending order by year, month, and timestamp
                            Collections.sort(receivelist, new Comparator<RECEIVE>() {
                                @Override
                                public int compare(RECEIVE v1, RECEIVE v2) {
                                    // Parse the date and time strings to LocalDateTime objects
                                    LocalDateTime dt1 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt1 = LocalDateTime.parse(v1.getDate() + " " + v1.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }
                                    LocalDateTime dt2 = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dt2 = LocalDateTime.parse(v2.getDate() + " " + v2.getTimeStamp(),
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                                    }

                                    // Compare the year in descending order
                                    int yearCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        yearCompare = dt2.getYear() - dt1.getYear();
                                    }
                                    if (yearCompare != 0) {
                                        return yearCompare;
                                    }

                                    // Compare the month in descending order
                                    int monthCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        monthCompare = dt2.getMonthValue() - dt1.getMonthValue();
                                    }
                                    if (monthCompare != 0) {
                                        return monthCompare;
                                    }

                                    // Compare the day in descending order
                                    int dayCompare = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        dayCompare = dt2.getDayOfMonth() - dt1.getDayOfMonth();
                                    }
                                    if (dayCompare != 0) {
                                        return dayCompare;
                                    }

                                    // Compare the timestamp in descending order
                                    return v2.getTimeStamp().compareTo(v1.getTimeStamp());
                                }
                            });

                            // Create and set up the RecyclerView adapter
                            ReceiveListAdapter adapter = new ReceiveListAdapter(receivelist);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });


        return v;
    }
}