package com.example.finant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BudgetService extends Service {

    private static final String TAG = "BudgetService";

    private static final String CHANNEL_ID = "my_channel";
    private static final String CHANNEL_NAME = "My Channel";
    private NotificationManager notificationManager;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String budgetName;
    private String Rstatus;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private String bbidd;
    int restdayofmonth;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.d(TAG, "onCreate: BudgetService created");

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        budgetName = intent.getStringExtra("budget_name");
        Rstatus = intent.getStringExtra("repeat_status");


        Log.d(TAG, "onStartCommand: budgetName = " + budgetName + ", Rstatus = " + Rstatus);

            startTimer();

        Notification notification = buildForegroundNotification();
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();

        Log.d(TAG, "onDestroy: BudgetService destroyed");

        removeForegroundNotification();
    }

    private void startTimer() {
        Log.d(TAG, "startTimer: Timer started");

        final int[] i = {0};
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                // User is signed in, so get the user ID
                String userId = user.getUid();

                // Reference the Firestore collection where you want to store the data
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference myCollectionRef = db.collection("budget");

                // Query Firestore for the previous budget
                Query query = myCollectionRef
                        .whereEqualTo("user_ID", userId);



                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (!snapshot.isEmpty()) {
                            // Get the data for the previous budget
                            for (DocumentSnapshot prevBudgetDoc : snapshot.getDocuments()) {
                                bbidd = prevBudgetDoc.getString("bid");
                                budgetName = prevBudgetDoc.getString("budget_name");
                                Rstatus = prevBudgetDoc.getString("repeat_status");
                                String prevBeginDateString = prevBudgetDoc.getString("begin_date");
                                double prevAmount = prevBudgetDoc.getDouble("amount");
                                String currencies = prevBudgetDoc.getString("bcurrency");
                                double counters = prevBudgetDoc.getDouble("counter");
                                double prevusage = prevBudgetDoc.getDouble("usage");

                                //get current month
                                Calendar cal0 = Calendar.getInstance();
                                int currentmonth = cal0.get(Calendar.MONTH);

                                //get prev month
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date prevBeginDate;
                                try {
                                    prevBeginDate = dateFormat.parse(prevBeginDateString);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(prevBeginDate);

                                int currentmonthprev = cal.get(Calendar.MONTH);

                                int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                                Log.e("Lastdayofthemonth: "," " + lastDayOfMonth);

                                int currecntdayofmonth = cal.get(Calendar.DAY_OF_MONTH);
                                Log.e("Currentdayofthemonth: "," " + currecntdayofmonth);

                                restdayofmonth = lastDayOfMonth - currecntdayofmonth;
                                Log.e("Restdayofthemonth1ST: "," " + restdayofmonth);

                                if (Rstatus.equals("YES") && counters < 12 && currentmonth == currentmonthprev) {
                                    // Calculate the new budget data

                                    cal.add(Calendar.MONTH, 1);
                                    cal.set(Calendar.DAY_OF_MONTH,1);
                                    Date nextBeginDate = cal.getTime();
                                    int newmonth = cal.get(Calendar.MONTH)+1;
                                    int newyear = cal.get(Calendar.YEAR);

                                    String newBudgetName = budgetName.replaceAll("[\\d\\W]+", "");
                                    String nextBudgetName = newBudgetName + "/" + newmonth + "/"+ newyear;
                                    String nextBeginDateString = new SimpleDateFormat("dd/MM/yyyy").format(nextBeginDate);

                                    double nextAmount = prevAmount;
                                    double usage = 0.00;

                                    // Set the data for the new budget document
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("budget_name", nextBudgetName);
                                    data.put("amount", nextAmount+prevusage);
                                    data.put("bcurrency", currencies);
                                    data.put("begin_date", nextBeginDateString);
                                    data.put("repeat_status", "YES");
                                    data.put("user_ID", userId);
                                    data.put("usage", usage);
                                    data.put("counter", counters + 1);

//                                  Write data to Firestore for the new budget
                                    myCollectionRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference newDocRef1) {
                                            Log.d("MAP", "New budget added with ID: " + newDocRef1.getId());
                                            Toast.makeText(getApplicationContext(), "Budget Successfully Created", Toast.LENGTH_SHORT).show();

                                            // Update the document with the budget code
                                            newDocRef1.update("bid", newDocRef1.getId())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("TAG", "Bid updated successfully.");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("TAG", "Error updating Bid.", e);
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("MAP", "Error adding new document", e);
                                            Toast.makeText(getApplicationContext(), "Budget Failed To Created", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    //update previous budget repeat status
                                    DocumentReference dRef = myCollectionRef.document(bbidd);

                                    Map<String, Object> data1 = new HashMap<>();
                                    data1.put("repeat_status", "NO");

                                    dRef.update(data1).addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "Budget updated successfully.");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error updating document
                                            Log.w("TAG", "Error updating Budget", e);
                                        }
                                    });
                                }else{
                                    Notification notification1 = buildForegroundNotification1(budgetName);
                                    startForeground(2, notification1);
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

            }
        };
                // Create a Calendar object and set it to the current time
                // Calendar cal1 = Calendar.getInstance();

                // Set the calendar to the 1st day of the month at 12:00:00 AM
                // cal.set(Calendar.DAY_OF_MONTH, 1);
                // cal.set(Calendar.HOUR_OF_DAY, 0);
                // cal.set(Calendar.MINUTE, 0);
                // cal.set(Calendar.SECOND, 0);
                // cal.set(Calendar.MILLISECOND, 0);

                // Schedule the task to run every month at the specified time
                // mTimer.schedule(mTimerTask, cal1.getTime(), TimeUnit.MILLISECONDS.convert(restdayofmonth, TimeUnit.DAYS));
//                Log.e("Restdayofthemonth: "," " + restdayofmonth);

                // Schedule the task to run every minute at the specified time
//                mTimer.schedule(mTimerTask, cal1.getTime(), TimeUnit.SECONDS.toMillis(1));
                mTimer.schedule(mTimerTask, 60 * 1000, 60 * 1000);
    }

    private void stopTimer() {
        Log.d(TAG, "stopTimer: Timer stopped");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Service")
                .setContentText("Running in foreground")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    private Notification buildForegroundNotification1(String budgetName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("This Year Budget-Auto-Creation-Service End")
                .setContentText("Please Renew " + budgetName + " By Recreate Manually To Continue The Auto Creation Service")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    private void removeForegroundNotification() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

