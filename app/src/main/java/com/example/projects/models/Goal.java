package com.example.projects.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity(tableName = "goal", foreignKeys = @ForeignKey(entity = Category.class,
        parentColumns = "id",
        childColumns = "idCategory",
        onDelete = CASCADE))
public class Goal {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private int idCategory;
        private String name;
        private int targetAmount;
        private int currentAmount;
        private Date date;
    private boolean isCompleted;

        public Goal(String name, int targetAmount, int currentAmount, Date date,int idCategory,boolean isCompleted) {
            this.name = name;
            this.targetAmount = targetAmount;
            this.currentAmount = currentAmount;
            this.date = date;
            this.idCategory = idCategory;
            this.isCompleted = isCompleted;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(int targetAmount) {
            this.targetAmount = targetAmount;
        }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    public boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

        @Override
        public String toString() {
            return "Goal{" +
                    "id=" + id +
                    "idCategory=" + idCategory +
                    ", name='" + name + '\'' +
                    ", targetAmount=" + targetAmount +
                    ", currentAmount=" + currentAmount +
                    "date=" + date +'}';
        }
    }
