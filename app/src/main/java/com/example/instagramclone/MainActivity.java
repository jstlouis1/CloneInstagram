package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static BottomNavigationView bottom_navigation;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment HomeFragment = new PostFragment();
    final Fragment PostFragment = new ComposeFragment();
    final Fragment AccountFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // click on the bottom navigation and check the item selected
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new Fragment();
                switch (item.getItemId()){
                    case R.id.action_home:
                        fragment = HomeFragment;
                        break;

                    case R.id.action_plus:
                        fragment = PostFragment;
                        break;

                    case R.id.action_account:
                    default:
                        fragment = AccountFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
                return true;
            }
        });
        bottom_navigation.setSelectedItemId(R.id.action_home);
    }
}