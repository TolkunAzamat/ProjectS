package com.example.projects.fragments.goals;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.projects.utils.App;
import com.example.projects.utils.AppDatabase;
import com.example.projects.databinding.FragmentAddGoalsBinding;
import com.example.projects.models.Goal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddGoalsFragment extends Fragment {
FragmentAddGoalsBinding binding;
    private AppDatabase appDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = App.getInstance().getDatabase();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddGoalsBinding.inflate(inflater, container, false);
        binding.titlePage.setText("Добавление целей");
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.date.setOnClickListener(view1 -> {
            showDatePickerDialog();
        });
        getCategories();
        NavController navController = Navigation.findNavController(view);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
        binding.addGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем, заполнены ли все необходимые поля
                if (binding.name.getText().toString().isEmpty() ||
                        binding.summa.getText().toString().isEmpty() ||
                        binding.date.getText().toString().isEmpty()) {
                    // Если какие-либо поля пусты, выводим сообщение об ошибке
                    Toast.makeText(requireContext(), "Заполните поля!", Toast.LENGTH_SHORT).show();
                } else if (binding.spinner.getSelectedItem() == null) {
                    // Если не выбрана категория, выводим сообщение об ошибке
                    Toast.makeText(requireContext(), "Создайте категорию!", Toast.LENGTH_SHORT).show();
                } else {
                    // Если все поля заполнены и категория выбрана, выполняем следующий код в фоновом потоке
                    AsyncTask.execute(() -> {
                        // Получаем выбранное имя категории из Spinner
                        String categoryName = (String) binding.spinner.getSelectedItem();
                        // Получаем идентификатор категории по её имени из базы данных
                        int categoryId = appDatabase.categoryDao().getCategoryIdByName(categoryName);
                        requireActivity().runOnUiThread(() -> {
                            // Получаем значения из полей ввода для имени, суммы, даты
                            String name = binding.name.getText().toString();
                            int targetAmount = Integer.parseInt(binding.summa.getText().toString());
                            int currentAmount = 0;
                            boolean isCompleted = false;
                            String dateText = binding.date.getText().toString();
                            Date date = null;
                            try {
                                // Преобразуем строку даты в объект Date
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                date = sdf.parse(dateText);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // Если идентификатор категории существует (-1 означает, что категория не найдена), добавляем новую цель
                            if (categoryId != -1) {
                                addGoal(categoryId, name, targetAmount, currentAmount, date, isCompleted);
                            }
                            // Очищаем поля ввода
                            binding.name.setText("");
                            binding.summa.setText("");
                            binding.date.setText("");
                            // Выводим уведомление о добавлении цели
                            Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            }
        });
    }
    private void showDatePickerDialog() {
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
            binding.date.setText(selectedDate);
        }, year, month, day);
        // Отображение диалогового окна выбора даты
        datePickerDialog.show();
    }

    private void getCategories() {
        // Выполняем операцию в фоновом потоке с использованием AsyncTask.execute()
        AsyncTask.execute(() -> {
            // Получаем список категорий из базы данных
            List<String> categories = appDatabase.categoryDao().getCategories();
            // Создаем адаптер для отображения категорий в Spinner
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
            // Выполняем операцию на главном потоке, чтобы обновить интерфейс
            getActivity().runOnUiThread(() -> {
                // Устанавливаем вид элементов выпадающего списка (Spinner)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Устанавливаем адаптер в Spinner, чтобы отобразить список категорий
                binding.spinner.setAdapter(categoryAdapter);
            });
        });
    }


    public void addGoal(int idCategory, String name, int targetAmount, int currentAmount, Date date, boolean isCompleted) {
        // Создаем объект Goal (цель) с переданными данными
        Goal goal = new Goal(name, targetAmount, currentAmount, date, idCategory, isCompleted);
        // Создаем новый экземпляр DatabaseTask, который выполняет операцию вставки цели в базу данных
        new DatabaseTask(appDatabase, goal).execute();
    }



    private static class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase appDatabase; // Ссылка на экземпляр базы данных
        private Goal goal;// Категория, которую необходимо вставить

        public DatabaseTask(AppDatabase database, Goal goal) {
            this.appDatabase = database;  // Инициализируем базу данных
            this.goal = goal;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // В фоновом потоке выполняется операция вставки цели в базу данных

            appDatabase.goalDao().insertGoal(goal);
            return null;
        }
    }
}


