package com.example.finant;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class BudgetFragment extends Fragment {

    TextInputLayout menu, r2;

    AutoCompleteTextView currencyOptions, repeating;

    ArrayAdapter<String> adapterItems, adapterItems1;

    TextInputEditText etSelectDate, budgetname ,amount;

    String nextBudgetName, bbidd;

    TextView misc;

    Button btncreate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 =  inflater.inflate(R.layout.fragment_budget, container, false);

        //choose currency
        menu=v1.findViewById(R.id.menudrpdown);
        currencyOptions=v1.findViewById(R.id.currencyOpt);

        String[] currency = {"MYR - MALAYSIA","CNY - CHINA","WON - KOREA","USD - UNITED STATE","EU - EUROPE"};
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
        btncreate = v1.findViewById(R.id.btn_createBud);
        budgetname = v1.findViewById(R.id.budgetname);
        amount = v1.findViewById(R.id.amount);

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    String budgetName = budgetname.getText().toString();
                    String amountBudget = amount.getText().toString();
                    String currencies = currencyOptions.getText().toString();
                    String beginDate = etSelectDate.getText().toString();
                    String Rstatus = repeating.getText().toString();

                    if(budgetName.trim().length() == 0 || amountBudget.trim().length() == 0 || currencies.trim().length() == 0 || beginDate.trim().length() == 0 || Rstatus.trim().length() == 0) {
                        // Show an error message to the user
                        Log.w("INPUT", "NO DATA INPUT");
                        Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    }else{
                        // User is signed in, so get the user ID
                        String userId = user.getUid();

                        // Reference the Firestore collection where you want to store the data
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // DocumentReference docRef = db.collection("user").document(userId);
                        CollectionReference myCollectionRef = db.collection("budget");
                        Query query = db.collection("budget").whereEqualTo("budget_name", budgetName).whereEqualTo("user_ID", userId);

                        query.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (!snapshot.isEmpty()) {
                                    // Reject write because budget name already exists
                                    Log.d("MAP", "Budget name " + budgetName + " already exists in collection " + myCollectionRef);
                                    // Show an error message to the user
                                    Toast.makeText(getActivity(), "A budget with this name already exists", Toast.LENGTH_SHORT).show();
                                }else {
                                    // Write data to Firestore because budget name does not exist
//                                 Set the data for the document
                                    double amountB = Double.parseDouble(amountBudget);

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("budget_name", budgetName);
                                    data.put("amount", amountB);
                                    data.put("bcurrency", currencies);
                                    data.put("begin_date", beginDate);
                                    data.put("repeat_status", Rstatus);
                                    data.put("user_ID",userId);
                                    data.put("usage",0.00);
                                    data.put("counter",1);

                                    myCollectionRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference newDocRef) {
                                            Log.d("MAP", "New budget added with ID: " + newDocRef.getId());
                                            Toast.makeText(getActivity(), "Budget Successfully Created", Toast.LENGTH_SHORT).show();

                                            // Update the document with the budget code
                                            newDocRef.update("bid", newDocRef.getId())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("TAG", "Bid updated successfully.");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("TAG", "Error updating Bid.", e);
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("MAP", "Error adding new document", e);
                                            Toast.makeText(getActivity(), "Budget Failed To Created", Toast.LENGTH_SHORT).show();
                                        }
                                    });

//
                                }
                            }else {
                                Log.e("QUERY", "Error querying collection " + myCollectionRef, task.getException());
                                // Show an error message to the user
                                Toast.makeText(getActivity(), "Error querying collection", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(getActivity(), BudgetService.class);
//                        intent.putExtra("budget_name", budgetName);
//                        intent.putExtra("repeat_status", Rstatus);
                        getActivity().startService(intent);

                    }
                }
            }
        });


        //only allow one misc budget each month, only include amount, and begin_date, usage, Mid, userid
        misc = v1.findViewById(R.id.btn_emergency);

        misc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                budgetname.setVisibility(View.GONE);
                amount.setVisibility(View.GONE);
                menu.setVisibility(View.GONE);
                etSelectDate.setVisibility(View.GONE);
                repeating.setVisibility(View.GONE);
                btncreate.setVisibility(View.GONE);

                Fragment newFragment = new MiscFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.createbdg, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v1;
    }
}