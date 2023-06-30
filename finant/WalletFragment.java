package com.example.finant;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DecimalFormat;


public class WalletFragment extends Fragment {

    public WalletFragment() {
        // Required empty public constructor
    }

    TextView reload, transachis, wAmount;
    String userId,wid;
    ImageView qscan, qpay, ebill, etrans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);

        reload = v.findViewById(R.id.reload);
        transachis = v.findViewById(R.id.transhis);
        wAmount = v.findViewById(R.id.walletAmount);
        qscan = v.findViewById(R.id.qrscan);
        qpay = v.findViewById(R.id.qrpay);
        ebill = v.findViewById(R.id.billing);
        etrans = v.findViewById(R.id.transfer);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");
        wid = args.getString("wid");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myWalletRef1 = db.collection("e-wallet");
        Query desquery2 = myWalletRef1.whereEqualTo("user_ID",userId);

        desquery2.get().addOnCompleteListener(walletTask2 -> {
                    if (walletTask2.isSuccessful()) {
                        QuerySnapshot wallet2Snapshot = walletTask2.getResult();

                        // Calculate total expenses for each month and year
                        if (!wallet2Snapshot.isEmpty()) {

                            for (QueryDocumentSnapshot walletDoc2 : wallet2Snapshot) {
                                double wAMNT = walletDoc2.getDouble("WAL_Amount");

                                DecimalFormat decimalFormat = new DecimalFormat("#.##"); // format to two decimal places
                                String formattedValue = decimalFormat.format(wAMNT);

                                wAmount.setText("RM " + formattedValue);
                            }
                        }
                    }
                });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new ReloadFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("userID", userId);
                args.putString("wid", wid);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.expana, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        transachis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new TransHisFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("wid", wid);
                args.putString("userID", userId);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.expana, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        qpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new GenerateQrFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("userID", userId);
                args.putString("wid", wid);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.expana, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        transachis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new ViewTransHistFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("userID", userId);
                args.putString("wid", wid);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.expana, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        qscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        return v;
    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volumn up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if(result.getContents()!=null){

            Fragment newFragment = new qrscanFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            //passing budgetID
            Bundle args = new Bundle();
            args.putString("userID", userId);
            args.putString("wid", wid);
            args.putString("Otherwid", result.getContents());
            newFragment.setArguments(args);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.expana, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    });
}