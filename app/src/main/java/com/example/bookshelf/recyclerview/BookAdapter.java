package com.example.bookshelf.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookshelf.Book;
import com.example.bookshelf.R;

import java.util.ArrayList;

public class BookAdapter extends
        RecyclerView.Adapter<BookAdapter.CustomViewHolder> {
    private final ArrayList<Book> books;
    private final RecyclerListener recyclerListener;

    public BookAdapter(RecyclerListener recyclerListener, ArrayList<Book> colors) {
        this.recyclerListener = recyclerListener;
        this.books = colors;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.setContent(currentBook.name, currentBook.author);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layout;
        private final TextView name;
        private final TextView author;

        public CustomViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.bookLayout);
            name = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.bookAuthor);
        }

        public void setContent(String name, String author)
        {
            this.name.setText(name);
            this.author.setText(author);
        }
    }
}