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
    final FragmentManager fragmentTransaction = getSupportFragmentManager();
    final Fragment postFrag = new PostFrag();
    final Fragment composeFrag = new ComposeFrag();
    final Fragment profileFrag = new ProfileFrag();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = new Fragment();
                if (item.getItemId()==R.id.Home){frag = postFrag; }
                if(item.getItemId()== R.id.Composetouch) {frag = composeFrag;}
                if(item.getItemId()==R.id.Aprofile){frag = profileFrag;}
                fragmentTransaction.beginTransaction().replace(R.id.Placeholder, frag).commit();
                return true;
            }


        });
        bottom_navigation.setSelectedItemId(R.id.Home);
    }
}