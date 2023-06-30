package com.example.finant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        textView1 = findViewById(R.id.btn_SwaptoLogin);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password;

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegistrationActivity.this,"Email Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this,"Password Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(password.length() < 10){
                        Toast.makeText(RegistrationActivity.this,"Minimum Password Length Is 10 Characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!password.matches(".*[A-Z].*")){
                        Toast.makeText(RegistrationActivity.this,"Must Consist At Least 1 Upper Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*[a-z].*")) {
                        Toast.makeText(RegistrationActivity.this,"Must Consist At Least 1 Lower Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*\\d.*")) {
                        Toast.makeText(RegistrationActivity.this,"Must Consist At Least 1 Digit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*[!@#$%^&+=].*")) {
                        Toast.makeText(RegistrationActivity.this,"Must Consist At Least 1 Special Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

//                          send email verification link
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegistrationActivity.this,"Email Verification Link Have Been Sent", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email Not Sent" + e.getMessage());
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Invalid Email or Email Been Used", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // need to clear cache here, so will not directly login without typing ?
        // need to get verification then only data will load into database. ?


        //used after logout
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}