package com.example.finant;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensesFragment extends Fragment {

    TextInputEditText etSelectDate, ExpensesDes ,Expenseamount;
    ArrayAdapter<String> adapterItems;
    AutoCompleteTextView currencyOptions,mBudgetDropdown;
    TextInputLayout menu;
    Button btnAddExpens;
    TextView misc;
    String budgid, budget_Name, budgetcurrency, currencies, budgetCur, repeatStatus, BeginDateString, begindate;

    List<Budget> mBudgets;
    ArrayAdapter<Budget> mAdapter;
    Budget selectedBudget;
    double budgetAmount, budgetUsage, amountEx, ConVamountEx = 0.0;
    String budgetId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 = inflater.inflate(R.layout.fragment_expenses, container, false);



        //choose currency
        menu=v1.findViewById(R.id.menudrpdown);
        currencyOptions=v1.findViewById(R.id.currencyOpt);

        String[] currency = {"MYR - MALAYSIA","CNY - CHINA","WON - KOREA","USD - UNITED STATE","EU - EUROPE"};
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.itemlisted,currency);

        currencyOptions.setAdapter(adapterItems);

        currencyOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v1, int i, long l) {
                currencies= adapterView.getItemAtPosition(i).toString();
                Log.d("currency selectted", currencies);
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
                        String date = String.format("%02d/%02d/%04d",dayOfMonth,month,year);
                        etSelectDate.setText(date);

                    }
                },year, month,day);
                dialog.show();

            }
        });

        //choose Budget
//      Initialize the AutoCompleteTextView
        mBudgetDropdown = v1.findViewById(R.id.BudgetOpt);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // User is signed in, so get the user ID
        String userId = user.getUid();

        // Reference the Firestore collection where you want to store the data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // DocumentReference docRef = db.collection("user").document(userId);
        CollectionReference myCollectionRef = db.collection("budget");
        Query query = db.collection("budget").whereEqualTo("user_ID", userId);

        // Retrieve the list of budgets from Firestore
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mBudgets = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Get the budget ID and name from the document
                    budget_Name = document.getString("budget_name");
                    budgid = document.getString("bid");
                    budgetCur = document.getString("bcurrency");
                    repeatStatus = document.getString("repeat_status");
                    BeginDateString = document.getString("begin_date");

                    //get current month
                    Calendar cal0 = Calendar.getInstance();
                    int currentmonth = cal0.get(Calendar.MONTH);

                    //get budget month
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date BeginDate;
                    try {
                        BeginDate = dateFormat.parse(BeginDateString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(BeginDate);

                    int currentmonthbudget = cal.get(Calendar.MONTH);

                    // Add the budget to the list-------------------------------------------------------
                    // Add the budget to the list if repeat_status is not "no"
                    if (currentmonth == currentmonthbudget) {
                        mBudgets.add(new Budget(budgid,budget_Name,budgetCur,BeginDateString));
                    }
                }

//              Set up the adapter for the AutoCompleteTextView
                mAdapter = new ArrayAdapter<Budget>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        mBudgets
                );
                mBudgetDropdown.setAdapter(mAdapter);
            }
        });

//      input into firestore
        btnAddExpens = v1.findViewById(R.id.btn_ADDExp);
        ExpensesDes = v1.findViewById(R.id.expenseDes);
        Expenseamount = v1.findViewById(R.id.expensAmnt);

        mBudgetDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the selected budget
                selectedBudget = mAdapter.getItem(i);
                Log.d("BUDGET_name", String.valueOf(selectedBudget));

                if (selectedBudget != null) {
                    // Get the budget ID of the selected budget
                    budgetId = selectedBudget.getBid();
                    Log.d("BUDGET_ID", budgetId);
                    budgetcurrency = selectedBudget.getBcurrency();
                    Log.d("Budget_Currency", budgetcurrency);
                    begindate = selectedBudget.getBegin_date();
                    Log.d("Budget_Date", String.valueOf(begindate));
                }
            }
        });

        //----
        btnAddExpens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (selectedBudget != null) {
                        budgetId = selectedBudget.getBid();
                        Log.d("BUDGET_ID", budgetId);
                        String budgetNM = selectedBudget.getBudget_name();
                        budgetcurrency = selectedBudget.getBcurrency();
                        Log.d("Budget_Currency", budgetcurrency);
                        begindate = selectedBudget.getBegin_date();
                        Log.d("Budget_Date", String.valueOf(begindate));

                        CollectionReference budgetssRef = db.collection("budget");
                        Query query = budgetssRef.whereEqualTo("bid", budgetId);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        budgetAmount = document.getDouble("amount");
                                        budgetUsage = document.getDouble("usage");
                                        Log.e("$$$$UNIQUEAmount$$$", String.valueOf(budgetAmount));
                                        Log.e("$$$$UNIQUEUsage$$$", String.valueOf(budgetUsage));

                                        String ExAmount = Expenseamount.getText().toString();
                                        currencies = currencyOptions.getText().toString();
                                        String ExDate = etSelectDate.getText().toString();
                                        String ExDES = ExpensesDes.getText().toString();

                                        if(ExAmount.trim().length() == 0 || currencies.trim().length() == 0 || ExDate.trim().length() == 0 || ExDES.trim().length() == 0 ) {
                                            Log.w("INPUT", "NO DATA INPUT");
                                            Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
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

                                            //get input month
                                            DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                                            Date ExpenseDate;
                                            try {
                                                ExpenseDate = dateFormat1.parse(ExDate);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Calendar cal9 = Calendar.getInstance();
                                            cal9.setTime(ExpenseDate);
                                            int currentinputmonth = cal9.get(Calendar.MONTH);

                                            if(currentinputmonth == currentbudgetmonth){
                                                String userId = user.getUid();
                                                amountEx = Double.parseDouble(ExAmount);

                                                if(budgetcurrency.equals("MYR - MALAYSIA")){
                                                    if(currencies.equals("MYR - MALAYSIA")){
                                                        ConVamountEx = amountEx;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("CNY - CHINA")){
                                                        ConVamountEx = amountEx * 0.65;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("WON - KOREA")){
                                                        ConVamountEx = amountEx * 0.0034;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("USD - UNITED STATE")){
                                                        ConVamountEx = amountEx * 4.49;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else{
                                                        ConVamountEx = amountEx * 4.83;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                }
                                                else if(budgetcurrency.equals("CNY - CHINA")){
                                                    if(currencies.equals("MYR - MALAYSIA")){
                                                        ConVamountEx = amountEx * 1.54;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("CNY - CHINA")){
                                                        ConVamountEx = amountEx;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("WON - KOREA")){
                                                        ConVamountEx = amountEx * 0.0053;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("USD - UNITED STATE")){
                                                        ConVamountEx = amountEx * 6.89;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else{
                                                        ConVamountEx = amountEx * 7.42;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                }
                                                else if(budgetcurrency.equals("WON - KOREA")){
                                                    if(currencies.equals("MYR - MALAYSIA")){
                                                        ConVamountEx = amountEx * 291.78;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("CNY - CHINA")){
                                                        ConVamountEx = amountEx * 190.03;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("WON - KOREA")){
                                                        ConVamountEx = amountEx;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("USD - UNITED STATE")){
                                                        ConVamountEx = amountEx * 1308.75;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else{
                                                        ConVamountEx = amountEx * 1410.24;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                }
                                                else if(budgetcurrency.equals("USD - UNITED STATE")){
                                                    if(currencies.equals("MYR - MALAYSIA")){
                                                        ConVamountEx = amountEx * 0.22;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("CNY - CHINA")){
                                                        ConVamountEx = amountEx * 0.15;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("WON - KOREA")){
                                                        ConVamountEx = amountEx * 0.00076;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("USD - UNITED STATE")){
                                                        ConVamountEx = amountEx;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else{
                                                        ConVamountEx = amountEx * 1.08;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                }
                                                else {
                                                    if(currencies.equals("MYR - MALAYSIA")){
                                                        ConVamountEx = amountEx * 0.21;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("CNY - CHINA")){
                                                        ConVamountEx = amountEx * 0.14;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("WON - KOREA")){
                                                        ConVamountEx = amountEx * 0.00071;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else if(currencies.equals("USD - UNITED STATE")){
                                                        ConVamountEx = amountEx * 0.93;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                    else{
                                                        ConVamountEx = amountEx;
                                                        Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                    }
                                                }

                                                DecimalFormat df = new DecimalFormat("#.##");
                                                double formattedAmount = Double.parseDouble(df.format(ConVamountEx));
                                                long currenctime = System.currentTimeMillis();

                                                if (formattedAmount <= budgetAmount){
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("EXP_Amount", amountEx);
                                                    data.put("EXPCV_Amount", formattedAmount);
                                                    data.put("currency", currencies);
                                                    data.put("EXP_date", ExDate);
                                                    data.put("EXP_Des", ExDES);
                                                    data.put("user_ID",userId);
                                                    data.put("bid",budgetId);
                                                    data.put("timeStamp", currenctime);
                                                    DocumentReference budgetRef = db.collection("budget").document(budgetId);
                                                    budgetRef.collection("expenses")
                                                            .add(data)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(getActivity(), "Expense added to budget " + budgetId + "," + budgetNM, Toast.LENGTH_SHORT).show();
                                                                    Log.w("DONE ADDED", "Expense added to budget");
                                                                    double temp = formattedAmount;
                                                                    double amtLeft = budgetAmount - temp;

                                                                    double ttlusage = budgetUsage + temp;


                                                                    budgetRef.update("amount", amtLeft,"usage", ttlusage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Log.d("AMOUNT AFTER EXPENSE", String.valueOf(amtLeft));
                                                                            Log.d("USAGE AFTER EXPENSE", String.valueOf(ttlusage));
                                                                        }
                                                                    });

                                                                    // Clear the input fields
                                                                    Expenseamount.setText("");
                                                                    currencyOptions.setText("");
                                                                    etSelectDate.setText("");
                                                                    ExpensesDes.setText("");
                                                                    mBudgetDropdown.setText("");

                                                                    // Update the document with the budget code
                                                                    documentReference.update("eid", documentReference.getId())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("TAG", "Eid updated successfully.");
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w("TAG", "Error updating Eid.", e);
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w("FAILED ADD", "Error adding expense to budget", e);
                                                                    Toast.makeText(getActivity(), "Error adding expense to budget " + budgetId, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                                else {
                                                    Toast.makeText(getActivity(), "Expense amount cannot be greater than " + budgetAmount + " " + budgetcurrency, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else{
                                                Toast.makeText(getActivity(), "Expense Date Must Be Within Month : " + (currentbudgetmonth+1), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                                else {
                                    Log.w("NO DOCUMENTS FOUND", "Error getting documents.", task.getException());
                                    Toast.makeText(getActivity(), "Error getting documents", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(getActivity(), "Please select a budget first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //----

        //only allow one misc budget each month, only include amount, and begin_date, usage, Mid, userid
        misc = v1.findViewById(R.id.btn_emergency);

        misc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddExpens.setVisibility(View.GONE);
                ExpensesDes.setVisibility(View.GONE);
                menu.setVisibility(View.GONE);
                etSelectDate.setVisibility(View.GONE);
                Expenseamount.setVisibility(View.GONE);
                mBudgetDropdown.setVisibility(View.GONE);

                Fragment newFragment = new MiscExpFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.createExpens, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v1;
    }
}