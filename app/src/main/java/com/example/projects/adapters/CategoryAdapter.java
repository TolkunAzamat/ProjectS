package com.example.projects.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projects.interfaces.CategoryClickListener;
import com.example.projects.R;
import com.example.projects.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
{
    private List<Category> categories;
    private CategoryClickListener categoryClickListener;
    public void setCategoryClickListener(CategoryClickListener listener)  // Установка слушателя, который будет реагировать на события нажатия
    {
        this.categoryClickListener = listener;
    }
    public void setData(List<Category> categories) // Данные категорий, которые будут отображаться в адаптере
    {
        this.categories = categories;
    }
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position)
    {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.image.setImageResource(category.getImage());
        //слушатель для иконки удаления
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
        if (categoryClickListener != null)
        {
        categoryClickListener.onIconClick(category.getId());
        }
            }
        });
       // слушатель для изображения категории для перехода
        holder.image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (categoryClickListener != null)
                {
                    categoryClickListener.onImageClick(category.getId());
                }
            }
        });
        // слушатель для текста категории, для изменения
        holder.categoryName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (categoryClickListener != null) {
                    categoryClickListener.onTextClick(category.getId(),category.getName());
                }
            }
        });

    }

@Override
    public int getItemCount() {return categories != null ? categories.size() : 0;}

    public class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        private TextView categoryName;
        private ImageView image;
        public ImageView delete;

        public CategoryViewHolder(View itemView)
        {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txtCategory);
            image = itemView.findViewById(R.id.category_image);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}