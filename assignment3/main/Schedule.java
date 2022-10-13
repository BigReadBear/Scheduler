/*
 * Name: Schedule
 * Purpose: store values pertaining to schedule and format for reporting including adding strings to display sections
 * */

package main;

public class Schedule {
    private String courseID;
    private int section;
    private String classRoom;
    private String proffesorID;
    private String days;
    private String startTime;
    private String endTime;

    public Schedule(String courseID, String proffesorID, String days,
                    String startTime, String endTime) {
        this.courseID = courseID;
        this.proffesorID = proffesorID;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // copy constructor
    public Schedule(Schedule s) {
        this.courseID = s.getCourseID();
        this.section = s.getSection();
        this.classRoom = s.getClassRoom();
        this.proffesorID = s.getProffesorID();
        this.days = s.getDays();
        this.startTime = s.getStartTime();
        this.endTime = s.getEndTime();
    }

    public Schedule() {

    }

    public String getCourseID() {
        return courseID;
    }

    public String getProffesorID() {
        return proffesorID;
    }

    public String getDays() {
        return days;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setProffesorID(String proffesorID) {
        this.proffesorID = proffesorID;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    @Override
    public String toString() {
        StringBuilder courseName = new StringBuilder(getCourseID());
        String result;
        if (getSection() < 10) {
            courseName.append("-0");
            courseName.append(getSection());
        } else {
            courseName.append("-");
            courseName.append(getSection());
        }
        if (getClassRoom() == null) {
            result = String.format("%10s | %10s | %3s  | %6s | %6s",
                    courseName, getProffesorID(),
                    getDays(), getStartTime(), getEndTime());
        } else {
            result = String.format("%10s | %10s | %13s | %3s  | %6s | %6s",
                courseName, getProffesorID(), getClassRoom(),
                getDays(), getStartTime(), getEndTime());
        }

        return result;
    }
}
