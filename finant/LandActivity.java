package com.example.finant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class LandActivity extends AppCompatActivity {

    BottomNavigationView bnv;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        //this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bnv = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        bnv.setSelectedItemId(R.id.home);

        //for vibration
        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.budget:
                        //Set vibration Time in Ms
                        vb.vibrate(80);
                        fragment = new AddBudget();
                        break;

                    case R.id.expenses:
                        vb.vibrate(80);
                        fragment = new ExpensesFragment();
                        break;

                    case R.id.home:
                        vb.vibrate(80);
                        fragment = new HomeFragment();
                        break;

                    case R.id.user:
                        vb.vibrate(80);
                        fragment = new UserFragment();
                        break;

                    case R.id.PDF:
                        vb.vibrate(80);
                        fragment = new ReportFragment();
                        break;

//                    case R.id.ewalet:
//                        vb.vibrate(80);
//                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });
    }
}