package com.example.finant;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReloadFragment extends Fragment {

    Button btnrld;
    String userId,wid,wpass,currentDate,currentTime;
    double rAmount;
    TextInputEditText reload;

    public ReloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reload, container, false);

        reload = v.findViewById(R.id.reloadAmount);
        btnrld = v.findViewById(R.id.btn_reload);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");
        wid = args.getString("wid");

        btnrld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                wpass = walletDoc.getString("encryptedpwd");
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

                                                    rAmount = Double.parseDouble(String.valueOf(reload.getText()));

                                                    // get current date and time
                                                    Calendar calendar = Calendar.getInstance();
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                                    currentDate = dateFormat.format(calendar.getTime());
                                                    currentTime = timeFormat.format(calendar.getTime());

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("Reload_Amount", rAmount);
                                                    data.put("user_ID", userId);
                                                    data.put("wallet_currency", "MYR - MALAYSIA");
                                                    data.put("timeStamp",currentTime);
                                                    data.put("date",currentDate);

                                                    DocumentReference reloadRef = db.collection("e-wallet").document(wid);
                                                    reloadRef.collection("reload")
                                                            .add(data)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(getActivity(), "Reload added to wallet ", Toast.LENGTH_SHORT).show();
                                                                    Log.w("DONE ADDED", "Reload added to wallet" + wid);

                                                                    // Update the document with the budget code
                                                                    documentReference.update("rid", documentReference.getId())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("TAG", "Rid updated successfully.");
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w("TAG", "Error updating Rid.", e);
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

                                                                                            double total = wAMNT + rAmount;

                                                                                            DocumentReference reloadRef2 = db.collection("e-wallet").document(wid);

                                                                                            Map<String, Object> updates = new HashMap<>();
                                                                                            updates.put("WAL_Amount", total);
                                                                                            updates.put("d", d);
                                                                                            updates.put("n", n);
                                                                                            updates.put("encryptedpwd", encryptedpwd);
                                                                                            updates.put("wallet_currency", wcur);
                                                                                            updates.put("user_ID", userId);
                                                                                            updates.put("wid", wid);

                                                                                            reloadRef2.update(updates)
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

                                                                                            btnrld.setVisibility(View.GONE);
                                                                                            reload.setVisibility(View.GONE);

                                                                                            Fragment newFragment = new ShowSuccessfulFragment();
                                                                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                                                                            //passing budgetID
                                                                                            Bundle args = new Bundle();
                                                                                            args.putString("userID", userId);
                                                                                            args.putString("wid", wid);
                                                                                            args.putString("timeStamp",currentTime);
                                                                                            args.putString("date",currentDate);
                                                                                            args.putString("Reload_Amount", String.valueOf(rAmount));
                                                                                            newFragment.setArguments(args);

                                                                                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                                                                                            transaction.replace(R.id.toreload, newFragment);
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

                                                }else{
                                                    Toast.makeText(getActivity(), "Wrong Credential ", Toast.LENGTH_SHORT).show();
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
            }
        });

        return v;
    }
    public static BigInteger decrypt(BigInteger message, BigInteger d, BigInteger n)
    {
        return message.modPow(d, n);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        btnrld.setOnClickListener(null);
        btnrld = null;
    }
}