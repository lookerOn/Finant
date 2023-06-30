package com.example.finant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.math.BigInteger;
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

public class qrscanFragment extends Fragment {

    Button btntrns;
    TextInputEditText trans;
    AutoCompleteTextView mBudgetDropdown;
    String userId,wid, othwid,currentDate,currentTime;
    double tAmount;
    Budget selectedBudget;
    String budgid, budget_Name, budgetcurrency, budgetCur, repeatStatus, BeginDateString, begindate, budgetId;
    double budgetAmount, budgetUsage, ConVamountEx = 0.0, wAMNT;
    List<Budget> mBudgets;
    ArrayAdapter<Budget> mAdapter;

    public qrscanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qrscan, container, false);

        trans = v.findViewById(R.id.transAmount);
        btntrns = v.findViewById(R.id.btn_TRANSfer);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");
        wid = args.getString("wid");
        othwid = args.getString("Otherwid");

        //get wallet cash value
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myWalletRef1 = db.collection("e-wallet");
        Query desquery2 = myWalletRef1.whereEqualTo("user_ID",userId);

        desquery2.get().addOnCompleteListener(walletTask2 -> {
            if (walletTask2.isSuccessful()) {
                QuerySnapshot wallet2Snapshot = walletTask2.getResult();

                // Calculate total expenses for each month and year
                if (!wallet2Snapshot.isEmpty()) {

                    for (QueryDocumentSnapshot walletDoc2 : wallet2Snapshot) {
                        wAMNT = walletDoc2.getDouble("WAL_Amount");

                        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // format to two decimal places
                        String formattedValue = decimalFormat.format(wAMNT);
                    }
                }
            }
        });


        //choose Budget
//      Initialize the AutoCompleteTextView
        mBudgetDropdown = v.findViewById(R.id.BudgetOpt1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // User is signed in, so get the user ID
        String userId = user.getUid();

        // Reference the Firestore collection where you want to store the data
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


        btntrns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedBudget != null) {
                    budgetId = selectedBudget.getBid();
                    Log.d("BUDGET_ID", budgetId);
                    String budgetNM = selectedBudget.getBudget_name();
                    budgetcurrency = selectedBudget.getBcurrency();
                    Log.d("Budget_Currency", budgetcurrency);
                    begindate = selectedBudget.getBegin_date();
                    Log.d("Budget_Date", String.valueOf(begindate));

                    String tAmountString = String.valueOf(trans.getText());

                    if(tAmountString.trim().length() != 0){

                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        // create the dialog
                        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Enter your e-wallet password");

                        // set up the password input field
                        final EditText passwordInput = new EditText(getActivity());
                        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(passwordInput);

                        // set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String password = passwordInput.getText().toString();

                                // do something with the password
                                // Reference the Firestore collection where you want to store the data
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                // DocumentReference docRef = db.collection("user").document(userId);
                                CollectionReference myWalletRef = db.collection("e-wallet");
                                Query desquery = myWalletRef.whereEqualTo("user_ID",userId);

                                desquery.get().addOnCompleteListener(walletTask1 -> {
                                    if (walletTask1.isSuccessful()) {
                                        QuerySnapshot wallet1Snapshot = walletTask1.getResult();

                                        // Calculate total expenses for each month and year
                                        if (!wallet1Snapshot.isEmpty()) {

                                            for (QueryDocumentSnapshot walletDoc : wallet1Snapshot) {
                                                String wpass = walletDoc.getString("encryptedpwd");
                                                String dStr = walletDoc.getString("d");
                                                String nStr = walletDoc.getString("n");
                                                BigInteger d = new BigInteger(dStr);
                                                BigInteger n = new BigInteger(nStr);

                                                //get CipherMessage
                                                BigInteger cipherText = new BigInteger(wpass);
                                                // Decrypt the encrypted message
                                                BigInteger decrypted = decrypt(cipherText, d, n);
                                                // Uncipher the decrypted message to text
                                                String plainText = new String(decrypted.toByteArray());

                                                Log.e("Decrypted : ", String.valueOf(decrypted));
                                                Log.e("Plaintext : ", plainText);

                                                if (plainText.equals(password)) {
                                                    Toast.makeText(getActivity(), "Verified Successfully", Toast.LENGTH_SHORT).show();

                                                    tAmount = Double.parseDouble(String.valueOf(trans.getText()));

                                                    // get current date and time
                                                    Calendar calendar = Calendar.getInstance();
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                                    currentDate = dateFormat.format(calendar.getTime());
                                                    currentTime = timeFormat.format(calendar.getTime());

                                                    if(tAmount<=wAMNT){

                                                        ///--->>update expenses
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

                                                                        if (budgetcurrency.equals("MYR - MALAYSIA")) {
                                                                            ConVamountEx = tAmount;
                                                                            Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                                        } else if (budgetcurrency.equals("CNY - CHINA")) {
                                                                            ConVamountEx = tAmount * 1.54;
                                                                            Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                                        } else if (budgetcurrency.equals("WON - KOREA")) {
                                                                            ConVamountEx = tAmount * 291.78;
                                                                            Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                                        } else if (budgetcurrency.equals("USD - UNITED STATE")) {
                                                                            ConVamountEx = tAmount * 0.22;
                                                                            Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                                        } else {
                                                                            ConVamountEx = tAmount * 0.21;
                                                                            Log.d("ConVamountEx", String.valueOf(ConVamountEx));
                                                                        }

                                                                        DecimalFormat df = new DecimalFormat("#.##");
                                                                        double formattedAmount = Double.parseDouble(df.format(ConVamountEx));
                                                                        long currenctime = System.currentTimeMillis();

                                                                        if (formattedAmount <= budgetAmount) {
                                                                            Map<String, Object> data = new HashMap<>();
                                                                            data.put("EXP_Amount", tAmount);
                                                                            data.put("EXPCV_Amount", formattedAmount);
                                                                            data.put("currency", "MYR - MALAYSIA");
                                                                            data.put("EXP_date", currentDate);
                                                                            data.put("EXP_Des", "WALLET");
                                                                            data.put("user_ID", userId);
                                                                            data.put("bid", budgetId);
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


                                                                                            budgetRef.update("amount", amtLeft, "usage", ttlusage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Log.d("AMOUNT AFTER EXPENSE", String.valueOf(amtLeft));
                                                                                                    Log.d("USAGE AFTER EXPENSE", String.valueOf(ttlusage));
                                                                                                }
                                                                                            });

                                                                                            // Update the document with the budget code
                                                                                            documentReference.update("eid", documentReference.getId())
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            Log.d("TAG", "Eid updated successfully.");



                                                                                                            //******************************************************************

                                                                                                            Map<String, Object> data = new HashMap<>();
                                                                                                            data.put("Transfer_Amount", tAmount);
                                                                                                            data.put("user_ID", userId);
                                                                                                            data.put("wallet_currency", "MYR - MALAYSIA");
                                                                                                            data.put("timeStamp",currentTime);
                                                                                                            data.put("date",currentDate);
                                                                                                            data.put("receiver_ID",othwid);

                                                                                                            DocumentReference reloadRef = db.collection("e-wallet").document(wid);
                                                                                                            reloadRef.collection("transfer")
                                                                                                                    .add(data)
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                                                                            Toast.makeText(getActivity(), "Transfer Successfully", Toast.LENGTH_SHORT).show();
                                                                                                                            Log.w("DONE ADDED", "Transfer Successfully" + wid);

                                                                                                                            // Update the document with the budget code
                                                                                                                            documentReference.update("tid", documentReference.getId())
                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                            Log.d("TAG", "Tid updated successfully.");
                                                                                                                                        }
                                                                                                                                    })
                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                            Log.w("TAG", "Error updating Tid.", e);
                                                                                                                                        }
                                                                                                                                    });

                                                                                                                            CollectionReference myWalletRef1 = db.collection("e-wallet");
                                                                                                                            Query desquery2 = myWalletRef1.whereEqualTo("user_ID",userId);

                                                                                                                            desquery2.get().addOnCompleteListener(walletTask2 -> {
                                                                                                                                if (walletTask2.isSuccessful()) {
                                                                                                                                    QuerySnapshot wallet2Snapshot = walletTask2.getResult();

                                                                                                                                    // Calculate total expenses for each month and year
                                                                                                                                    if (!wallet2Snapshot.isEmpty()) {

                                                                                                                                        for (QueryDocumentSnapshot walletDoc2 : wallet2Snapshot) {
                                                                                                                                            double wAMNT = walletDoc2.getDouble("WAL_Amount");
                                                                                                                                            String d = walletDoc2.getString("d");
                                                                                                                                            String n = walletDoc2.getString("n");
                                                                                                                                            String encryptedpwd = walletDoc2.getString("encryptedpwd");
                                                                                                                                            String wcur = walletDoc2.getString("wallet_currency");

                                                                                                                                            double total = wAMNT - tAmount;

                                                                                                                                            DocumentReference transferRef2 = db.collection("e-wallet").document(wid);

                                                                                                                                            Map<String, Object> updates = new HashMap<>();
                                                                                                                                            updates.put("WAL_Amount", total);
                                                                                                                                            updates.put("d", d);
                                                                                                                                            updates.put("n", n);
                                                                                                                                            updates.put("encryptedpwd", encryptedpwd);
                                                                                                                                            updates.put("wallet_currency", wcur);
                                                                                                                                            updates.put("user_ID", userId);
                                                                                                                                            updates.put("wid", wid);

                                                                                                                                            transferRef2.update(updates)
                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                            Log.d("TAG", "WAL_Amount successfully updated!");
                                                                                                                                                        }
                                                                                                                                                    })
                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                            Log.w("TAG", "Error updating WAL_Amount", e);
                                                                                                                                                        }
                                                                                                                                                    });


                                                                                                                                            //for receiver side
                                                                                                                                            Map<String, Object> data = new HashMap<>();
                                                                                                                                            data.put("Transfer_Amount", tAmount);
                                                                                                                                            data.put("sender_user_ID", userId);
                                                                                                                                            data.put("wallet_currency", "MYR - MALAYSIA");
                                                                                                                                            data.put("timeStamp",currentTime);
                                                                                                                                            data.put("date",currentDate);

                                                                                                                                            DocumentReference reloadRef6 = db.collection("e-wallet").document(othwid);
                                                                                                                                            reloadRef6.collection("receive")
                                                                                                                                                    .add(data)
                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                                                                                                            Log.w("DONE ADDED", "Transfer Successfully" + othwid);

                                                                                                                                                            // Update the document with the budget code
                                                                                                                                                            documentReference.update("tid", documentReference.getId())
                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                            Log.d("TAG", "Tid updated successfully.");
                                                                                                                                                                        }
                                                                                                                                                                    })
                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                            Log.w("TAG", "Error updating Tid.", e);
                                                                                                                                                                        }
                                                                                                                                                                    });

                                                                                                                                                            CollectionReference myWalletRef3 = db.collection("e-wallet");
                                                                                                                                                            Query desquery3 = myWalletRef3.whereEqualTo("wid", othwid);

                                                                                                                                                            desquery3.get().addOnCompleteListener(walletTask3 -> {
                                                                                                                                                                if (walletTask3.isSuccessful()) {
                                                                                                                                                                    QuerySnapshot wallet3Snapshot = walletTask3.getResult();

                                                                                                                                                                    // Calculate total expenses for each month and year
                                                                                                                                                                    if (!wallet3Snapshot.isEmpty()) {

                                                                                                                                                                        for (QueryDocumentSnapshot walletDoc3 : wallet3Snapshot) {
                                                                                                                                                                            double wAMNTreceive = walletDoc3.getDouble("WAL_Amount");
                                                                                                                                                                            String dreceive = walletDoc3.getString("d");
                                                                                                                                                                            String nreceive = walletDoc3.getString("n");
                                                                                                                                                                            String encryptedpwdreceive = walletDoc3.getString("encryptedpwd");
                                                                                                                                                                            String wcurreceive = walletDoc3.getString("wallet_currency");

                                                                                                                                                                            double receivetotal = wAMNTreceive + tAmount;

                                                                                                                                                                            DocumentReference transferRef3 = db.collection("e-wallet").document(othwid);

                                                                                                                                                                            Map<String, Object> updates3 = new HashMap<>();
                                                                                                                                                                            updates3.put("WAL_Amount", receivetotal);
                                                                                                                                                                            updates3.put("d", dreceive);
                                                                                                                                                                            updates3.put("n", nreceive);
                                                                                                                                                                            updates3.put("encryptedpwd", encryptedpwdreceive);
                                                                                                                                                                            updates3.put("wallet_currency", wcurreceive);
//                                                                                                                                                                            updates3.put("sender_user_ID", userId);
                                                                                                                                                                            updates3.put("wid", othwid);

                                                                                                                                                                            transferRef3.update(updates3)
                                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                        @Override
                                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                                            Log.d("TAG", "WAL_Amount successfully updated!");
                                                                                                                                                                                        }
                                                                                                                                                                                    })
                                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                        @Override
                                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                            Log.w("TAG", "Error updating WAL_Amount", e);
                                                                                                                                                                                        }
                                                                                                                                                                                    });
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            });
                                                                                                                                                        }
                                                                                                                                                    });

                                                                                                                                            btntrns.setVisibility(View.GONE);
                                                                                                                                            trans.setVisibility(View.GONE);

                                                                                                                                            Fragment newFragment = new ShowSuccessfulFragment();
                                                                                                                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                                                                                                                            //passing budgetID
                                                                                                                                            Bundle args = new Bundle();
                                                                                                                                            args.putString("userID", userId);
                                                                                                                                            args.putString("wid", wid);
                                                                                                                                            args.putString("timeStamp",currentTime);
                                                                                                                                            args.putString("date",currentDate);
                                                                                                                                            args.putString("Reload_Amount", String.valueOf(tAmount));
                                                                                                                                            newFragment.setArguments(args);

                                                                                                                                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                                                                                                                                            transaction.replace(R.id.totrans, newFragment);
                                                                                                                                            transaction.addToBackStack(null);
                                                                                                                                            transaction.commit();

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }
                                                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                                                        @Override
                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                            Toast.makeText(getActivity(), "Failed to reload wallet ", Toast.LENGTH_SHORT).show();
                                                                                                                            Log.w("DONE ADDED", "Reload failed to added to wallet" + wid);
                                                                                                                        }
                                                                                                                    });
                                                                                                            //******************************************************************


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
                                                                        } else {
                                                                            Toast.makeText(getActivity(), "Expense amount cannot be greater than " + budgetAmount + " " + budgetcurrency, Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                }
                                                            }
                                                        });
                                                        //----------------------------------------------------------------------------

                                                    }else{
                                                        Toast.makeText(getActivity(), "Exceed Wallet Cash Value Limit", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    Toast.makeText(getActivity(), "Wrong Credential", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        // show the dialog
                        builder.show();

                    }else{
                        Log.w("INPUT", "NO Amount INPUT");
                        Toast.makeText(getActivity(), "All Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Please select a budget first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }
    public static BigInteger decrypt(BigInteger message, BigInteger d, BigInteger n)
    {
        return message.modPow(d, n);
    }
}