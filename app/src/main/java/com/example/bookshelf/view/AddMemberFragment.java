package com.example.bookshelf.view;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.databinding.FragmentAddMemberBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.RepositoryConverter;
import com.example.bookshelf.repository.converters.UserConverter;
import com.example.bookshelf.repository.objects.User;

public class AddMemberFragment extends Fragment {
    FragmentAddMemberBinding binding;

    public AddMemberFragment() {}

    public static AddMemberFragment newInstance() {
        return new AddMemberFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false);
        changeTitle("Adding a family member");
        setBackButtonHandler();

        binding.inviteMember.setOnClickListener(v -> {
            String login = binding.editTextLoginOfFamilyMember.getText().toString();

            RepositoryConverter converter = new UserConverter();
            User criterion = new User(0, login, "", false, 0);
            User foundUser = (User) Repository.selectObject(criterion, converter);

            if (foundUser == null) {
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            foundUser.setFamilyId(EntryController.getLoggedUser().getFamilyId());
            Repository.updateObject(foundUser, converter);

            getParentFragmentManager().popBackStack(
                    "add_member",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        });

        return binding.getRoot();
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
                FamilyFragment.handleBackButton(
                        requireActivity(), getParentFragmentManager()
                );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), callback
        );
    }
}