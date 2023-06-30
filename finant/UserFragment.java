package com.example.finant;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UserFragment extends Fragment {

    FirebaseAuth auth;
    Button changeMail;
    TextView toLogout, emailview, resetpass, deactivate;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user, container, false);

        auth = FirebaseAuth.getInstance();
        changeMail = v.findViewById(R.id.btn_CHANGEMAIL);
        toLogout = v.findViewById(R.id.btn_logout);
        emailview = v.findViewById(R.id.email);
        resetpass = v.findViewById(R.id.btn_resetpassword);
        user = auth.getCurrentUser();

        String tmp = user.getEmail();
        emailview.setText(tmp);
        emailview.setEnabled(false);

        changeMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText pass = new EditText(view.getContext());
                final AlertDialog.Builder changeEmail = new AlertDialog.Builder(view.getContext());
                changeEmail.setTitle("Change Email Address");
//
                // Create a SpannableString with the text you want to display
                SpannableString message = new SpannableString("Please Enter Your Password At Below As Verification Purpose. \n\n");
                // Create a RelativeSizeSpan to set the text size
                RelativeSizeSpan textSizeSpan = new RelativeSizeSpan(0.8f); // 1.5 times the default text size
                // Set the span on the SpannableString to change the text size
                message.setSpan(textSizeSpan, 0, message.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//
                changeEmail.setMessage(message);
                changeEmail.setView(pass);

                changeEmail.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = user.getEmail();
                        String passw = pass.getText().toString();
//
                        // Authenticate the user
                        auth.signInWithEmailAndPassword(mail, passw).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                final EditText changeM = new EditText(view.getContext());
                                final AlertDialog.Builder changeEmail2 = new AlertDialog.Builder(view.getContext());
                                changeEmail2.setTitle("Change Email Address");

                                // Create a SpannableString with the text you want to display
                                SpannableString message = new SpannableString("Enter Your New Email. \n\nWARNING : \nPlease Confirm Your Email Before Click YES. \nThis Modification Is Unable to Restore\n\n");
                                // Create a RelativeSizeSpan to set the text size
                                RelativeSizeSpan textSizeSpan = new RelativeSizeSpan(0.8f); // 1.5 times the default text size
                                // Set the span on the SpannableString to change the text size
                                message.setSpan(textSizeSpan, 0, message.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                                changeEmail2.setMessage(message);
                                changeEmail2.setView(changeM);

                                changeEmail2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Update user's email address
                                        String newEmailAddress = changeM.getText().toString();

                                        // Authentication successful, change the user's mail
                                        user.updateEmail(newEmailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    // Send verification email to new email address
                                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(), "Email Verification Link Have Been Sent", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                            getActivity().finish();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: Email Not Sent" + e.getMessage());
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to Update Email", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });

                                changeEmail2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //closed dialog
                                    }
                                });

                                changeEmail2.create().show();

                            }else {
                                // Authentication failed, display an error message
                                Toast.makeText(getActivity(), "User Doesn't Existed Or Wrong Credential", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent2 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                startActivity(intent2);
                                getActivity().finish();
                            }
                        });
                    }
                });

                changeEmail.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //closed dialog
                    }
                });

                changeEmail.create().show();

            }
        });





        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(view.getContext());
                passwordresetDialog.setTitle("Reset Password");

                // Create a SpannableString with the text you want to display
                SpannableString message = new SpannableString("Password Requirement Enforced: \n\n1. Minimum Password Length Is 10 Characters \n2. Must Consist At Least 1 Upper Case Character \n3. Must Consist At Least 1 Lower Case Character \n4. Must Consist At Least 1 Digit \n5. Must Consist At Least 1 Special Character \n\nAlert: Unfollow Password Requirement Will Cause Unable of Login And Repeat Of Reset Password Process Until Meet Requirement \n\n\n\nAre You Want to Reset Password? \n");
                // Create a RelativeSizeSpan to set the text size
                RelativeSizeSpan textSizeSpan = new RelativeSizeSpan(0.8f); // 1.5 times the default text size
                // Set the span on the SpannableString to change the text size
                message.setSpan(textSizeSpan, 0, message.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                passwordresetDialog.setMessage(message);

                passwordresetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = user.getEmail();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Reset Link Sent to Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Reset Link Failed to Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
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


        //used to delete account
        deactivate = v.findViewById(R.id.btn_deactivate);

        deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText pass= new EditText(view.getContext());
                final AlertDialog.Builder deactivasteDialog = new AlertDialog.Builder(view.getContext());
                deactivasteDialog.setTitle("Delete Account");

                // Create a SpannableString with the text you want to display
                SpannableString message = new SpannableString("Are You Sure To Deactivate This Account? \n\nPlease Enter Your Password At Below As Verification Purpose. \n");
                // Create a RelativeSizeSpan to set the text size
                RelativeSizeSpan textSizeSpan = new RelativeSizeSpan(0.8f); // 1.5 times the default text size
                // Set the span on the SpannableString to change the text size
                message.setSpan(textSizeSpan, 0, message.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                deactivasteDialog.setMessage(message);
                deactivasteDialog.setView(pass);

                deactivasteDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = user.getEmail();

                        String passw = pass.getText().toString();

                        // Authenticate the user
                        auth.signInWithEmailAndPassword(mail, passw).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // Authentication successful, delete the user's account
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Successful Deactivate Account", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent1 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                            startActivity(intent1);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to Deactivate Account", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent2 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                            startActivity(intent2);
                                            getActivity().finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "User Doesn't Existed Or Wrong Credential", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent2 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                startActivity(intent2);
                                getActivity().finish();
                            }
                        });
                    }
                });

                deactivasteDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //closed dialog
                    }
                });

                deactivasteDialog.create().show();
            }
        });


        toLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return v;
    }
}