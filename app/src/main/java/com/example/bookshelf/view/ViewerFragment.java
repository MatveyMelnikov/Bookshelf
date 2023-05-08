package com.example.bookshelf.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookshelf.R;

//public class ViewerFragment extends Fragment implements MenuProvider {
public class ViewerFragment extends Fragment {
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View fragmentView = inflater.inflate(
                R.layout.fragment_viewer, container, false
        );

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.viewer_action_bar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView bottomText = fragmentView.findViewById(R.id.bottomText);
            bottomText.setText(bundle.getString("author") + " - " +
                    bundle.getString("name"));
        }
        //requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        return fragmentView;
    }

//    @Override
//    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//        menuInflater.inflate(R.menu.action_bar, menu);
//    }
//
//    @Override
//    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//        return false;
//    }
}