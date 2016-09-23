package com.example.truman_cranor.doit;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name= "Tasks")
public class Task extends Model {

    @Column(name = "text")
    public String text;


    public Task() {
        super();
    }

    public Task(String text) {
        this.text = text;
    }
}
