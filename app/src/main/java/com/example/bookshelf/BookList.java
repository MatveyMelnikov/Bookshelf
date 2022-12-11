package com.example.bookshelf;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.recyclerview.BookAdapter;
import com.example.bookshelf.recyclerview.RecyclerListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BookList extends Fragment implements RecyclerListener {
    private RecyclerView recyclerView;
    private int previousStatesSize = 0;
    private ArrayList<Book> states = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            DataController.Init(requireContext());
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
        View view = inflater.inflate(R.layout.activity_book_list, container, false);

        //setInitialData();
        recyclerView = view.findViewById(R.id.bookList);
        BookAdapter adapter = new BookAdapter(
                new WeakReference<>(getContext()), this, states
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        view.findViewById(R.id.floatingActionButton).setOnClickListener(view1 ->
                ((MainActivity) requireActivity()).startAddBookFragment()
        );

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
        BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
        bookAdapter.swapItem(index, 0);
        DataController.swapBooks(index, 0);
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
                    if (optionsMenu[i].equals("Edit")) {
                        int a = 3; a += 1;
                    } else if (optionsMenu[i].equals("Delete")) {
                        BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
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
}