package com.example.projects.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.projects.utils.App;
import com.example.projects.utils.AppDatabase;
import com.example.projects.interfaces.CategoryClickListener;
import com.example.projects.R;
import com.example.projects.adapters.CategoryAdapter;
import com.example.projects.databinding.FragmentHomeBinding;
import com.example.projects.fragments.goals.GoalsFragment;
import com.example.projects.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeFragment extends Fragment {
    private AppDatabase appDatabase;
    FloatingActionButton addFab, addCategory, addGoal;
    TextView category_txt, goal_txt;
    boolean isAllFabsVisible;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = App.getInstance().getDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        //инициализация элементов UI
        addFab = rootView.findViewById(R.id.add_fab);
        addCategory = rootView.findViewById(R.id.add_category);
        addGoal = rootView.findViewById(R.id.add_goal);
        category_txt = rootView.findViewById(R.id.add_category_txt);
        goal_txt = rootView.findViewById(R.id.add_goal_text);
        addCategory.setVisibility(View.GONE);
        addGoal.setVisibility(View.GONE);
        category_txt.setVisibility(View.GONE);
        goal_txt.setVisibility(View.GONE);
        isAllFabsVisible = false;

        // Обработка нажатия на FAB
        addFab.setOnClickListener(v -> {
            if (!isAllFabsVisible) {
                addCategory.show();
                addGoal.show();
                category_txt.setVisibility(View.VISIBLE);
                goal_txt.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            } else {
                addCategory.hide();
                addGoal.hide();
                category_txt.setVisibility(View.GONE);
                goal_txt.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });
        // Нахождение RecyclerView в разметке
        recyclerView = rootView.findViewById(R.id.category_recyclerview);
        // Создание и установка адаптера для RecyclerView
        categoryAdapter = new CategoryAdapter();
        recyclerView.setAdapter(categoryAdapter);
        //загрузка категорий с базы
        loadCategoriesFromDatabase();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //действия при нажатии на кнопок FloatingActionButton
        addCategory.setOnClickListener(view1 ->
        {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_addCategoryFragment);
        });
        addGoal.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_addGoals);
        });
        //слушатели списка
        categoryAdapter.setCategoryClickListener(new CategoryClickListener() {
            //при нажатии на иконку категории осуществляется переход +передача categoryId
            public void onImageClick(int categoryId) {
                GoalsFragment goalsFragment = new GoalsFragment();
                Bundle args = new Bundle();
                args.putInt("categoryId", categoryId);
                goalsFragment.setArguments(args);
                //переход
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, goalsFragment)
                        .addToBackStack(null)
                        .commit();
            }
            //при нажатии на текст открытие окна длоя редактирования
            public void onTextClick(int categoryId, String categoryName) {
                showEditCategoryDialog(categoryId, categoryName);
            }
            //удаление
            public void onIconClick(int categoryId){
                showDeleteCategoryDialog(categoryId);
        }
    });
    }

    //загрузка данных с базы в фоновом потоке
    private void loadCategoriesFromDatabase() {
        AsyncTask.execute(() -> {
            List<Category> categories = appDatabase.categoryDao().getAllCategory();
            getActivity().runOnUiThread(() -> {
                categoryAdapter.setData(categories);
                categoryAdapter.notifyDataSetChanged();
            });
            Log.d("aaaa",categories.toString());
        });

    }
    //показ диалогового окна редактирования
    private void showEditCategoryDialog(int categoryId, String currentName) {
       //объявление AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Создаем View для диалогового окна, используя разметку edit_category_dialog.xml
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_category_dialog, null);
        EditText editCategoryName = dialogView.findViewById(R.id.edit_category_name);
        //отображение исходных данных
        editCategoryName.setText(currentName);
        builder.setView(dialogView);
        builder.setTitle("Редактировать");
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editCategoryName.getText().toString();
                updateCategoryInDatabase(categoryId, newName);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
    //сохранение изменений в базу
    private void updateCategoryInDatabase(int categoryId, String newName) {
        AsyncTask.execute(() -> {
            // Получаем объект категории из базы данных по его идентификатору categoryId
            Category category = appDatabase.categoryDao().getCategoryById(categoryId);
            if (category != null) {
                category.setName(newName);
                // Обновляем данные категории в базе данных с использованием метода updateCategory
                appDatabase.categoryDao().updateCategory(category);
                loadCategoriesFromDatabase();
            }
        });
    }
    //вызов диалогового окно при удалении
    private void showDeleteCategoryDialog(int categoryId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удалить");
        builder.setMessage("Вы уверены, что хотите удалить?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategoryFromDatabase(categoryId);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
//метод для удаления записи по id
    private void deleteCategoryFromDatabase(int categoryId) {
        AsyncTask.execute(() -> {
            Category category = appDatabase.categoryDao().getCategoryById(categoryId);
            if (category != null) {
                appDatabase.categoryDao().deleteCategory(category);
                loadCategoriesFromDatabase();
            }
        });
    }
}
