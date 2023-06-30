package com.example.finant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ReportFragment extends Fragment {

    TextInputEditText monthOpt,yearOpt;
    String budgetId, userId, expDateStr, SlecExpYearStr, SlecExpMontStr,budgetcur, budgetnm,expId,expAmount,expCVAmount,expCur,expDt,expDE,englishmonthform, expCTime;
    int SlecExpYear;
    int SlecExpMont;
    Button btn_search;
    Uri pdfUri;

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 = inflater.inflate(R.layout.fragment_report, container, false);

        monthOpt = v1.findViewById(R.id.MONTHOpt);
        yearOpt = v1.findViewById(R.id.expenseyear);
        btn_search = v1.findViewById(R.id.btn_ADDExp);

        ArrayList<Expense> selectedExpenses = new ArrayList<>();

        btn_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                SlecExpYearStr = yearOpt.getText().toString();
                SlecExpMontStr = monthOpt.getText().toString();

                if(SlecExpMontStr != null && SlecExpYearStr != null){

                    SlecExpYear = Integer.parseInt(SlecExpYearStr);
                    SlecExpMont = Integer.parseInt(SlecExpMontStr);

                    if(SlecExpMontStr.equals("1")){
                        englishmonthform = "JAN";
                    } else if (SlecExpMontStr.equals("2")) {
                        englishmonthform = "FEB";
                    } else if (SlecExpMontStr.equals("3")) {
                        englishmonthform = "MAR";
                    } else if (SlecExpMontStr.equals("4")) {
                        englishmonthform = "APR";
                    } else if (SlecExpMontStr.equals("5")) {
                        englishmonthform = "MAY";
                    } else if (SlecExpMontStr.equals("6")) {
                        englishmonthform = "JAN";
                    } else if (SlecExpMontStr.equals("7")) {
                        englishmonthform = "JUL";
                    } else if (SlecExpMontStr.equals("8")) {
                        englishmonthform = "AUG";
                    } else if (SlecExpMontStr.equals("9")) {
                        englishmonthform = "SEP";
                    } else if (SlecExpMontStr.equals("10")) {
                        englishmonthform = "OCT";
                    } else if (SlecExpMontStr.equals("11")) {
                        englishmonthform = "NOV";
                    } else {
                        englishmonthform = "DEC";
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userId = user.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference budgetssRef = db.collection("budget");
                    Query query = budgetssRef.whereEqualTo("user_ID", userId);

                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();

                            //----
                            if (!snapshot.isEmpty()){
                                for (QueryDocumentSnapshot document : snapshot) {
                                    budgetId = document.getString("bid"); // get budget ID
                                    // do something with budgetId

                                    // query expenses for current budget
                                    CollectionReference expensesRef = db.collection("budget").document(budgetId).collection("expenses");
                                    expensesRef.get().addOnCompleteListener(expensesTask -> {
                                        if (expensesTask.isSuccessful()) {

                                            //----
                                            QuerySnapshot expensesSnapshot = expensesTask.getResult();

                                            if(!expensesSnapshot.isEmpty()){

                                                for (QueryDocumentSnapshot expenseDocument : expensesSnapshot) {
                                                    expDateStr = expenseDocument.getString("EXP_date");
                                                    // do something with expense date

                                                    // Construct a SimpleDateFormat object for parsing dates in dd/MM/yyyy format
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                                    // Parse the EXP_date string to a Date object using the SimpleDateFormat
                                                    Date expDate = null;
                                                    try {
                                                        expDate = sdf.parse(expDateStr);
                                                    } catch (ParseException e) {
                                                        throw new RuntimeException(e);
                                                    }

                                                    // Extract the month and year from the EXP_date Date object
                                                    Calendar cal = Calendar.getInstance();
                                                    cal.setTime(expDate);
                                                    int expMonth = cal.get(Calendar.MONTH) + 1; // 0-indexed
                                                    int expYear = cal.get(Calendar.YEAR);

                                                    if (expMonth == SlecExpMont && expYear == SlecExpYear) {
                                                        String budId = expenseDocument.getString("bid");

                                                        //get budget currency
                                                        Query query3 = budgetssRef.whereEqualTo("bid", budId);
                                                        query3.get().addOnCompleteListener(tasksnap -> {
                                                            if (task.isSuccessful()) {
                                                                QuerySnapshot snapshot2 = tasksnap.getResult();

                                                                if (!snapshot2.isEmpty()){

                                                                    for (QueryDocumentSnapshot documentbud : snapshot2) {
                                                                        budgetcur = documentbud.getString("bcurrency"); // get budget currency
                                                                        budgetnm = documentbud.getString("budget_name"); // get budget name

                                                                        //get expense detail
                                                                        expId = expenseDocument.getString("eid");
                                                                        expAmount = String.valueOf(expenseDocument.getDouble("EXP_Amount"));
                                                                        expCVAmount = String.valueOf(expenseDocument.getDouble("EXPCV_Amount"));
                                                                        expCur = expenseDocument.getString("currency");
                                                                        expDt = expenseDocument.getString("EXP_date");
                                                                        expDE = expenseDocument.getString("EXP_Des");
                                                                        expCTime = Long.toString(expenseDocument.getLong("timeStamp"));

//                                                                        String expCTime = Long.toString(expCTimeString);

                                                                        // Create a string that concatenates all the expense details
                                                                        Expense expense = new Expense(expId, expDt, expAmount, expDE, expCur, expCVAmount, budgetnm, budgetcur, expCTime);
                                                                        selectedExpenses.add(expense); // add the string to the ArrayList
                                                                        generatePdf(selectedExpenses);
                                                                    }
                                                                }else{
                                                                    Log.e("Failed Fetching", "Failed to fetch expenses data even got");
                                                                    Toast.makeText(getActivity(), "Failed to fetch expenses data even got", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        Log.e("Error Fecthing expenses", "Failed to fetch expenses data inner");
                                                    }
                                                }
                                            }
                                            //----
                                        } else {
                                            Log.e("Error Fecthing expenses", "Failed to fetch expenses data");
                                            Toast.makeText(getActivity(), "Failed to fetch expenses data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else{
                                Log.e("Error Fecthing budget","No Budget Data Found");
                                Toast.makeText(getActivity(), "No Budget Data Found", Toast.LENGTH_SHORT).show();
                            }
                            //----
                        }else{
                            Log.e("Error Fecthing budget","Failed to fetch budget data");
                            Toast.makeText(getActivity(), "Failed to fetch Budget", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Log.e("Null Input Value","Input Value Can't be Null");
                    Toast.makeText(getActivity(), "Input Value Can't be Null", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return v1;
    }

    private void generatePdf(ArrayList<Expense> selectedExpenses) {
        Document document = new Document();

        // Sort the selected expenses by date and timestamp
        Collections.sort(selectedExpenses, new Comparator<Expense>() {
            @Override
            public int compare(Expense e1, Expense e2) {
                // Compare the dates of the two expenses
                String date1 = e1.getEXP_date();
                String date2 = e2.getEXP_date();
                int dateCompareResult = date1.compareTo(date2);
                if (dateCompareResult != 0) {
                    // If the dates are not equal, return the result of comparing the dates
                    return dateCompareResult;
                } else {
                    // If the dates are equal, compare the timestamps
                    long timestamp1 = Long.parseLong(e1.getTimeStamp());
                    long timestamp2 = Long.parseLong(e2.getTimeStamp());

                    if (timestamp1 < timestamp2) {
                        return -1;
                    } else if (timestamp1 > timestamp2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        try {
            // Create a new PDF document
            File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "expenses.pdf");
            FileOutputStream outputStream = new FileOutputStream(file);

            //get the file path
            String absolutePath = file.getAbsolutePath();
            Log.d("PDF", "PDF saved to: " + absolutePath);
//            Toast.makeText(getActivity(), "PDF saved to"+ absolutePath, Toast.LENGTH_SHORT).show();

            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add a title to the PDF document
            Font font = FontFactory.getFont(StandardFonts.HELVETICA, 35, BaseColor.RED);
            Paragraph title = new Paragraph("EXPENSES REPORT FOR " + englishmonthform + " " + SlecExpYearStr + "\n\n");
            title.setAlignment(Element.ALIGN_CENTER);
            title.setFont(font);
            document.add(title);

            // Create a PdfPTable object
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(110);

            // Add column headers to the table
            table.addCell("EXPENSES ID");
            table.addCell("EXPENSE AMOUNT");
            table.addCell("EXPENSES DATE");
            table.addCell("EXPENSE DESCRIPTION");
            table.addCell("EXPENSES CURRENCY");
            table.addCell("EXPENSE CONVETED AMOUNT");
            table.addCell("BUDGET NAME");
            table.addCell("BUDGET CURRENCY");

            // Add the selected expenses to the table
            for (Expense expense : selectedExpenses) {
                table.addCell(expense.getEid());
                table.addCell(expense.getEXP_Amount());
                table.addCell(expense.getEXP_date());
                table.addCell(expense.getEXP_Des());
                table.addCell(expense.getCurrency());
                table.addCell(expense.getEXPCV_Amount());
                table.addCell(expense.getBudget_name());
                table.addCell(expense.getBcurrency());
            }

            // Add the table to the PDF document
            document.add(table);

            // Close the PDF document
            document.close();


            // Define the file URI using the FileProvider
            File pdfFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "expenses.pdf");
            pdfUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);

            // Create an AlertDialog with Preview and Download buttons
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Preview the PDF file?")
                    .setPositiveButton("Preview", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Display the PDF document to the user using an Intent with the file URI
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(pdfUri, "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to the URI
                            startActivity(intent);
                            Toast.makeText(getActivity(), "Successfully Generate PDF", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        } catch (DocumentException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
