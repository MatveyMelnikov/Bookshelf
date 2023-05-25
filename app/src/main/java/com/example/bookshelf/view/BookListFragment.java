package com.example.bookshelf.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentBookListBinding;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.view.recyclerview.BookAdapter;
import com.example.bookshelf.view.recyclerview.RecyclerListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BookListFragment extends Fragment implements RecyclerListener, MenuProvider {
    FragmentBookListBinding binding;
    private ArrayList<Book> states = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitle("Welcome, " + EntryController.getLoggedUser().getName() + "!");

        states = Repository.getArrayOfBookModels(EntryController.getLoggedUser().getId());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentBookListBinding.inflate(inflater, container, false);

        BookAdapter adapter = new BookAdapter(
                new WeakReference<>(getContext()), this, states
        );
        binding.bookList.setAdapter(adapter);
        binding.bookList.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        binding.floatingActionButton.setOnClickListener(view1 ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, AddBookFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
        );

        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        return binding.getRoot();
    }

    @Override
    public void onElementClick(int index) {
        BookAdapter bookAdapter = (BookAdapter) binding.bookList.getAdapter();
        assert bookAdapter != null;

        Fragment fragment = new ViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", bookAdapter.getItem(index));
        fragment.setArguments(bundle);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();

        bookAdapter.addBookToBeginning(index);
    }

    @Override
    public void onLongElementClick(int index) {
        selectActionsOnBook(index);
    }

    void selectActionsOnBook(int index) {
        final CharSequence[] optionsMenu = {"Edit", "Delete" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setItems(
                optionsMenu,
                (dialogInterface, i) -> {
                    BookAdapter bookAdapter = (BookAdapter) binding.bookList.getAdapter();
                    assert bookAdapter != null;
                    Book book = bookAdapter.getItem(index);
                    if (optionsMenu[i].equals("Edit")) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("editableBook", book);
                        Fragment fragment = new AddBookFragment();
                        fragment.setArguments(bundle);

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .addToBackStack(null)
                                .commit();

                    } else if (optionsMenu[i].equals("Delete")) {
                        BookConverter bookConverter = new BookConverter();
                        com.example.bookshelf.repository.objects.Book bookDB =
                                (com.example.bookshelf.repository.objects.Book)
                                        Repository.selectObject(book.id, bookConverter);
                        assert bookDB != null;
                        File localFile = new File(bookDB.getPdf());
                        localFile.delete();

                        Repository.deleteObject(book.id, bookConverter);
                        bookAdapter.deleteItem(index);
                    }
                }
        );
        builder.show();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.pop_up_menu, menu);
    }

    private void startFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_logout:
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, LoginFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                changeTitle("Bookshelf");
                EntryController.logOut();
                return true;
            case R.id.action_quotes:
//                QuotesFragment fragment = QuotesFragment.newInstance(Repository.currentBookId);
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.fragmentContainerView, fragment, null)
//                        .setReorderingAllowed(true)
//                        .addToBackStack(null)
//                        .commit();
                startFragment(QuotesFragment.newInstance(Repository.currentBookId));
                return true;
            case R.id.action_family:
//                FamilyFragment fragment = FamilyFragment.newInstance();
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.fragmentContainerView, fragment, null)
//                        .setReorderingAllowed(true)
//                        .addToBackStack(null)
//                        .commit();
                startFragment(FamilyFragment.newInstance());
                return true;
            default:
                return false;
        }

//        if (menuItem.getItemId() == R.id.action_logout) {
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragmentContainerView, LoginFragment.class, null)
//                    .setReorderingAllowed(true)
//                    .commit();
//            changeTitle("Bookshelf");
//            EntryController.logOut();
//            return true;
//        }
        //return false;
    }

    private void changeTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    static public void handleBackButton(Activity context, FragmentManager fragmentManager) {
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setTitle("Welcome, " + EntryController.getLoggedUser().getName() + "!");
        }
        fragmentManager.popBackStack();
    }
}