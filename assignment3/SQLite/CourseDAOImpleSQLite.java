/*
 * Name: CourseDOAImplSQLite
 * Purpose: perform sql queries on database pertaining to the course table
 * */
package SQLite;

import DAO.CourseDAO;
import DAO.ProfessorDAO;
import main.Course;
import main.Professor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpleSQLite implements CourseDAO {

    //override dao getter
    //perform sql query to find classroom
    @Override
    public Course get(String ID) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Courses_Table WHERE Course_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL); //use prepared sql statements to prevent SQLi's
            pstmt.setString(1, ID);
            rs = pstmt.executeQuery(); //set resultset to result from query
            Course c = new Course("");
            // iterate over the records found
            while (rs.next()){
                //set values of resultset
                c.setCourseID(rs.getString("Course_ID"));
                c.setCourseTitle(rs.getString("Course_Title"));
                c.setCreditHours(rs.getInt("Credit_Hours"));
                c.setTuid(rs.getInt("TUID"));
            }
            return c; //return course
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //same thing as above just with TUID now instead of name
    @Override
    public Course get(int tuid) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Courses_Table WHERE TUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            pstmt.setInt(1, tuid);
            rs = pstmt.executeQuery();
            Course c = new Course("");
            // iterate over the records found
            while (rs.next()){
                c.setCourseID(rs.getString("Course_ID"));
                c.setCourseTitle(rs.getString("Course_Title"));
                c.setCreditHours(rs.getInt("Credit_Hours"));
                c.setTuid(rs.getInt("TUID"));
            }
            return c;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //override dao getter
    //get all courses from table and return as a list
    @Override
    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Courses_Table";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            rs = pstmt.executeQuery();
            // iterate over the records found
            while (rs.next()) {
                Course c = new Course();
                c.setTuid(rs.getInt("TUID"));
                c.setCourseID(rs.getString("Course_ID"));
                c.setCourseTitle(rs.getString("Course_Title"));
                c.setCreditHours(rs.getInt("Credit_Hours"));
                courses.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return courses;
    }

    //override dao insert
    //insert courses into courses table
    @Override
    public int insert(Course c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String INSERT_SQL = "INSERT INTO Courses_Table (Course_ID, Course_Title, Credit_Hours) " +
                    "VALUES (?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setString(1, c.getCourseID());
            pstmt.setString(2, c.getCourseTitle());
            pstmt.setInt(3, c.getCreditHours());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

    //override dao update
    // update course information
    @Override
    public int update(Course c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String UPDATE_SQL = "UPDATE Courses_Table SET Course_Title = ?," +
                    " Credit_Hours = ? WHERE Course_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL);
            pstmt.setString(1, c.getCourseTitle());
            pstmt.setInt(2, c.getCreditHours());
            pstmt.setString(3, c.getCourseID());
            nrOfRows = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

    //override dao delete
    //delete courses from coursesTable
    @Override
    public int delete(Course c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String DELETE_SQL = "DELETE FROM Courses_Table WHERE Course_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setString(1, c.getCourseID());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }
}
