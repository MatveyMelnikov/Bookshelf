package com.example.bookshelf.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentFamilyBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.FamilyConverter;
import com.example.bookshelf.repository.converters.UserConverter;
import com.example.bookshelf.repository.objects.Family;
import com.example.bookshelf.repository.objects.RepositoryObject;
import com.example.bookshelf.repository.objects.User;
import com.example.bookshelf.view.recyclerview.FamilyAdapter;
import com.example.bookshelf.view.recyclerview.RecyclerListener;

import java.util.ArrayList;

public class FamilyFragment extends Fragment implements RecyclerListener {
    FragmentFamilyBinding binding;
    public FamilyFragment() {}

    public static FamilyFragment newInstance() {
        return new FamilyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFamilyBinding.inflate(inflater, container, false);
        changeTitle("Family group");
        setBackButtonHandler();

        FamilyConverter converter = new FamilyConverter();

        if (EntryController.getLoggedUser().getFamilyId() == 0) {
            binding.createFamily.setVisibility(View.VISIBLE);
        } else {
            binding.addFamilyMember.setVisibility(View.VISIBLE);

            ArrayList<User> familyMembers = Repository.getArrayOfAllFamilyMembers();

            Family currentFamily = (Family) Repository.selectObject(
                    EntryController.getLoggedUser().getFamilyId(),
                    converter
            );
            FamilyAdapter familyAdapter = new FamilyAdapter(
                    this,
                    familyMembers,
                    currentFamily
            );

            binding.familyList.setAdapter(familyAdapter);
            binding.familyList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        }

        binding.createFamily.setOnClickListener(v -> {
            RepositoryObject newFamily = new Family(
                    0, EntryController.getLoggedUser().getId()
            );

            Repository.insertNewObject(newFamily, converter);
            binding.createFamily.setVisibility(View.GONE);
            binding.addFamilyMember.setVisibility(View.VISIBLE);

            Family family = (Family) Repository.selectObject(newFamily, converter);

            assert family != null;
            EntryController.getLoggedUser().setFamilyId(family.getId());
            Repository.updateObject(EntryController.getLoggedUser(), new UserConverter());
        });

        binding.addFamilyMember.setOnClickListener(v -> selectSource());

        return binding.getRoot();
    }

    private void selectSource() {
        final CharSequence[] optionsMenu = {"Register child account", "Add account" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setItems(
                optionsMenu,
                (dialogInterface, i) -> {
                    if (optionsMenu[i].equals("Register child account")) {
                        startFragment(
                                RegisterFragment.newInstance(false),
                                "registration"
                        );
                    } else if (optionsMenu[i].equals("Add account")) {
                        startFragment(AddMemberFragment.newInstance(), "add_member");
                    }
                }
        );
        builder.show();
    }

    private void startFragment(Fragment fragment, String name) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(name)
                .commit();
    }

    private void changeTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    private void setBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BookListFragment.handleBackButton(
                        requireActivity(), getParentFragmentManager()
                );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), callback
        );
    }

    static public void handleBackButton(Activity context, FragmentManager fragmentManager) {
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Family group");
        }
        fragmentManager.popBackStack();
    }

    @Override
    public void onElementClick(int index) {
        return;
    }

    @Override
    public void onLongElementClick(int index) {
        return;
    }
}