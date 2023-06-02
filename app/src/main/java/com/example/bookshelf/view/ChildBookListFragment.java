package com.example.bookshelf.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentChildBookListBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.User;
import com.example.bookshelf.view.recyclerview.BookAdapter;
import com.example.bookshelf.view.recyclerview.RecyclerListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ChildBookListFragment extends Fragment
        implements RecyclerListener, MenuProvider, FragmentResultListener {
    private FragmentChildBookListBinding binding;
    private static final String USER_PARAM = "user";
    private static final String ADD_BOOK_FRAGMENT_NAME = "addBookFragment";
    private User user;
    private ArrayList<Book> states = new ArrayList<>();

    public ChildBookListFragment() {}

    public static ChildBookListFragment newInstance(User user) {
        ChildBookListFragment fragment = new ChildBookListFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_PARAM, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_PARAM);
            states = Repository.getArrayOfBookModels(user.getId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChildBookListBinding.inflate(inflater, container, false);

        setActionBar();
        setBackButtonHandler();

        BookAdapter adapter = new BookAdapter(
                new WeakReference<>(getContext()), this, states
        );
        binding.childBookList.setAdapter(adapter);
        binding.childBookList.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        binding.addBookToChildList.setOnClickListener(view1 ->
                startAddBookFragment(null)
        );

        return binding.getRoot();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        FamilyFragment.handleBackButton(requireActivity(), getParentFragmentManager());
        return true;
    }

    @Override
    public void onElementClick(int index) {}

    @Override
    public void onLongElementClick(int index) {
        selectActionsOnBook(index);
    }

    private void selectActionsOnBook(int index) {
        final CharSequence[] optionsMenu = { "Edit", "Delete" };
        BookAdapter bookAdapter = (BookAdapter) binding.childBookList.getAdapter();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setItems(
                optionsMenu,
                (dialogInterface, i) -> {
                    assert bookAdapter != null;
                    Book book = bookAdapter.getItem(index);
                    if (optionsMenu[i].equals("Edit")) {
                        startAddBookFragment(book);

                    } else if (optionsMenu[i].equals("Delete")) {
                        BookConverter bookConverter = new BookConverter();
                        File localFile = new File(book.getPdf());
                        localFile.delete();

                        Repository.deleteObject(book.getId(), bookConverter);
                        bookAdapter.deleteItem(index);
                    }
                }
        );
        builder.show();
    }

    private void setActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar == null)
            return;

        actionBar.setTitle("Book list of " + user.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setBackButtonHandler() {
        requireActivity().addMenuProvider(
                this, getViewLifecycleOwner(), Lifecycle.State.CREATED
        );
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

    static public void handleBackButton(
            Activity context,
            FragmentManager fragmentManager,
            User user
    ) {
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar == null)
            return;

        actionBar.setTitle("Book list of " + user.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        fragmentManager.popBackStack();
    }

    @Override
    public void handleResult() {
        states = Repository.getArrayOfBookModels(user.getId());
        BookAdapter adapter = new BookAdapter(
                new WeakReference<>(getContext()), this, states
        );
        binding.childBookList.setAdapter(adapter);
    }

    private void startAddBookFragment(Book book) {
        AddBookFragment fragment = AddBookFragment.newInstance(
                user,
                book == null ? 0 : book.getId()
        );
        fragment.setResultListener(this);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(ADD_BOOK_FRAGMENT_NAME)
                .commit();
    }
}