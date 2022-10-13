/*
 * Name: Classroom
 * Purpose: store values pertaining to classroom
 * */
package main;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private String name;
    private int capacity;

    private int tuid;

    public Classroom(String name) {
        this.name = name;
        this.capacity = 20;
    }

    public Classroom() {

    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getTuid() {
        return tuid;
    }

    public void setTuid(int tuid) {
        this.tuid = tuid;
    }
}
