package com.example.finant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class EditBudgetFragment extends Fragment {

    TextInputLayout menu, r2;

    String budgetId, budgetName;

    AutoCompleteTextView currencyOptions, repeating;

    ArrayAdapter<String> adapterItems, adapterItems1;

    TextInputEditText etSelectDate, budgetname ,amount;

    Button btnedit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 =  inflater.inflate(R.layout.fragment_edit_budget, container, false);



        //display specific budget data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

//          Reference the Firestore collection where you want to store the data
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            budgetId = getArguments().getString("budgetId");

            if (budgetId == null || budgetId.isEmpty()) {
                Log.d("TAG", "budgetId is null or empty");
            }else{
                db.collection("budget").document(budgetId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                // Extract the data from the document
                                budgetName = documentSnapshot.getString("budget_name");
                                double Amount = documentSnapshot.getDouble("amount");
                                String Currency = documentSnapshot.getString("bcurrency");
                                String beginDate = documentSnapshot.getString("begin_date");
                                String repeatStatus = documentSnapshot.getString("repeat_status");

                                budgetname.setText(budgetName);
                                amount.setText(String.valueOf(Amount));
                                currencyOptions.setText(Currency);
                                etSelectDate.setText(beginDate);
                                repeating.setText(repeatStatus);

                                String[] currency = {"MYR - MALAYSIA","CNY - CHINA","WON - KOREA","USD - UNITED STATE","EU - EUROPE"};
                                adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.itemlisted,currency);
                                currencyOptions.setAdapter(adapterItems);

                                String[] option = {"YES","NO"};
                                adapterItems1 = new ArrayAdapter<String>(getActivity(),R.layout.itemlisted,option);
                                repeating.setAdapter(adapterItems1);

                                Log.e("Fec", "Done success fet specific budget");
                            }
                        });
            }
        }


        //choose currency
        menu=v1.findViewById(R.id.menudrpdown);
        currencyOptions=v1.findViewById(R.id.currencyOpt);

        String[] currency = {"RM","YN","RP"};
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.itemlisted,currency);

        currencyOptions.setAdapter(adapterItems);

        currencyOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v1, int i, long l) {
                String currencies= adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getActivity(), "Currency: " + currencies, Toast.LENGTH_SHORT).show();
            }
        });




        //choose date
        etSelectDate = v1.findViewById(R.id.etSelectDate);

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
                        String date = String.format("%02d/%02d/%04d",1,month,year);
                        etSelectDate.setText(date);

                    }
                },year, month,day);
                dialog.show();

            }
        });




        //choose yes no
        r2=v1.findViewById(R.id.r2);
        repeating=v1.findViewById(R.id.repeating);

        String[] option = {"YES","NO"};
        adapterItems1 = new ArrayAdapter<String>(getActivity(),R.layout.itemlisted,option);

        repeating.setAdapter(adapterItems1);

        repeating.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v2, int j, long l) {
                String options= adapterView.getItemAtPosition(j).toString();
                Toast.makeText(getActivity(), "Selection: " + options, Toast.LENGTH_SHORT).show();
            }
        });




        //input into firestore
        btnedit = v1.findViewById(R.id.btn_saveEditedBud);
        budgetname = v1.findViewById(R.id.budgetname);
        amount = v1.findViewById(R.id.amount);

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show an alert dialog to confirm the deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("BUDGET MODIFIED CONFIRMATION");
                builder.setMessage("Are you sure you want to save this modified budget?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String budgetNme = budgetname.getText().toString();
                        String amountBudget = amount.getText().toString();
                        String currencies = currencyOptions.getText().toString();
                        String beginDate = etSelectDate.getText().toString();
                        String Rstatus = repeating.getText().toString();

                        if(budgetNme.trim().length() == 0 || amountBudget.trim().length() == 0 || currencies.trim().length() == 0 || beginDate.trim().length() == 0 || Rstatus.trim().length() == 0) {
                            // Show an error message to the user
                            Log.w("INPUT", "NO DATA INPUT");
                            Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                        }else{
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference myCollectionRef = db.collection("budget");
                            DocumentReference dRef = myCollectionRef.document(budgetId);

                            // User is signed in, so get the user ID
                            String userId = user.getUid();
                            Query query = db.collection("budget").whereEqualTo("budget_name", budgetNme).whereEqualTo("user_ID", userId);

                            query.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    if (!snapshot.isEmpty()&&!budgetNme.equals(budgetName)) {
                                        // Reject write because budget name already exists
                                        Log.d("MAP", "Budget name " + budgetNme + " already exists in collection " + myCollectionRef);
                                        // Show an error message to the user
                                        Toast.makeText(getActivity(), "A budget with this name already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Set the data for the document
                                        double amountB = Double.parseDouble(amountBudget);

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("budget_name", budgetNme);
                                        data.put("amount", amountB);
                                        data.put("bcurrency", currencies);
                                        data.put("begin_date", beginDate);
                                        data.put("repeat_status", Rstatus);


                                        dRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "Budget updated successfully.");
                                                Toast.makeText(getActivity(), "Budget updated successfully.", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error updating document
                                                Log.w("TAG", "Error updating Budget", e);
                                            }
                                        });

                                        Intent intent = new Intent(getActivity(), BudgetService.class);
                                        getActivity().startService(intent);
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                builder.show();

            }
        });


        return v1;
    }
}