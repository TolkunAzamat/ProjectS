package com.example.projects.fragments.category;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projects.utils.App;
import com.example.projects.utils.AppDatabase;
import com.example.projects.utils.Constants;
import com.example.projects.R;
import com.example.projects.adapters.ChooseIconAdapter;
import com.example.projects.databinding.FragmentAddCategoryBinding;
import com.example.projects.models.Category;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AddCategoryFragment extends Fragment {
    FragmentAddCategoryBinding binding;
    private Integer imageUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        //объявление navController для перехода по навигации
        NavController navController = Navigation.findNavController(view);
        //Переход на предыдущий фрагмент
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
    }

    private void setupView() {
        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.addNameCategory.getText().toString().isEmpty() || imageUrl == null) {
                    Toast.makeText(requireContext(), "Заполните поля!", Toast.LENGTH_SHORT).show();
                } else {
                    addCategory(
                            binding.addNameCategory.getText().toString(),
                            imageUrl
                    );
                    binding.addNameCategory.setText("");
                    Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //метод для показа диалогово окна BottomSheetDialog
    public void showBottomSheetDialog() {
        //объявление BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        // Создание представления (view) для диалогового окна, используя макет fragment_choose_icon
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_choose_icon, null);
        // Находим кнопку "назад" в представлении диалогового окна
        ImageView backBtn = view.findViewById(R.id.back_btn);
        // Находим элемент RecyclerView, который будет использоваться для отображения списка иконок
        RecyclerView recyclerView = view.findViewById(R.id.choose_icon_recyclerview);
        // Создание адаптера (ChooseIconAdapter) и передача ему списка иконок из Constants.getIconList()
        ChooseIconAdapter adapter = new ChooseIconAdapter(Constants.getIconList());
        // Устанавливаем адаптер в RecyclerView для отображения списка иконок
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ChooseIconAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int imageResource) {
                binding.addImage.setImageResource(imageResource);
                imageUrl = imageResource;
                dialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    //метод для добавления новой категории в базу данных
    public void addCategory(String name, Integer image) {
        // Создаем объект категории с переданным именем и изображением
        Category category = new Category(name, image);
        // Создаем новый экземпляр DatabaseTask, который выполняет операцию вставки категории в базу данных
        new DatabaseTask(App.getInstance().getDatabase(), category).execute();
    }

    private static class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase appDatabase;  // Ссылка на экземпляр базы данных
        private Category category;         // Категория, которую необходимо вставить

        public DatabaseTask(AppDatabase database, Category category) {
            this.appDatabase = database;   // Инициализируем базу данных
            this.category = category;       // Инициализируем категорию
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // В фоновом потоке выполняется операция вставки категории в базу данных
            appDatabase.categoryDao().insertCategory(category);
            return null; // Результат в фоновом потоке (null, так как не возвращает результат)
        }
    }
}