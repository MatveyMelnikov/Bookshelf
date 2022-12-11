package com.example.bookshelf;

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
    ArrayList<Book> states = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            DataController.Init(requireContext());
            setInitialData();
        } else {
            states = DataController.getBooksArrayFromJSON(
                    savedInstanceState.getString("states")
            );
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
        RecyclerView recyclerView = view.findViewById(R.id.bookList);
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
        states.addAll(DataController.books);
    }

    @Override
    public void onElementClick(String path) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("states", DataController.getBooksJSON(states).toString());
        super.onSaveInstanceState(savedInstanceState);
    }
}