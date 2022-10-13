/*
 * Name: Course
 * Purpose: store values pertaining to course
 * */
package main;

public class Course {
    private String courseID;
    private String courseTitle;
    private int creditHours;

    private int tuid;

    private int section;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public Course(String courseID) {
        this.courseID = courseID;
        this.courseTitle = "";
        this.creditHours = 0;
    }

    public Course(){

    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public int getTuid() {
        return tuid;
    }

    public void setTuid(int tuid) {
        this.tuid = tuid;
    }
}
