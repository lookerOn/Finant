package com.example.finant;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQrFragment extends Fragment {

    String userId,wid;
    Button backhome;

    public GenerateQrFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generate_qr, container, false);

        // Get the passed userID
        Bundle args = getArguments();
        wid = args.getString("wid");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // User is signed in, so get the user ID
        userId = user.getUid();
        backhome = v.findViewById(R.id.btn_HOMEWT);

//        generateQRCode(wid);

        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the visibility of the backhome button to GONE
                backhome.setVisibility(View.GONE);

                Fragment newFragment = new WalletFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //passing budgetID
                Bundle args = new Bundle();
                args.putString("userID", userId);
                args.putString("wid", wid);
                newFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.geneQR, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateQRCode(wid);
    }

    private void generateQRCode(String wid) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(wid, BarcodeFormat.QR_CODE, 1000, 1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ImageView imageView = getView().findViewById(R.id.qr_code_image_view);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}