package com.example.finant;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MiscFragment extends Fragment {
    TextInputEditText etSelectDate, amount;
    String begindate,currentDate,amountBudget,beginDate,userId;
    boolean foundMatchingBudget;
    Button msc;
    public MiscFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vv = inflater.inflate(R.layout.fragment_misc, container, false);

        //choose date
        etSelectDate = vv.findViewById(R.id.etSelectDate);

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etSelectDate.setInputType(InputType.TYPE_NULL);

        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month = month + 1;
                        String date = String.format("%02d/%02d/%04d", 1, month, year);
                        etSelectDate.setText(date);

                    }
                }, year, month, day);
                dialog.show();

            }
        });


        msc = vv.findViewById(R.id.btn_createMis);
        amount = vv.findViewById(R.id.amount);

        msc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    amountBudget = amount.getText().toString();
                    beginDate = etSelectDate.getText().toString();

                    if (amountBudget.trim().length() == 0 || beginDate.trim().length() == 0) {
                        // Show an error message to the user
                        Log.w("INPUT", "NO DATA INPUT");
                        Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        // User is signed in, so get the user ID
                        userId = user.getUid();

                        // Reference the Firestore collection where you want to store the data
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // DocumentReference docRef = db.collection("user").document(userId);
                        CollectionReference myCollectionRef = db.collection("MISC");
                        Query query = myCollectionRef.whereEqualTo("user_ID", userId);

                        query.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                foundMatchingBudget = false;
                                if (!snapshot.isEmpty()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        begindate = document.getString("begin_date");

                                        //get budget month
                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        Date BeginDate;
                                        try {
                                            BeginDate = dateFormat.parse(begindate);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Calendar cal6 = Calendar.getInstance();
                                        cal6.setTime(BeginDate);
                                        int currentbudgetmonth = cal6.get(Calendar.MONTH);
                                        int currentbudgetyear = cal6.get(Calendar.YEAR);

                                        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                        Date curDate;
                                        try {
                                            curDate = dateFormat2.parse(beginDate);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Calendar cal7 = Calendar.getInstance();
                                        cal7.setTime(curDate);
                                        int currentmonth = cal7.get(Calendar.MONTH);
                                        int currentyear = cal7.get(Calendar.YEAR);

                                        if (currentbudgetmonth == currentmonth && currentbudgetyear == currentyear) {
                                            foundMatchingBudget = true;
                                            break;
                                        }
                                    }

                                    if (foundMatchingBudget) {
                                        // Reject write because budget name already exists
                                        Log.d("MAP", "MISC ONLY ALLOWED CREATED ONE TIMES PER MONTH ");
                                        // Show an error message to the user
                                        Toast.makeText(getActivity(), "MISC ONLY ALLOWED CREATED ONE TIMES PER MONTH", Toast.LENGTH_SHORT).show();
                                    } else {
                                        double amountB = Double.parseDouble(amountBudget);

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("amount", amountB);
                                        data.put("begin_date", beginDate);
                                        data.put("user_ID",userId);
                                        data.put("usage",0.00);

                                        // DocumentReference docRef = db.collection("user").document(userId);
                                        CollectionReference myCollectionRef2 = db.collection("MISC");

                                        myCollectionRef2.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference newDocRef) {
                                                Log.d("MAP", "New MISC budget added with ID: " + newDocRef.getId());
                                                Toast.makeText(getActivity(), "MISC Budget Successfully Created", Toast.LENGTH_SHORT).show();

                                                // Update the document with the budget code
                                                newDocRef.update("Mid", newDocRef.getId())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "Mid updated successfully.");

                                                                // Clear the input fields
                                                                amount.setText("");
                                                                etSelectDate.setText("");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("TAG", "Error updating Mid.", e);
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("MAP", "Error adding new document", e);
                                                Toast.makeText(getActivity(), "MISC Budget Failed To Created", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    double amountB = Double.parseDouble(amountBudget);

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("amount", amountB);
                                    data.put("begin_date", beginDate);
                                    data.put("user_ID",userId);
                                    data.put("usage",0.00);

                                    // DocumentReference docRef = db.collection("user").document(userId);
                                    CollectionReference myCollectionRef2 = db.collection("MISC");

                                    myCollectionRef2.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference newDocRef) {
                                            Log.d("MAP", "New MISC budget added with ID: " + newDocRef.getId());
                                            Toast.makeText(getActivity(), "MISC Budget Successfully Created", Toast.LENGTH_SHORT).show();

                                            // Update the document with the budget code
                                            newDocRef.update("Mid", newDocRef.getId())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("TAG", "Mid updated successfully.");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("TAG", "Error updating Mid.", e);
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("MAP", "Error adding new document", e);
                                            Toast.makeText(getActivity(), "MISC Budget Failed To Created", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.e("QUERY", "Error querying collection " + myCollectionRef, task.getException());
                                // Show an error message to the user
                                Toast.makeText(getActivity(), "Error querying collection", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            }
        });

        return vv;
    }
}