package com.example.projects.fragments.goals;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projects.utils.App;
import com.example.projects.utils.AppDatabase;
import com.example.projects.interfaces.GoalClickListener;
import com.example.projects.R;
import com.example.projects.adapters.GoalAdapter;
import com.example.projects.models.Goal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsFragment extends Fragment {
    private RecyclerView recyclerView;
    private GoalAdapter goalAdapter;
    private AppDatabase appDatabase;
    ImageView backBtn;
    TextView title;
    private boolean isUpdating = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = App.getInstance().getDatabase();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Создание и "рисование" макета фрагмента
        View rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        // Нахождение RecyclerView в разметке
        recyclerView = rootView.findViewById(R.id.goal_recyclerview);
        // Создание и установка адаптера для RecyclerView
        goalAdapter = new GoalAdapter();
        recyclerView.setAdapter(goalAdapter);
        // Обновление адаптера
        goalAdapter.notifyDataSetChanged();
        // Получение аргументов, переданных во фрагмент
        Bundle args = getArguments();
        if (args != null) {
            int categoryId = args.getInt("categoryId", -1);
            // Если был передан идентификатор категории, загрузить цели для этой категории
            if (categoryId != -1) {
                loadGoalsFromCategory(categoryId);
            }
        }
        // Нахождение и установка текста в элементе TextView с идентификатором "title"
        title = rootView.findViewById(R.id.title);
        title.setText("Цели");
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goalAdapter.setGoalClickListener(new GoalClickListener() {
            @Override
            public void editClick(int categoryId, int goalId, String name, int targetSum, Date date) {
                // Вызывается при нажатии на кнопку редактирования цели.
                // Показывает диалоговое окно редактирования цели с переданными данными.
                showEditGoalDialog(categoryId, goalId, name, targetSum, date);
            }

            @Override
            public void deleteClick(int goalId) {
                // Вызывается при нажатии на кнопку удаления цели.
                // Показывает диалоговое окно подтверждения удаления цели.
                showDeleteGoalDialog(goalId);
            }

            @Override
            public void addClick(int goalId) {
                // Вызывается при нажатии на кнопку добавления суммы к цели.
                // Показывает диалоговое окно для ввода суммы, которая будет добавлена к цели.
                showAddAmountDialog(goalId);
            }
        });

    }

    private void loadGoalsFromCategory(int categoryId) {
        AsyncTask.execute(() -> {
            try {
                // Получаем список целей из базы данных, связанных с заданной категорией (categoryId)
                List<Goal> goals = appDatabase.goalDao().getGoalsByCategory(categoryId);
                // Проходим по каждой цели и вычисляем процент выполнения
                for (Goal goal : goals) {
                    double percentage = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
                    goal.setCompleted(percentage >= 100);
                }
                // Обновляем интерфейс на главном потоке, устанавливая новый список целей в адаптер
                getActivity().runOnUiThread(() -> {
                    goalAdapter.setData(goals);
                    goalAdapter.notifyDataSetChanged();
                });
                // Сортируем цели, перемещая выполненные цели в конец списка
                Collections.sort(goals, new Comparator<Goal>() {
                    @Override
                    public int compare(Goal goal1, Goal goal2) {
                        if (goal1.getCompleted() && !goal2.getCompleted()) {
                            return 1;
                        } else if (!goal1.getCompleted() && goal2.getCompleted()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                // Обновляем интерфейс с отсортированным списком целей
                getActivity().runOnUiThread(() -> {
                    goalAdapter.setData(goals);
                    goalAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
//метод для отображения окна внесения изменений
    private void showEditGoalDialog(int categoryId,int goalId, String currentName, int currentTargetSum, Date date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_goal, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editTargetSum = dialogView.findViewById(R.id.editSumma);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        editName.setText(currentName);
        editTargetSum.setText(String.valueOf(currentTargetSum));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);
        editDate.setText(dateStr);
        editDate.setOnClickListener(view -> showDatePickerDialog(editDate));
        builder.setView(dialogView);
        builder.setTitle("Редактировать");
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editName.getText().toString();
                String newTargetSum = editTargetSum.getText().toString();
                String newDateStr = editDate.getText().toString();
                try {
                    Date newDate = sdf.parse(newDateStr);
                    updateGoalInDatabase(categoryId,goalId, newName, Integer.parseInt(newTargetSum), newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
//метод обновления данных
    private void updateGoalInDatabase(int categoryId,int goalId, String newName, int newTargetSum, Date newDate) {
        AsyncTask.execute(() -> {
            Goal goal = appDatabase.goalDao().getGoalById(goalId);
            if (goal != null) {
                //установка новых значений
                goal.setName(newName);
                goal.setTargetAmount(newTargetSum);
                goal.setDate(newDate);
                //обновление в базе
                appDatabase.goalDao().updateGoal(goal);
                loadGoalsFromCategory(categoryId);
            }
        });
    }
//метод показывающий календарт
    private void showDatePickerDialog(EditText editText) {
        // Создание экземпляра Calendar для получения текущей даты и времени
        Calendar calendar = Calendar.getInstance();
        // Извлечение текущего года, месяца и дня из Calendar
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Создание экземпляра DatePickerDialog с текущей датой и временем
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // Когда пользователь выбирает дату в диалоговом окне, этот код выполняется
            // Форматирование выбранной даты в строку в формате "yyyy-MM-dd"
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            // Установка выбранной даты в поле ввода даты (binding.date)
            editText.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    //метод отображает диалоговое окно при удалении цели
    private void showDeleteGoalDialog(int goalId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удалить");
        builder.setMessage("Вы уверены, что хотите удалить?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGoalFromDatabase(goalId);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
//метод для удаления целей
    private void deleteGoalFromDatabase(int goalId) {
        AsyncTask.execute(() -> {
            //удаление с базы цель с указанной id
            Goal goal = appDatabase.goalDao().getGoalById(goalId);
            if (goal != null) {
                appDatabase.goalDao().deleteGoal(goal);
                loadGoalsFromCategory(goal.getIdCategory());
            }
        });
    }
//метод отображает диалоговое окно для добавления суммы к цели.
    private void showAddAmountDialog(int goalId) {
        if (isUpdating) {
            return; // Если обновление уже выполняется, игнорируем запрос
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_sum, null);
        EditText editAmountToAdd = dialogView.findViewById(R.id.addAmountToAdd);
        builder.setView(dialogView);
        builder.setTitle("Добавить накопление");
        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isUpdating = true; // Устанавливаем флаг перед выполнением операции
                String amountStr = editAmountToAdd.getText().toString();
                if (!amountStr.isEmpty()) {
                    int amountToAdd = Integer.parseInt(amountStr);
                    addAmountToGoal(goalId, amountToAdd);
                }
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
//метод добавляет указанную сумму к текущей сумме цели и
// обновляет данные в базе данных.
    private void addAmountToGoal(int goalId, int amountToAdd) {
        AsyncTask.execute(() -> {
            Goal goal = appDatabase.goalDao().getGoalById(goalId);
            if (goal != null) {
                int currentSum = goal.getCurrentAmount();
                int newSum = currentSum + amountToAdd;
                goal.setCurrentAmount(newSum);
                appDatabase.goalDao().updateGoal(goal);

                getActivity().runOnUiThread(() -> {
                    isUpdating = false; // Сбрасываем флаг после обновления
                    loadGoalsFromCategory(goal.getIdCategory());
                });
            }
        });
    }
}



