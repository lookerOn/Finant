package com.example.finant;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment{

    private BarChart mBarChart;
    private FirebaseFirestore mFirestore;
    String userId,budgetId,budgetcur;
    RecyclerView recyclerView;
    ImageView wallet;

    // Define a list to store the expenses for each fetch
    List<Map<String, Float>> expensesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mBarChart = v.findViewById(R.id.chart);
        recyclerView = v.findViewById(R.id.recycler);
        wallet = v.findViewById(R.id.walletbtn);
        mFirestore = FirebaseFirestore.getInstance();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChartData();
    }
    private void loadChartData() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        CollectionReference budgetssRef = mFirestore.collection("budget");
        Query query = budgetssRef.whereEqualTo("user_ID", userId);

        query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();

                        //----
                        if (!snapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : snapshot) {
                                budgetId = document.getString("bid"); // get budget ID
                                // do something with budgetId

                                //2nd version of fetch data code
                                // query expenses for current budget
                                CollectionReference expensesRef = mFirestore.collection("budget").document(budgetId).collection("expenses");

                                // Fetch expenses from Firestore
                                expensesRef.get().addOnCompleteListener(expensesTask -> {
                                    if (expensesTask.isSuccessful()) {
                                        QuerySnapshot expensesSnapshot = expensesTask.getResult();

                                        // Calculate total expenses for each month and year
                                        if (!expensesSnapshot.isEmpty()) {
                                            Map<String, Float> expensesByMonthForThisFetch = new HashMap<>();
                                            for (QueryDocumentSnapshot expenseDocument : expensesSnapshot) {
                                                String date = expenseDocument.getString("EXP_date");
                                                float amount = expenseDocument.getDouble("EXP_Amount").floatValue();

                                                // Extract month and year from date string
                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                                Date expDate = null;
                                                try {
                                                    expDate = sdf.parse(date);
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTime(expDate);
                                                int month = cal.get(Calendar.MONTH) + 1;
                                                int year = cal.get(Calendar.YEAR);
                                                String monthYear = String.format("%02d-%04d", month, year);

                                                // Add expense to the expenses for this fetch
                                                if (expensesByMonthForThisFetch.containsKey(monthYear)) {
                                                    expensesByMonthForThisFetch.put(monthYear, expensesByMonthForThisFetch.get(monthYear) + amount);
                                                } else {
                                                    expensesByMonthForThisFetch.put(monthYear, amount);
                                                }
                                            }

                                            // Add the expenses for this fetch to the list
                                            expensesList.add(expensesByMonthForThisFetch);
                                            Log.e("CHECKING","" + expensesList);
                                        }

                                        // Loop through the list of expenses to calculate the overall expenses for each month and year
                                        Map<String, Float> expensesByMonth = new HashMap<>();
                                        for (Map<String, Float> expenses : expensesList) {
                                            for (Map.Entry<String, Float> entry : expenses.entrySet()) {
                                                String monthYear = entry.getKey();
                                                float totalAmount = entry.getValue();

                                                if (expensesByMonth.containsKey(monthYear)) {
                                                    expensesByMonth.put(monthYear, expensesByMonth.get(monthYear) + totalAmount);
                                                } else {
                                                    expensesByMonth.put(monthYear, totalAmount);
                                                }
                                            }
                                        }

                                        // Sort the expensesByMonth map by key
                                        List<Map.Entry<String, Float>> sortedExpenses = new ArrayList<>(expensesByMonth.entrySet());
                                        Collections.sort(sortedExpenses, new Comparator<Map.Entry<String, Float>>() {
                                            @Override
                                            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                                SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
                                                try {
                                                    Date date1 = sdf.parse(o1.getKey());
                                                    Date date2 = sdf.parse(o2.getKey());
                                                    return date1.compareTo(date2);
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });

                                        // Create BarEntry for each month and year with the corresponding total expense
                                        List<BarEntry> entries = new ArrayList<>();
                                        int i = 0;
                                        for (Map.Entry<String, Float> entry : sortedExpenses) {
                                            String monthYear = entry.getKey();
                                            float totalAmount = entry.getValue();
                                            Log.e("ExpensesByMonth", "Month/Year: " + entry.getKey() + ", Total Expenses: " + entry.getValue());
                                            String label = monthYear.substring(0, 3) + " " + monthYear.substring(3);
                                            entries.add(new BarEntry(i++, totalAmount, label));
                                        }

                                        // Set up the BarChart
                                        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
                                        dataSet.setColor(Color.rgb(213,255,232));
                                        dataSet.setValueTextSize(12f);
                                        dataSet.setValueTextColor(Color.CYAN);

                                        BarData data = new BarData(dataSet);
                                        mBarChart.setData(data);

                                        Description description = mBarChart.getDescription();
                                        description.setText("");

                                        // Enable drag and set other chart properties here
                                        mBarChart.setDragEnabled(true);
                                        mBarChart.setScaleEnabled(true); // Disable scaling to avoid issues with drag
                                        mBarChart.getBarData().setBarWidth(0.9f);

                                        // Set X-axis labels
                                        XAxis xAxis = mBarChart.getXAxis();
                                        xAxis.setTextColor(Color.WHITE);
                                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                        xAxis.setGranularity(1);
                                        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisLabels(expensesByMonth.keySet())));

                                        // Set Y-axis labels
                                        YAxis yAxis = mBarChart.getAxisLeft();
                                        yAxis.setTextColor(Color.WHITE);
                                        yAxis.setValueFormatter(new ValueFormatter() {
                                            @Override
                                            public String getFormattedValue(float value) {
                                                return "$" + String.format(Locale.getDefault(), "%.2f", value);
                                            }
                                        });


                                        mBarChart.getAxisRight().setEnabled(false);
                                        mBarChart.animateY(800, Easing.EaseInOutQuad);

                                        // Set up the Legend
                                        Legend legend = mBarChart.getLegend();
                                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                                        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                                        legend.setTextColor(Color.CYAN);
                                        legend.setDrawInside(false);
                                        legend.setXEntrySpace(10f);
                                        legend.setYEntrySpace(5f);
                                        legend.setYOffset(10f);

                                        //Set the bar onclick
                                        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                            @Override
                                            public void onValueSelected(Entry e, Highlight h) {
                                                // Get the label of the selected bar
                                                String label = e.getData().toString();

                                                // Extract the month and year from the label
                                                int month = Integer.parseInt(label.substring(0, 2));
                                                int year = Integer.parseInt(label.substring(3).trim());

                                                // Filter the expenses by month and year
                                                List<Expenses2forchart> expensesByMonthYearView = new ArrayList<>();

                                                CollectionReference budgetssRef = mFirestore.collection("budget");
                                                Query query2 = budgetssRef.whereEqualTo("user_ID", userId);


                                                query2.get().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot snapshot = task.getResult();

                                                        //----
                                                        if (!snapshot.isEmpty()) {
                                                            for (QueryDocumentSnapshot document : snapshot) {
                                                                budgetId = document.getString("bid"); // get budget ID
                                                                // do something with budgetId

                                                                //2nd version of fetch data code
                                                                // query expenses for current budget
                                                                CollectionReference expensesRef5 = mFirestore.collection("budget").document(budgetId).collection("expenses");

                                                                // Fetch expenses from Firestore
                                                                expensesRef5.get().addOnCompleteListener(expensesTask1 -> {
                                                                    if (expensesTask1.isSuccessful()) {
                                                                        QuerySnapshot expenses1Snapshot = expensesTask1.getResult();

                                                                        // Calculate total expenses for each month and year
                                                                        if (!expenses1Snapshot.isEmpty()) {

                                                                            for (QueryDocumentSnapshot expenseDoc : expenses1Snapshot) {
                                                                                String dateexp = expenseDoc.getString("EXP_date");
                                                                                double amount = expenseDoc.getDouble("EXP_Amount").floatValue();
                                                                                String curren = expenseDoc.getString("currency");
                                                                                String des = expenseDoc.getString("EXP_Des");
                                                                                double cvrtamount = expenseDoc.getDouble("EXPCV_Amount").floatValue();

                                                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                                                                Date expDate = null;
                                                                                try {
                                                                                    expDate = sdf.parse(dateexp);
                                                                                } catch (ParseException ex) {
                                                                                    throw new RuntimeException(ex);
                                                                                }
                                                                                Calendar cal = Calendar.getInstance();
                                                                                cal.setTime(expDate);
                                                                                int expMonth = cal.get(Calendar.MONTH) + 1;
                                                                                int expYear = cal.get(Calendar.YEAR);

                                                                                if (expMonth == month && expYear == year) {
                                                                                    // Add the expense to the list
                                                                                    expensesByMonthYearView.add(new Expenses2forchart(dateexp, amount, curren, des, cvrtamount));

                                                                                }
                                                                            }

                                                                            // Create and set up the RecyclerView adapter
                                                                            ChartExpenseAdapter adapter = new ChartExpenseAdapter(expensesByMonthYearView);
                                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                                            recyclerView.setAdapter(adapter);
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onNothingSelected() {

                                            }
                                        });

                                        mBarChart.invalidate();

                                    }
                                });
                            }
                        }
                    }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userId = user.getUid();
                CollectionReference WalletRef = mFirestore.collection("e-wallet");
                Query query = WalletRef.whereEqualTo("user_ID", userId);

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot walletsnapshot = task.getResult();

                        //----
                        if (!walletsnapshot.isEmpty()) {

                            mBarChart.setVisibility(View.GONE);

                            Fragment newFragment = new WalletLoginFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                            //passing budgetID
                            Bundle args = new Bundle();
                            args.putString("userID", userId);
                            newFragment.setArguments(args);

                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.expana, newFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

                        }else{
                            // Wallet does not exist, show alert dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("E-WALLET ACCOUNT ACTIVATION");
                            builder.setMessage("Seems that you haven't got one account, ACTIVATE now?");
                            builder.setPositiveButton("ACTIVATE", (dialog, which) -> {

                                mBarChart.setVisibility(View.GONE);

                                // Go to activate account fragment
                                Fragment newFragment = new ActivateAccountFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                //passing budgetID
                                Bundle args = new Bundle();
                                args.putString("userID", userId);
                                newFragment.setArguments(args);

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.expana, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            });
                            builder.setNegativeButton("NO", (dialog, which) -> {
                                // Do nothing
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });
    }
    @NonNull
    private String[] getXAxisLabels(@NonNull Set<String> monthYears) {
        String[] labels = new String[monthYears.size()];
        int i = 0;
        List<String> sortedMonthYears = new ArrayList<>(monthYears);
        Collections.sort(sortedMonthYears, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] parts1 = o1.split("-");
                String[] parts2 = o2.split("-");
                int year1 = Integer.parseInt(parts1[1]);
                int year2 = Integer.parseInt(parts2[1]);
                int month1 = Integer.parseInt(parts1[0]);
                int month2 = Integer.parseInt(parts2[0]);
                if (year1 != year2) {
                    return Integer.compare(year1, year2);
                } else {
                    return Integer.compare(month1, month2);
                }
            }
        });
        for (String monthYear : sortedMonthYears) {
            String[] parts = monthYear.split("-");
            String month = parts[0];
            String year = parts[1];
            labels[i++] = getMonthName(Integer.parseInt(month)) + " " + year;
        }
        return labels;
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
            default:
                throw new IllegalArgumentException("Invalid month number: " + month);
        }
    }
}