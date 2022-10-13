/*
 * Name: SchedulerDOA
 * Purpose: Interface with SchedulerDAOImplSQLite (scheduleTable),
 * these 2 are separated in case you want to use a different DBMS
 * */
package DAO;

import main.Schedule;

import java.io.File;
import java.util.List;

public interface SchedulerDAO {
    boolean exists(); //hold a value that indicate whether a schedule already exists or not
    void createNewScheduler();
    void readNewScheduleData(File file);
    void generateReportByDayTime();
    void generateReportByFacultyMember();
    void generateReportByClass();
    void drop(); //delete entire schedule table

    List<Schedule> getAll();
    int insert(Schedule s); //insert course in schedule


}
