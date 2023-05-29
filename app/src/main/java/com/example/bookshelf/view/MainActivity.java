package com.example.bookshelf.view;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.repository.Repository;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Repository.init(getApplicationContext());
        EntryController.init(getApplicationContext());
        if (EntryController.isUserLoggedIn()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new BookListFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        Repository.close();
        super.onDestroy();
    }
}