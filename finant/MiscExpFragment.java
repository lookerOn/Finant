package com.example.finant;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MiscExpFragment extends Fragment {
    TextInputEditText etSelectDate, MISCExpensesDes, MISCExpAmount;
    double MISCAmount,formattedAmount,MISCusage=0.00;
    boolean foundMatchingBudget;
    FirebaseFirestore db;
    Button btnexpmisc;
    String userId, MAmount, MExDate, MExDES,MISCDate,currentTime,mid;
    public MiscExpFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v4 = inflater.inflate(R.layout.fragment_misc_exp, container, false);


        etSelectDate = v4.findViewById(R.id.etSelectDate);
        btnexpmisc = v4.findViewById(R.id.btn_createMis);
        MISCExpensesDes = v4.findViewById(R.id.MiscDes);
        MISCExpAmount = v4.findViewById(R.id.amount);


        //choose date
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etSelectDate.setInputType(InputType.TYPE_NULL);

        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month = month+1;
                        String date = String.format("%02d/%02d/%04d",dayOfMonth,month,year);
                        etSelectDate.setText(date);

                    }
                },year, month,day);
                dialog.show();

            }
        });

        btnexpmisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userId = user.getUid();

                if (user != null) {

                    MAmount = MISCExpAmount.getText().toString();
                    MExDate = etSelectDate.getText().toString();
                    MExDES = MISCExpensesDes.getText().toString();

                    if (MAmount.trim().length() == 0 || MExDate.trim().length() == 0 || MExDES.trim().length() == 0) {
                        Log.w("INPUT", "NO DATA INPUT");
                        Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        db = FirebaseFirestore.getInstance();
                        CollectionReference budgetssRef = db.collection("MISC");
                        Query query = budgetssRef.whereEqualTo("user_ID", userId);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    foundMatchingBudget = false;
                                    if (!snapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            MISCDate = document.getString("begin_date");
                                            mid = document.getString("Mid");

                                            //get MISCbudget month
                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            Date BeginDate;
                                            try {
                                                BeginDate = dateFormat.parse(MISCDate);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Calendar cal6 = Calendar.getInstance();
                                            cal6.setTime(BeginDate);
                                            int currentbudgetmonth = cal6.get(Calendar.MONTH);
                                            int currentbudgetyear = cal6.get(Calendar.YEAR);


                                            ///get input month
                                            DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                                            Date ExpenseDate;
                                            try {
                                                ExpenseDate = dateFormat1.parse(MExDate);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Calendar cal9 = Calendar.getInstance();
                                            cal9.setTime(ExpenseDate);
                                            int currentinputmonth = cal9.get(Calendar.MONTH);
                                            int currentinputyear = cal9.get(Calendar.YEAR);


                                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                            currentTime = timeFormat.format(calendar.getTime());


                                            if (currentbudgetyear == currentinputyear && currentbudgetmonth == currentinputmonth) {
                                                foundMatchingBudget = true;
                                                break;

                                            }
                                        }

                                        if (foundMatchingBudget) {

                                            db = FirebaseFirestore.getInstance();
                                            CollectionReference Ref = db.collection("MISC");
                                            Query query11 = Ref.whereEqualTo("Mid", mid);
                                            query11.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document10 : task.getResult()) {
                                                            double AMTvalue = document10.getDouble("amount");
                                                            Log.d("Field value:", "Field value: " + AMTvalue);
                                                            double USGvalue = document10.getDouble("usage");
                                                            Log.d("Field value:", "Field value: " + USGvalue);

                                                            double amountB = Double.parseDouble(MAmount);

                                                            if (amountB <= AMTvalue) {
                                                                DecimalFormat df = new DecimalFormat("#.##");
                                                                formattedAmount = Double.parseDouble(df.format(amountB));

                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("MISCEXP_Amount", formattedAmount);
                                                                data.put("currency", "MYR - MALAYSIA");
                                                                data.put("MISCEXP_date", MExDate);
                                                                data.put("MISCEXP_Des", MExDES);
                                                                data.put("user_ID", userId);
                                                                data.put("Mid", mid);
                                                                data.put("MISCtimeStamp", currentTime);

                                                                db = FirebaseFirestore.getInstance();
                                                                DocumentReference budgetRef = db.collection("MISC").document(mid);
                                                                budgetRef.collection("MISCexpenses")
                                                                        .add(data)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Toast.makeText(getActivity(), "Expense added to MISC", Toast.LENGTH_SHORT).show();
                                                                                Log.w("DONE ADDED", "Expense added to MISC");

                                                                                double temp = formattedAmount;

                                                                                double amtLeft = AMTvalue - temp;

                                                                                double ttlusage = USGvalue + temp;

                                                                                budgetRef.update("amount", amtLeft, "usage", ttlusage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("AMOUNT AFTER EXPENSE", String.valueOf(amtLeft));
                                                                                        Log.d("USAGE AFTER EXPENSE", String.valueOf(ttlusage));

//                                                                              Update the document with the budget code
                                                                                        documentReference.update("MiscExpid", documentReference.getId())
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        Log.d("TAG", "MiscExpid updated successfully.");
                                                                                                        // Clear the input fields
                                                                                                        etSelectDate.setText("");
                                                                                                        MISCExpensesDes.setText("");
                                                                                                        MISCExpAmount.setText("");
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        Log.w("TAG", "Error updating MiscExpid.", e);
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w("FAILED ADD", "Error adding expense to budget", e);
                                                                                Toast.makeText(getActivity(), "Error adding expense to budget ", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                            } else {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                builder.setTitle("ALERT");
                                                                builder.setMessage("YOU ARE OUT OF MONEY! PLEASE BE CAUTION IN USING MONEY!!!");

                                                                // Set up the buttons
                                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        // Do something when the OK button is clicked
                                                                    }
                                                                });
                                                                // Create and show the alert dialog
                                                                AlertDialog dialog = builder.create();
                                                                dialog.show();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.w("NO DOCUMENTS FOUND", "No MISC.", task.getException());
                                            Toast.makeText(getActivity(), "No MISC", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.w("error", "Error getting documents.", task.getException());
                                        Toast.makeText(getActivity(), "Error getting documents", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        return v4;
    }
}
