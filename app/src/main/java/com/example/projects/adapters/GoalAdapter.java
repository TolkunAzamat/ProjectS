package com.example.projects.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.interfaces.GoalClickListener;
import com.example.projects.R;
import com.example.projects.models.Goal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;
    private GoalClickListener goalClickListener;
    public   void setGoalClickListener(GoalClickListener listener) {
        this.goalClickListener = listener;
    }
    public void setData(List<Goal> goals) {this.goals = goals;}

    @Override
    public  GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalAdapter.GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        // Рассчет процента и установка его в TextView
        double percentage = (goal.getCurrentAmount() / (double) goal.getTargetAmount()) * 100;
        Date date = goal.getDate();
        // SimpleDateFormat с нужным форматом для отображения даты
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);
        holder.goal.setText(goal.getName());
        holder.currentAmount.setText(String.valueOf(goal.getCurrentAmount()));
        holder.targetAmount.setText(String.valueOf(goal.getTargetAmount()));
        holder.date.setText(dateString);
        holder.percent.setText(String.format("%.2f%%", percentage));
        // Изменение цвет фона в зависимости от процента
        if (percentage >= 100) {
            holder.itemView.setBackgroundColor(Color.parseColor("#66ff4d"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FBF8FF"));
        }
// Слушатели кликов для кнопок редактирования, удаления и добавления

        holder.image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (goalClickListener != null)
                {
                    // Вызов действие редактирования с соответствующими данными
                    goalClickListener.editClick(goal.getIdCategory(),goal.getId(), goal.getName(), goal.getTargetAmount(), goal.getDate());
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goalClickListener != null){
                    // Вызвать действие удаления с ID цели
                    goalClickListener.deleteClick(goal.getId());
                }
                notifyDataSetChanged();
            }
        });
        holder.add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (goalClickListener != null){
                    goalClickListener.addClick(goal.getId());
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }
    public class GoalViewHolder extends RecyclerView.ViewHolder {
        private TextView goal, targetAmount, currentAmount,percent,date;
        private ImageView add,delete,image;
        public GoalViewHolder(View itemView) {
            super(itemView);
            goal = itemView.findViewById(R.id.goal);
            targetAmount = itemView.findViewById(R.id.targetSum);
            currentAmount = itemView.findViewById(R.id.currentSum);
            percent = itemView.findViewById(R.id.percent);
            add = itemView.findViewById(R.id.addMoney);
            delete = itemView.findViewById(R.id.deleteGoal);
            image = itemView.findViewById(R.id.goal_image);
            date = itemView.findViewById(R.id.targetDate);
        }
    }
}
