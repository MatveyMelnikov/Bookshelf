package com.example.bookshelf.view.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookshelf.R;
import com.example.bookshelf.repository.objects.Book;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.CustomViewHolder> {
    private final ArrayList<Book> books;
    private final RecyclerListener recyclerListener;
    private final WeakReference<Context> context;

    public BookAdapter(
            WeakReference<Context> context, 
            RecyclerListener recyclerListener, 
            ArrayList<Book> books
    ) {
        this.context = context;
        this.recyclerListener = recyclerListener;
        this.books = books;
    }

    public void deleteItem(int index) {
        books.remove(index);
        notifyItemRemoved(index);
    }

    public Book getItem(int index) {
        return books.get(index);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addBookToBeginning(int index) {
        Book book = books.get(index);
        books.remove(index);
        books.add(0, book);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Book book = books.get(position);
        holder.setContent(
                context.get(),
                book.getName(),
                book.getAuthor(),
                book.getCover()
        );

        holder.layout.setOnClickListener(view ->
                recyclerListener.onElementClick(holder.getAdapterPosition())
        );

        holder.layout.setOnLongClickListener(view -> {
                recyclerListener.onLongElementClick(holder.getAdapterPosition());
                return false;
            }
        );
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout layout;
        private final TextView name;
        private final TextView author;

        public CustomViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.bookLayout);
            name = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.bookAuthor);
        }

        public void setContent(Context context, String name, String author, Bitmap bitmap) {
            this.name.setText(name);
            this.author.setText(author);
            if (bitmap != null)
                layout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        }
    }
}