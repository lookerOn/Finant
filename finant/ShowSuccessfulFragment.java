package com.example.finant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ShowSuccessfulFragment extends Fragment {

    String userId,wid,rAmt,tStm,dt;
    Button backhome;
    TextView samt,stime,sdt;

    public ShowSuccessfulFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_successful, container, false);

        // Get the passed userID
        Bundle args = getArguments();
        userId = args.getString("userID");
        wid = args.getString("wid");
        rAmt = args.getString("Reload_Amount");
        tStm = args.getString("timeStamp");
        dt = args.getString("date");

        backhome = v.findViewById(R.id.btn_HOMEWALLET);
        samt = v.findViewById(R.id.sucsamt);
        stime = v.findViewById(R.id.sucstime);
        sdt = v.findViewById(R.id.sucsdate);

        samt.setText(rAmt);
        stime.setText(tStm);
        sdt.setText(dt);

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
                transaction.replace(R.id.Successful, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backhome.setOnClickListener(null);
        backhome = null;
    }
}