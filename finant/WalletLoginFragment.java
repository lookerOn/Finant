package com.example.finant;

import android.os.Bundle;

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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.util.Random;


public class WalletLoginFragment extends Fragment {

    TextInputEditText encypwallet;
    String walletpwd, userId, wid, wpass;
    Button LOGbtn;

    public static BigInteger p;
    public static BigInteger q;
    public static BigInteger n;
    public static BigInteger phi;
    public static BigInteger e;
    public static BigInteger d;

    public WalletLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet_login, container, false);

        encypwallet = v.findViewById(R.id.passwordwallet);
        LOGbtn = v.findViewById(R.id.btn_WLOGIN);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");

        LOGbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletpwd = String.valueOf(encypwallet.getText());

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
                                        wid = walletDoc.getString("wid");
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

                                        Log.e("Decrypted : ",String.valueOf(decrypted));
                                        Log.e("Plaintext : ",plainText);

                                        if(plainText.equals(walletpwd)){

                                            Toast.makeText(getActivity(), "Successfully Log In", Toast.LENGTH_SHORT).show();

                                            LOGbtn.setVisibility(View.GONE);
                                            encypwallet.setVisibility(View.GONE);

                                            Fragment newFragment = new WalletFragment();
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                            //passing budgetID
                                            Bundle args = new Bundle();
                                            args.putString("userID", userId);
                                            args.putString("wid", wid);
                                            newFragment.setArguments(args);

                                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                                            transaction.replace(R.id.wlogin, newFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        }else {
                                            Toast.makeText(getActivity(), "Wrong Credential", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });

            }
        });

        return v;
    }
    public static BigInteger decrypt(BigInteger message, BigInteger d, BigInteger n)
    {
        return message.modPow(d, n);
    }
}