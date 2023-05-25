package com.example.bookshelf.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookshelf.R;
import com.example.bookshelf.repository.objects.Quote;

import java.util.ArrayList;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.CustomViewHolder> {
    private final ArrayList<Quote> quotes;
    private final RecyclerListener recyclerListener;

    public QuoteAdapter(
            RecyclerListener recyclerListener,
            ArrayList<Quote> quotes
    ) {
        this.recyclerListener = recyclerListener;
        this.quotes = quotes;
    }

    public void deleteItem(int index) {
        quotes.remove(index);
        notifyItemRemoved(index);
    }

    public Quote getItem(int index) {
        return quotes.get(index);
    }

    @NonNull
    @Override
    public QuoteAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuoteAdapter.CustomViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_quote_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteAdapter.CustomViewHolder holder, int position) {
        holder.setContent(quotes.get(position).getContent());

        holder.layout.setOnClickListener(view -> {
            recyclerListener.onElementClick(holder.getAdapterPosition());
        });

        holder.layout.setOnLongClickListener(view -> {
            recyclerListener.onLongElementClick(holder.getAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView content;

        public CustomViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.quoteLayout);
            content = itemView.findViewById(R.id.name);
        }

        public void setContent(String content) {
            this.content.setText(content);
        }
    }
}
