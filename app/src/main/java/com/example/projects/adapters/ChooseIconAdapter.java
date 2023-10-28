package com.example.projects.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.databinding.ItemChooseIconBinding;
import com.example.projects.models.Icons;

import java.util.List;

public class ChooseIconAdapter extends RecyclerView.Adapter<ChooseIconAdapter.ViewHolder> {

    private List<Icons> array;
    private OnItemClickListener onItemClickListener;

    // Интерфейс для обработки событий нажатия
    public interface OnItemClickListener {
        void onItemClick(int imageResourceId);
    }
//Слушатель, который будет реагировать на события нажатия

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    // Конструктор адаптера, принимающий список иконок
    public ChooseIconAdapter(List<Icons> array) {
        this.array = array;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemChooseIconBinding binding = ItemChooseIconBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Icons item = array.get(position);
        holder.binding.image.setImageResource(item.getImage());
        holder.binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item.getImage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemChooseIconBinding binding;

        public ViewHolder(ItemChooseIconBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}