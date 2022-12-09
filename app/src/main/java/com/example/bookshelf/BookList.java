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

import java.util.ArrayList;

public class BookList extends Fragment implements RecyclerListener {
    ArrayList<Book> states = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.activity_book_list, container, false);

        setInitialData();
        RecyclerView recyclerView = view.findViewById(R.id.bookList);
        BookAdapter adapter = new BookAdapter(this, states);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return view;
    }

    private void setInitialData() {
        for (int i = 0; i < 10; i++) {
            states.add(
                    new Book("Name" + i, "Author" + i, "path" + i)
            );
        }
    }

    @Override
    public void onElementClick(String path) {

    }
}