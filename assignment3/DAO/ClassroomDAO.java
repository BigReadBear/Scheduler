/*
* Name: ClassroomDOA
* Purpose: Interface with ClassroomDAOImplSQLite (classroomTable),
* these 2 are separated in case you want to use a different DBMS
* */

package DAO;

import main.*;

import java.util.List;

public interface ClassroomDAO {
    //get classroom based on name (a.b.c) or TUID
    Classroom get(String name);
    Classroom get(int tuid);

    //get list of all classrooms
    List<Classroom> getAll();

    //crud operations
    int insert(Classroom c);
    int update(Classroom c);
    int delete(Classroom c);
}
