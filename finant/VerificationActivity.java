package com.example.finant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    Button resendverify;
    TextView verifyMesg1, tologin, toregis, timetext;
    FirebaseAuth mAuth;
    String userID;

    CountDownTimer count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        resendverify = findViewById(R.id.btn_verify);
        verifyMesg1 = findViewById(R.id.EmailVerificationText1);
        tologin = findViewById(R.id.backtologin);
        toregis = findViewById(R.id.deleteUser);
        timetext = findViewById(R.id.counter);
        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();
        FirebaseUser user = mAuth.getCurrentUser();

        resendverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // send email verification link
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "Email Verification Link Have Been Sent", Toast.LENGTH_SHORT).show();

                        timetext.setVisibility(View.VISIBLE);
                        count = new CountDownTimerClass(60000,1000);
                        count.start();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "onFailure: Email Not Sent" + e.getMessage());
                        Toast.makeText(v.getContext(), "Requests Too Frequent, Cold Down for 1 mins Before Trying", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //used without login
        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //used to delete unverified account
        toregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent1 = new Intent(getApplicationContext(), RegistrationActivity.class);
                                startActivity(intent1);
                                finish();
                            } else {
                                Toast.makeText(VerificationActivity.this, "Failed To Do New Registration", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent2);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    public class CountDownTimerClass extends CountDownTimer{

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            int progress=(int)(l/1000);
            timetext.setText("Please request after " + Integer.toString(progress) + " secs");
        }

        @Override
        public void onFinish() {
            timetext.setVisibility(View.INVISIBLE);
        }
    }
}