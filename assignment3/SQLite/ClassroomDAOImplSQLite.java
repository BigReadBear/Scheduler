/*
 * Name: ClassroomDOAImplSQLite
 * Purpose: perform sql queries on database pertaining to the classroom table
 * */
package SQLite;

//import dependencies
import DAO.ClassroomDAO;
import main.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAOImplSQLite implements ClassroomDAO {

    //override dao getter
    //perform sql query t find classroom
    @Override
    public Classroom get(String name) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM ClassRoom_Table WHERE ClassRoom_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL); //use prepared sql statements to prevent SQLi's
            pstmt.setString(1, name);
            rs = pstmt.executeQuery(); //set resultset to result from query
            Classroom c = new Classroom("");
            // iterate over the records found
            while (rs.next()){
                //set values of resultset
                c.setName(rs.getString("ClassRoom_Name"));
                c.setCapacity(rs.getInt("Capacity"));
                c.setTuid(rs.getInt("TUID"));
            }
            return c; //return classroom
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new Classroom(""); //question
    }

    //same thing as above just with TUID now instead of name
    @Override
    public Classroom get(int tuid) {
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM ClassRoom_Table WHERE TUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            pstmt.setInt(1, tuid);
            rs = pstmt.executeQuery();
            Classroom c = new Classroom("");
            // iterate over the records found
            while (rs.next()){
                c.setName(rs.getString("ClassRoom_Name"));
                c.setCapacity(rs.getInt("Capacity"));
                c.setTuid(rs.getInt("TUID"));
            }
            return c;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return new Classroom("");
    }

    //override dao getter
    //get all classroom from table and return as a list
    @Override
    public List<Classroom> getAll() {
        List<Classroom> classes = new ArrayList<>();
        ResultSet rs;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM ClassRoom_Table";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            rs = pstmt.executeQuery();
            // iterate over the records found
            while (rs.next()) {
                Classroom c = new Classroom();
                c.setTuid(rs.getInt("TUID"));
                c.setName(rs.getString("ClassRoom_Name"));
                c.setCapacity(rs.getInt("Capacity"));
                classes.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return classes;
    }

    //override dao insert
    //insert classrooms into classroom table
    @Override
    public int insert(Classroom c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String INSERT_SQL = "INSERT INTO ClassRoom_Table (ClassRoom_Name, Capacity) " +
                    "VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            //execute queries w/ prepared statements
            pstmt.setString(1, c.getName()); //get values from instance of classroom class
            pstmt.setInt(2, c.getCapacity()); //get values from instance of classroom class
            nrOfRows = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

    //override dao update
    // update capacity of class with unique class name
    @Override
    public int update(Classroom c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String UPDATE_SQL = "UPDATE ClassRoom_Table SET Capacity = ? WHERE ClassRoom_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL);
            pstmt.setInt(1, c.getCapacity()); //get values from instance of classroom class
            pstmt.setString(2, c.getName()); //get values from instance of classroom class
            nrOfRows = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }

    //override dao delete
    //delete classrooms from classroomTable
    @Override
    public int delete(Classroom c) {
        int nrOfRows = 0;
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String DELETE_SQL = "DELETE FROM ClassRoom_Table WHERE ClassRoom_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setString(1, c.getName());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }
}
