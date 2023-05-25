package com.example.bookshelf.view.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.repository.objects.Family;
import com.example.bookshelf.repository.objects.User;

import java.util.ArrayList;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.CustomViewHolder> {
    private final ArrayList<User> users;
    private final RecyclerListener recyclerListener;
    private final Family family;

    public FamilyAdapter(
            RecyclerListener recyclerListener,
            ArrayList<User> users,
            Family family
    ) {
        this.recyclerListener = recyclerListener;
        this.users = users;
        this.family = family;
    }

    public void deleteItem(int index) {
        users.remove(index);
        notifyItemRemoved(index);
    }

    public User getItem(int index) {
        return users.get(index);
    }

    @NonNull
    @Override
    public FamilyAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FamilyAdapter.CustomViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_family_member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyAdapter.CustomViewHolder holder, int position) {
        String status = users.get(position).isChild() ? "Child" : "Adult";
        if (family.getCreatorId() == EntryController.getLoggedUser().getId())
            status += ", creator";

        holder.setStrings(users.get(position).getName(), status);

        holder.layout.setOnClickListener(view ->
                recyclerListener.onElementClick(holder.getAdapterPosition())
        );

        holder.layout.setOnLongClickListener(view -> {
            recyclerListener.onLongElementClick(holder.getAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView name;
        private final TextView status;

        public CustomViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.familyLayout);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
        }

        public void setStrings(String name, String status) {
            this.name.setText(name);
            this.status.setText(status);
        }
    }
}
