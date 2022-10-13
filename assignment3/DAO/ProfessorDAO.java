/*
 * Name: ProfessorDOA
 * Purpose: Interface with ProfessorDAOImplSQLite (professorsTable),
 * these 2 are separated in case you want to use a different DBMS
 * */
package DAO;

import main.Professor;

import java.util.List;

public interface ProfessorDAO {
    //get prof based on name (a.b.c) or TUID
    Professor get(String name);
    Professor get(int tuid);

    //get list of all profs
    List<Professor> getAll();

    //crud operations
    int insert(Professor prof);
    int delete(Professor professor);

}
