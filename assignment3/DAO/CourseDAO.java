/*
 * Name: CourseDOA
 * Purpose: Interface with CourseDAOImplSQLite (coursesTable),
 * these 2 are separated in case you want to use a different DBMS
 * */
package DAO;

import main.Course;

import java.util.List;

public interface CourseDAO {
    //get course based on name or TUID
    Course get(String name);
    Course get(int tuid);

    //get list of all courses
    List<Course> getAll();

    //crud operations
    int insert(Course c);
    int update(Course c);
    int delete(Course c);
}
