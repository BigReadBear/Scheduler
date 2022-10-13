/*
 * Name: SchedulerDOAImplSQLite
 * Purpose: perform sql queries on database pertaining to the schedule table
 * */
package SQLite;

//import dependencies
import DAO.ClassroomDAO;
import DAO.CourseDAO;
import DAO.ProfessorDAO;
import DAO.SchedulerDAO;
import main.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SchedulerImplSQLite implements SchedulerDAO {

    //set db connection values
    static public String dbName = "SQLiteScheduler.db";
    static public String url = "jdbc:sqlite:" + dbName;

    //set sorted days and times as lists needed for the report
    List<String> sortedDays = new ArrayList<>(Arrays.asList("MW", "M", "TR", "T", "W", "R", "F"));
    List<String> sortedTimes = new ArrayList<>(Arrays.asList("8:30", "9:00", "9:30", "10:00",
            "10:30", "11:00", "11:30", "12:00",
            "12:30", "1:00", "1:30", "2:00",
            "2:30", "3:00", "3:30", "4:00", "4.30"));

    //override dao exists method
    //determine whether or not the DB already exists
    //returns T/F
    @Override
    public boolean exists() {
        File file = new File(dbName);
        return file.exists();
    }

    //override dao  method
    //create a new DB
    //drop existing one
    //create static tables
    @Override
    public void createNewScheduler() {
        createDatabase();
        dropDatabase();
        createTables();
        addClasses();
        addCourses();
        addProfessors();
    }

    //override dao  method
    //read in new schedule data from the file
    @Override
    public void readNewScheduleData(File file) {
        List<Schedule> schedules = new ArrayList<>();
        //read in file using bufferedreader
        try (BufferedReader br =
                     new BufferedReader(new FileReader(file))) {
            String[] line;
            String st;
            //while there is still a line to be read that's not empty
            while ((st = br.readLine()) != null) {
                //split line on whitespaces
                line = st.split("\\s+");
                //as long as there are 6 columns to read, the data input is valid (kinda, not flawless of course)
                if (line.length == 6) {
                    Schedule s = new Schedule(line[0] + " " + line[1], line[2], line[3], line[4], line[5]); //create new instance of schedule with inputted values
                    CourseDAO courseDAO = new CourseDAOImpleSQLite();
                    Course course = courseDAO.get(s.getCourseID()); //get course ID, needed for credit hours and section counting
                    //call schedule class function to determine if class cna be scheduled or not
                    Scheduler.ScheduleFound schedule = Scheduler.scheduleClass(course.getCreditHours(), s.getDays(), s.getStartTime(), s.getEndTime());
                    int section = getSection(s.getCourseID()) + 1; //increment section number for that course id
                    s.setSection(section); //update section
                    //if class can be scheduled, insert this course and info into scheduleTable
                    if (!(schedule == null)) {
                        s.setClassRoom(schedule.className);
                        s.setDays(schedule.days);
                        s.setStartTime(schedule.startTime);
                        s.setEndTime(schedule.endTime);
                        insert(s);
                    }
                    //else we still increment the section number, but print out an error message and add it to non-scheduled sections
                    else {
                        section = getSectionNotScheduled(schedules, s.getCourseID(), section);
                        s.setSection(section);
                        schedules.add(s);
                        System.out.println(s + "    COULD NOT BE SCHEDULED, TO BE ADDRESSED BY THE ADMINISTRATION");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println();

    }


    //override dao  method
    //generate report sorted by day and time
    @Override
    public void generateReportByDayTime() {
        List<Schedule> schedule = getAll(); //get entire schedule from getAll
        Collections.sort(schedule, sortByDayTime); //sort with help of helper function sortByDayTIme
        System.out.println();
        System.out.printf("%10s | %10s | %13s | %3s | %6s | %6s%n", //formatting
                "Course", "Professor", "Classroom", "Days", "Start", "End"); //formatting
        System.out.printf("%10s---%10s---%13s---%3s---%6s---%6s%n",
                "----------", "----------", "-------------", "---", "------", "-------");
        //for each line in schedule
        for(Schedule s : schedule){
            System.out.println(s);
        }
        System.out.println();
    }

    //override dao  method
    //generate report for credits per faculty member
    @Override
    public void generateReportByFacultyMember() {
        List<Schedule> schedule = getAll(); //get entire schedule from getAll
        ProfessorDAO professorDAO = new ProfessorDAOImplSQLite(); //create new instance of professorDAO
        List<Professor> professors = professorDAO.getAll(); //get all professors
        CourseDAO courseDAO = new CourseDAOImpleSQLite(); //create new instance of coursesDAO
        List<Course> courses = courseDAO.getAll(); //get all courses

        professors.sort(sortByName); //sort byname
        int credits = 0; //init credit counter
        //formatting
        System.out.printf("%10s | %6s%n", "Name", "Credits");
        System.out.printf("%10s---%6s%n", "----------", "-------");
        //for each professor
        for(Professor p : professors) {
            //for each line in schedule
            for(Schedule s : schedule) {
                //if name in schedule equals prof id
                if(p.getName().equals(s.getProffesorID())){
                    //for each course in courses
                    for (Course c : courses){
                        //if course name equals course id
                        if(c.getCourseID().equals(s.getCourseID())){
                            credits += c.getCreditHours(); //add credits to professors total credits
                        }
                    }
                }
            }
            System.out.printf("%10s | %6d%n", p.getName(), credits); //output per professor
            credits = 0; //reset counter
        }
        System.out.println();
    }

    //override dao  method
    //generate report per class
    //essentially the same as above reporting, just gives you capacity per class
    @Override
    public void generateReportByClass() {
        List<Schedule> schedule = getAll();
        ClassroomDAO classroomDAO = new ClassroomDAOImplSQLite();
        List<Classroom> classes = classroomDAO.getAll();
        schedule.sort(sortByCourse);
        classes.sort(sortClassByName);
        System.out.printf("%11s | %8s | %11s%n", "Classroom", "Capacity", "Course");
        System.out.printf("%11s---%8s---%11s%n", "-----------", "--------","-----------");
        for (Classroom c : classes){
            boolean writeClassName = true;
            for (Schedule s : schedule){
                if (s.getClassRoom().equals(c.getName())){
                    if (writeClassName) {
                        System.out.printf("%11s | %8d | %11s%n", c.getName(),
                                c.getCapacity(), s.getCourseID() + "-0" + s.getSection());
                        writeClassName = false;
                    }
                    else {
                        System.out.printf("%11s | %8s | %11s%n", "", "", s.getCourseID() + "-0" + s.getSection());
                    }
                }
            }
        }
        System.out.println();
    }

    //override dao  method
    //delete existing database and schedule
    @Override
    public void drop() {
        dropDatabase();
        File file = new File(dbName);
        if (file.delete())
            System.out.println(dbName + " deleted!");
    }

    //helper function to fetch entire schedule
    @Override
    public List<Schedule> getAll() {
        //init instances to store result in
        List<Schedule> schedules = new ArrayList<>();
        ResultSet rs;
        CourseDAO courseDAO = new CourseDAOImpleSQLite();
        ClassroomDAO classRoomDAO = new ClassroomDAOImplSQLite();
        ProfessorDAO professorDAO = new ProfessorDAOImplSQLite();
        //connect to DB and select everythign from schedule table
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String WHERE_SQL = "SELECT * FROM Schedule_Table";
            PreparedStatement pstmt = conn.prepareStatement(WHERE_SQL);
            rs = pstmt.executeQuery();
            // iterate over the records found
            while (rs.next()) {
                Schedule s = new Schedule();
                //get TUID's
                Course course = courseDAO.get(rs.getInt("Course_TUID"));
                Classroom classRoom = classRoomDAO.get(rs.getInt("ClassRoom_TUID"));
                Professor professor = professorDAO.get(rs.getInt("Professor_TUID"));
                if (course != null && classRoom != null && professor != null) {
                    //set values corresponding to values found
                    s.setCourseID(course.getCourseID());
                    s.setSection(rs.getInt("Section"));
                    s.setClassRoom(classRoom.getName());
                    s.setProffesorID(professor.getName());
                    s.setStartTime(rs.getString("Start_Time"));
                    s.setEndTime(rs.getString("End_Time"));
                    s.setDays(rs.getString("Days"));
                    schedules.add(s);
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return schedules; //return the schedule as an arraylist
    }

    //function to essentially schedule a class in the schedule table
    @Override
    public int insert(Schedule s) {
        // first entry
        int nrOfRows = 0;
        //set values to insert
        CourseDAO courseDAO = new CourseDAOImpleSQLite();
        Course course = courseDAO.get(s.getCourseID());
        ClassroomDAO classRoomDAO = new ClassroomDAOImplSQLite();
        Classroom classroom = classRoomDAO.get(s.getClassRoom());
        ProfessorDAO professorDAO = new ProfessorDAOImplSQLite();
        Professor professor = professorDAO.get(s.getProffesorID());
        //connect to db and insert using prepared stm
        try (Connection conn = DriverManager.getConnection(SchedulerImplSQLite.url)) {
            String INSERT_SQL = "INSERT INTO Schedule_Table (Course_TUID, Section, ClassRoom_TUID," +
                    "Professor_TUID, Start_Time, End_Time, Days) " +
                    "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, course.getTuid());
            pstmt.setInt(2, s.getSection());
            pstmt.setInt(3, classroom.getTuid());
            pstmt.setInt(4, professor.getTuid());
            pstmt.setString(5, s.getStartTime());
            pstmt.setString(6, s.getEndTime());
            pstmt.setString(7, s.getDays());
            nrOfRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nrOfRows;
    }


    //create new database and set connection to it
    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(url)) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //drop the database tables
    private void dropDatabase() {
        List<String> tables = new ArrayList<>();
        //select all tables
        try (Connection conn = DriverManager.getConnection(url)) {
            String DROP_SQL = "SELECT * FROM sqlite_master WHERE type= ? ";
            PreparedStatement pstmt = conn.prepareStatement(DROP_SQL);
            pstmt.setString(1, "table");
            ResultSet rs = pstmt.executeQuery();
            //add tables to list
            while (rs.next()) {
                tables.add(rs.getString("tbl_name"));
            }
            pstmt.close();
            //for each table, drop the table
            for (String s : tables) {
                // prevent deleting special tables
                if (!s.startsWith("sqlite_"))
                    conn.createStatement().execute("DROP TABLE " + s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //create tables, using agreed on schema
    private void createTables() {
        try (Connection conn = DriverManager.getConnection(url)) {
            String CREATE_CLASS_ROOM_SQL = "CREATE TABLE \"ClassRoom_Table\" (\n" +
                    "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"ClassRoom_Name\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"Capacity\"\tINTEGER,\n" +
                    "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                    ")";
            String CREATE_COURSES_TABLE_SQL = "CREATE TABLE \"Courses_Table\" (\n" +
                    "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"Course_ID\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"Course_Title\"\tTEXT,\n" +
                    "\t\"Credit_Hours\"\tINTEGER NOT NULL,\n" +
                    "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                    ")";

            String CREATE_PROFESSORS_TABLE_SQL = "CREATE TABLE \"Professors_Table\" (\n" +
                    "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"Professor_Name\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                    ")";
            String CREATE_SCHEDULE_TABLE_SQL = "CREATE TABLE \"Schedule_Table\" (\n" +
                    "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"Course_TUID\"\tINTEGER NOT NULL,\n" +
                    "\t\"Section\"\tINTEGER NOT NULL,\n" +
                    "\t\"ClassRoom_TUID\"\tINTEGER NOT NULL,\n" +
                    "\t\"Professor_TUID\"\tINTEGER NOT NULL,\n" +
                    "\t\"Start_Time\"\tTEXT NOT NULL,\n" +
                    "\t\"End_Time\"\tTEXT NOT NULL,\n" +
                    "\t\"Days\"\tTEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(\"ClassRoom_TUID\") REFERENCES \"ClassRoom_Table\"(\"TUID\"),\n" +
                    "\tFOREIGN KEY(\"Course_TUID\") REFERENCES \"Courses_Table\"(\"TUID\"),\n" +
                    "\tFOREIGN KEY(\"Professor_TUID\") REFERENCES \"Professors_Table\"(\"TUID\"),\n" +
                    "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                    ")";
            conn.createStatement().execute(CREATE_CLASS_ROOM_SQL);
            conn.createStatement().execute(CREATE_COURSES_TABLE_SQL);
            conn.createStatement().execute(CREATE_PROFESSORS_TABLE_SQL);
            conn.createStatement().execute(CREATE_SCHEDULE_TABLE_SQL);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // populate static course table
    private void addCourses() {
        List<String> courses = new ArrayList<>();
        courses.add("CSC 105, Computers and Programming, 4");
        courses.add("CSC 107, Introduction to Code Preparation, 1");
        courses.add("CSC 116, Programming I, 4");
        courses.add("CSC 216, Programming II, 4");
        courses.add("CSC 227, Commenting and Naming Conventions, 2");
        courses.add("CSC 316, Data Structures & Algorithms, 4");
        courses.add("CSC 416, Advanced Algorithm Analysis, 3");
        courses.add("CSC 211, Introductory .NET Development, 3");
        courses.add("CSC 311, Advanced .NET Development, 4");
        courses.add("CSC 313, Real World Application Development, 3");
        courses.add("CSC 411, Data Driven Systems, 3");
        courses.add("CSC 412, Sensor Systems, 3");
        courses.add("CSC 413, Artificial Intelligence Systems, 3");
        courses.add("CSC 496, Software Engineering I, 4");
        courses.add("CSC 497, Software Engineering II, 4");
        courses.add("CSC 498, Software Engineering III, 4");
        for (String s : courses) {
            String[] course = s.split(",");
            if (course.length == 3) {
                Course c = new Course(course[0].trim());
                c.setCourseTitle(course[1].trim());
                c.setCreditHours(Integer.parseInt(course[2].trim()));
                CourseDAO courseDAO = new CourseDAOImpleSQLite();
                if (courseDAO.get(c.getCourseID()) != null)
                    courseDAO.insert(c);
            }
        }
    }

    // populate static class table
    private void addClasses() {
        // there are three classrooms
        Classroom classroom = new Classroom("Classroom A");
        classroom.setCapacity(30);
        ClassroomDAO classRoomDAO = new ClassroomDAOImplSQLite();
        if (classRoomDAO.get(classroom.getName()) != null)
            classRoomDAO.insert(classroom);
        classroom.setName("Classroom B");
        classroom.setCapacity(25);
        if (classRoomDAO.get(classroom.getName()) != null)
            classRoomDAO.insert(classroom);
        classroom.setName("Classroom C");
        classroom.setCapacity(20);
        if (classRoomDAO.get(classroom.getName()) != null)
            classRoomDAO.insert(classroom);
    }

    // populate static professor table
    private void addProfessors() {
        List<Professor> professorList = new ArrayList<>();
        professorList.add(new Professor("James"));
        professorList.add(new Professor("Smith"));
        professorList.add(new Professor("Jones"));
        professorList.add(new Professor("Vasquez"));
        professorList.add(new Professor("Abdul"));
        professorList.add(new Professor("Thomas"));

        ProfessorDAO professorDAO = new ProfessorDAOImplSQLite();
        for (Professor p : professorList) {
            if (professorDAO.get(p.getName()) != null)
                professorDAO.insert(p);
        }
    }

    //helper fucntion to get section of a course(so we can do this plus 1)
    private int getSection(String courseName) {
        SchedulerDAO scheduler = new SchedulerImplSQLite();
        List<Schedule> schedules = scheduler.getAll();
        int count = 0;
        //count every course with the same name
        for (Schedule s : schedules) {
            if (s.getCourseID().equals(courseName)) {
                count++;
            }
        }
        return count;
    }

    //keep track off unscheduled courses, since we still need to update section numbers
    private int getSectionNotScheduled(List<Schedule> schedules, String courseName, int section) {
        int count = section;
        for (Schedule s : schedules) {
            if (s.getCourseID().equals(courseName)) {
                count++;
            }
        }
        return count;
    }


    //helper function to sort  by day and time
    private final Comparator<Schedule> sortByDayTime = new Comparator<Schedule>() {

        @Override
        public int compare(Schedule o1, Schedule o2) {
            //essentially we are comparing previously set sorted days and time with the schedule
            Integer i1 = sortedDays.indexOf(o1.getDays());
            Integer i2 = sortedDays.indexOf(o2.getDays());
            int dayCompare = i1.compareTo(i2);
            if (dayCompare != 0) {
                return dayCompare;
            }
            //same thing with times
            i1 = sortedTimes.indexOf(o1.getStartTime());
            i2 = sortedTimes.indexOf(o2.getStartTime());
            return i1.compareTo(i2);
        }
    };

    private final Comparator<Professor> sortByName = Comparator.comparing(Professor::getName);
    private final Comparator<Classroom> sortClassByName = Comparator.comparing(Classroom::getName);
    private final Comparator<Schedule> sortByCourse = new Comparator<Schedule>() {
        //helper fucntion to sort courses
        @Override
        public int compare(Schedule o1, Schedule o2) {
            int courseCompare = o1.getCourseID().compareTo(o2.getCourseID());
            if (courseCompare != 0) {
                return courseCompare;
            }
            Integer i1 = o1.getSection();
            Integer i2 = o2.getSection();
            return i1.compareTo(i2);
        }
    };
}
