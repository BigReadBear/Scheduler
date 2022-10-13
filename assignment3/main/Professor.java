/*
 * Name: Professor
 * Purpose: store values pertaining to professors
 * */
package main;

public class Professor {
    private String name;
    private int tuid; // to be filled by database

    public Professor(String name) {
        this.name = name;
    }

    public Professor() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTuid() {
        return tuid;
    }

    public void setTuid(int tuid) {
        this.tuid = tuid;
    }
}
