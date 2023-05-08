package com.example.bookshelf.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.DataController;
import com.example.bookshelf.view.recyclerview.BookAdapter;
import com.example.bookshelf.view.recyclerview.RecyclerListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BookListFragment extends Fragment implements RecyclerListener, MenuProvider {
    private RecyclerView recyclerView;
    private int previousStatesSize = 0;
    private ArrayList<Book> states = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            DataController.init(requireContext());
            setInitialData();
        } else {
            if (previousStatesSize == DataController.books.size()) {
                states = DataController.getBooksArrayFromJSON(
                        savedInstanceState.getString("states")
                );
            } else {
                setInitialData();
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        recyclerView = view.findViewById(R.id.bookList);
        BookAdapter adapter = new BookAdapter(
                new WeakReference<>(getContext()), this, states
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        view.findViewById(R.id.floatingActionButton).setOnClickListener(view1 ->
                //((MainActivity) requireActivity()).startAddBookFragment(null)
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, AddBookFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
        );

        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        return view;
    }

    private void setInitialData() {
        /*
        for (int i = 1; i <= 5; i++) {
            states.add(new Book("Name" + i, "Author" + i));
        }*/
        states.clear();
        states.addAll(DataController.books);
        previousStatesSize = 0;
    }

    @Override
    public void onElementClick(int index) {
        DataController.addBookToBeginning(index);

        Book book = DataController.books.get(0);
        Bundle bundle = new Bundle();
        bundle.putString("name", book.name);
        bundle.putString("author", book.author);
        ((MainActivity) requireActivity()).startViewerFragment(bundle);

        BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
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
                    BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
                    if (optionsMenu[i].equals("Edit")) {
                        Bundle bundle = new Bundle();
                        Book book = bookAdapter.getItem(index);
                        bundle.putString("name", book.name);
                        bundle.putString("author", book.author);
                        ((MainActivity) requireActivity()).startAddBookFragment(bundle);

                    } else if (optionsMenu[i].equals("Delete")) {
                        DataController.deleteBook(bookAdapter.getItemKey(index));
                        bookAdapter.deleteItem(index);
                    }
                }
        );
        builder.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("states", DataController.getBooksJSON(states).toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        DataController.saveBooks();
        super.onPause();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.pop_up_menu, menu);
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
                EntryController.logOut();
                return true;

            default:
                return false;

        }
    }
}