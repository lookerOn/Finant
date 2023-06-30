package com.example.finant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.CharacterPickerDialog;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLog;
    FirebaseAuth mAuth;
    TextView textView1, textView2;
    String userID;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLog = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();
        textView1 = findViewById(R.id.btn_SwaptoRegister);
        textView2 = findViewById(R.id.btn_forgotpassword);

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password;

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Email Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Password Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(password.length() < 10){
                        Toast.makeText(LoginActivity.this,"Minimum Password Length Is 10 Characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!password.matches(".*[A-Z].*")){
                        Toast.makeText(LoginActivity.this,"Must Consist At Least 1 Upper Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*[a-z].*")) {
                        Toast.makeText(LoginActivity.this,"Must Consist At Least 1 Lower Case Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*\\d.*")) {
                        Toast.makeText(LoginActivity.this,"Must Consist At Least 1 Digit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.matches(".*[!@#$%^&+=].*")) {
                        Toast.makeText(LoginActivity.this,"Must Consist At Least 1 Special Character", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            userID = mAuth.getCurrentUser().getUid();
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (!user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Email Haven't Been Verified", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(getApplicationContext(), VerificationActivity.class);
                                startActivity(intent1);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(getApplicationContext(), HomePage.class);
                                startActivity(intent2);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent3);
                finish();
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(view.getContext());
                passwordresetDialog.setTitle("Reset Password");

                // Create a SpannableString with the text you want to display
                SpannableString message = new SpannableString("Password Requirement Enforced: \n\n1. Minimum Password Length Is 10 Characters \n2. Must Consist At Least 1 Upper Case Character \n3. Must Consist At Least 1 Lower Case Character \n4. Must Consist At Least 1 Digit \n5. Must Consist At Least 1 Special Character \n\nAlert: Unfollow Password Requirement Will Cause Unable of Login And Repeat Of Reset Password Process Until Meet Requirement \n\n\n\nEnter Your Email Below To Receive Reset Link \n");
                // Create a RelativeSizeSpan to set the text size
                RelativeSizeSpan textSizeSpan = new RelativeSizeSpan(0.8f); // 1.5 times the default text size
                // Set the span on the SpannableString to change the text size
                message.setSpan(textSizeSpan, 0, message.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                passwordresetDialog.setMessage(message);
                passwordresetDialog.setView(resetMail);

                passwordresetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset Link Sent to Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Reset Link Failed to Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordresetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //closed dialog
                    }
                });

                passwordresetDialog.create().show();
            }
        });
    }
}