package com.example.truman_cranor.doit;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name= "Tasks")
public class Task extends Model {

    @Column(name = "text")
    public String text;

    @Column(name = "completed")
    public boolean completed;


    public Task() {
        super();
    }

    public Task(String text) {
        this.text = text;
        this.completed = false;
    }


    public static List<Task> readItems() {
        return  new Select()
                .from(Task.class)
                .execute();
    }

    public static Task newTask(String text) {
        Task newTask = new Task(text);
        newTask.save();

        return newTask;
    }

    public static void deleteTask(Task toDelete) {
        toDelete.delete();
    }


    public void updateTask(String newText, boolean newCompleted) {
        this.text = newText;
        this.completed = newCompleted;

        this.save();
    }

}
