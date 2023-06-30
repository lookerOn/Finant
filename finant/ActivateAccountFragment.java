package com.example.finant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActivateAccountFragment extends Fragment {

    public ActivateAccountFragment() {
        // Required empty public constructor
    }

    TextInputEditText pwallet;
    String walletpwd, userId;
    Button actbtn;
    double amount = 0.00;

    public static BigInteger p;
    public static BigInteger q;
    public static BigInteger n;
    public static BigInteger phi;
    public static BigInteger e;
    public static BigInteger d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_activate_account, container, false);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");

        pwallet = v.findViewById(R.id.passwordwallet);
        actbtn = v.findViewById(R.id.btn_ACTV);

        actbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletpwd = String.valueOf(pwallet.getText());

                if (TextUtils.isEmpty(walletpwd)) {
                    Toast.makeText(getActivity(), "Password Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(walletpwd.length() < 10){
                        Toast.makeText(getActivity(),"Minimum Password Length Is 10 Characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!walletpwd.matches(".*[A-Z].*")){
                        Toast.makeText(getActivity(),"Must Consist At Least 1 Upper Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!walletpwd.matches(".*[a-z].*")) {
                        Toast.makeText(getActivity(),"Must Consist At Least 1 Lower Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!walletpwd.matches(".*\\d.*")) {
                        Toast.makeText(getActivity(),"Must Consist At Least 1 Digit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!walletpwd.matches(".*[!@#$%^&+=].*")) {
                        Toast.makeText(getActivity(),"Must Consist At Least 1 Special Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                getgeneralkey();

                // Convert string to numbers using a cipher
                BigInteger messageBytes = new BigInteger(walletpwd.getBytes());
                // Encrypt the ciphered message
                BigInteger cipherText = encrypt(messageBytes, e, n);

                Log.e("messageBytes : ",String.valueOf(messageBytes));
                Log.e("ciphertext : ",String.valueOf(cipherText));

                // Reference the Firestore collection where you want to store the data
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // DocumentReference docRef = db.collection("user").document(userId);
                CollectionReference myWalletRef = db.collection("e-wallet");

                Map<String, Object> data = new HashMap<>();
                data.put("WAL_Amount", amount);
                data.put("user_ID", userId);
                data.put("wallet_currency", "MYR - MALAYSIA");
                data.put("encryptedpwd",String.valueOf(cipherText));
                data.put("d",String.valueOf(d));
                data.put("n",String.valueOf(n));

                myWalletRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference newDocRef) {
                        Log.d("MAP", "New wallet added with ID: " + newDocRef.getId());
                        Toast.makeText(getActivity(), "Wallet Successfully Created", Toast.LENGTH_SHORT).show();

                        // Update the document with the budget code
                        newDocRef.update("wid", newDocRef.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "Wid updated successfully.");

                                        actbtn.setVisibility(View.GONE);
                                        pwallet.setVisibility(View.GONE);

                                        Fragment newFragment = new WalletFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                        //passing budgetID
                                        Bundle args = new Bundle();
                                        args.putString("userID", userId);
                                        args.putString("wid", newDocRef.getId());
                                        args.putString("d", String.valueOf(d));
                                        args.putString("n", String.valueOf(n));
                                        newFragment.setArguments(args);

                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.replace(R.id.actwallet, newFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error updating Wid.", e);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MAP", "Error adding new document", e);
                        Toast.makeText(getActivity(), "Wallet Failed To Activate", Toast.LENGTH_SHORT).show();
                    }
                });

//
            }
        });

        return v;
    }
    public void getgeneralkey(){
        // 1. Find prime P and Q
        p = getPrime(512);
        q = getPrime(512);

        // 2. Calculate the value of n from multiplying p and q.
        // n is mod for public and private keys
        // formula is n = p x q

        n = getN(p,q);

        // 3. Calculate Phi(n), formula = Phi(n) = (p-1)(q-1)

        phi = getPhi(p,q);

        // 4. e = 65537 such that 1 < e < Phi(n) and GCD(e, Phi(n)) = 1
        e = getE();

        // Check if GCD(e, Phi(n)) = 1, if not, calculate new p,q,n
        while(true)
        {

            if(gcd(e,phi).equals(BigInteger.ONE))
            {
                break;
            }

            // 1. Find prime P and Q

            p = getPrime(8);
            q = getPrime(8);

            // 2. Calculate the value of n from multiplying p and q.
            // n is mod for public and private keys
            // formula is n = p x q

            n = getN(p,q);

            // 3. Calculate Phi(n), formula = Phi(n) = (p-1)(q-1)

            phi = getPhi(p,q);
        }

        // 5. Compute the value for the decryption exponent d,
        // where d ≡ e^(-1) (mod Phi(n))
        d = e.modInverse(phi);

        //print out fixed value
        Log.e("P : ",String.valueOf(p));
        Log.e("q : ",String.valueOf(q));
        Log.e("n : ",String.valueOf(n));
        Log.e("phi : ",String.valueOf(phi));
        Log.e("d : ",String.valueOf(d));
        Log.e("e : ",String.valueOf(e));
    }
    public static BigInteger getPrime(int bits)
    {
        Random rand = new Random();
        BigInteger getPrime = BigInteger.probablePrime(bits, rand);
        return getPrime;
    }

    public static BigInteger getN(BigInteger p, BigInteger q)
    {
        return p.multiply(q);
    }

    public static BigInteger getPhi(BigInteger p, BigInteger q)
    {
        BigInteger phiVal = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        return phiVal;
    }

    public static BigInteger getE()
    {
        BigInteger eVal = BigInteger.valueOf(65537);

        return eVal;
    }

    /**
     * Recursive implementation of Euclidian algorithm to find greatest common denominator
     * Note: Uses BigInteger operations
     */
    public static BigInteger gcd(BigInteger a, BigInteger b)
    {
        if (b.equals(BigInteger.ZERO))
        {
            return a;
        }
        else
        {
            return gcd(b, a.mod(b));
        }
    }

    public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n)
    {
        return message.modPow(e, n);
    }

    public static BigInteger decrypt(BigInteger message, BigInteger d, BigInteger n)
    {
        return message.modPow(d, n);
    }
}