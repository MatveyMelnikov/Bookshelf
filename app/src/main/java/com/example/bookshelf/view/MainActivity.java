package com.example.bookshelf.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.repository.Repository;

import java.util.List;

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
        //askForFilePermissions();
    }

    public boolean askForFilePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            if (!hasPermission) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return true;
            }
        }

        return false;
    }

    public void startAddBookFragment(Bundle bundle) {
        Fragment fragment = new AddBookFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void startBookListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new BookListFragment())
                .commit();
    }

    public void startViewerFragment(Bundle bundle) {
        Fragment fragment = new ViewerFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        Repository.close();
        super.onDestroy();
    }

    //    void handleBackButton() {
//        int count = getSupportFragmentManager().getBackStackEntryCount();
//        if (count == 0)
//            super.onBackPressed();
//        else
//            getSupportFragmentManager().popBackStack();
//
//        ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
//            actionBar.setDisplayShowCustomEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setTitle("Bookshelf");
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        handleBackButton();
//    }
}