/*
 * Name: ProfessorDOAImplSQLite
 * Purpose: perform sql queries on database pertaining to the professor table
 * */
package SQLite;

//import dependencies
import DAO.ClassroomDAO;
import DAO.CourseDAO;
import DAO.ProfessorDAO;
import main.Classroom;
import main.Course;
import main.Professor;
import main.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAOImplSQLite implements ProfessorDAO {

    //override dao getter
    //perform sql query to find professor
    @Override
    public Professor get(String name) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Professors_Table WHERE Professor_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL); //use prepared sql statements to prevent SQLi's
            pstmt.setString(1, name);
            rs = pstmt.executeQuery(); //set resultset to result from query
            Professor p = new Professor("");
            // iterate over the records found
            while (rs.next()) {
                //set values of resultset
                p.setName(rs.getString("Professor_Name"));
                p.setTuid((rs.getInt("TUID")));
            }
            return p; //return professor
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;

    }

    //same thing as above just with TUID now instead of name
    @Override
    public Professor get(int tuid) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Professors_Table WHERE TUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            pstmt.setInt(1, tuid);
            rs = pstmt.executeQuery();
            Professor p = new Professor("");
            while (rs.next()) {
                p.setName(rs.getString("Professor_Name"));
                p.setTuid((rs.getInt("TUID")));
            }
            return p;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    //override dao getter
    //get all profs from table and return as a list
    @Override
    public List<Professor> getAll() {
        List<Professor> professors = new ArrayList<>();
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Professors_Table";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            rs = pstmt.executeQuery();
            // iterate over the records found
            while (rs.next()) {
                Professor p = new Professor();
                p.setTuid(rs.getInt("TUID"));
                p.setName(rs.getString("Professor_Name"));
                professors.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return professors;
    }

    //override dao insert
    //insert profs into profs table
    @Override
    public int insert(Professor prof) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String INSERT_SQL = "INSERT INTO Professors_Table (Professor_Name) " +
                    "VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setString(1, prof.getName());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

    //override dao delete
    //delete profs from professorsTable
    @Override
    public int delete(Professor prof) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String DELETE_SQL = "DELETE FROM Professors_Table WHERE Professor_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setString(1, prof.getName());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

}
