/*
 * Name: Main
 * Purpose: main class
 * */
package main;

import DAO.SchedulerDAO;
import SQLite.SchedulerImplSQLite;

import java.io.File;
import java.util.Scanner;


public class Main {

    //set-up interface with database
    SchedulerDAO scheduler = new SchedulerImplSQLite();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Main app = new Main();

        String[] options = {"1- read file",
                "2- report by date",
                "3- report by faculty member",
                "4- report by class",
                "5- exit"
        };

        int option;
        app.initializeScheduler();
        if (!app.readFile())
            app.exitScheduler();


        //handle CLI args
        while (true) {
            Scanner in = new Scanner(System.in);
            printMenu(options);
            option = in.nextInt();
            System.out.println();
            switch (option) {
                case 1 :
                    app.readFile();
                    break;
                case 2 :
                    app.generateReportByDayTime();
                    break;
                case 3 :
                    app.generateReportByFacultyMember();
                    break;
                case 4 :
                    app.generateReportByClass();
                    break;
                case 5 :
                    app.exitScheduler();
            }
        }
    }

    //function to exit app
    private void exitScheduler() {
        Scanner in = new Scanner(System.in);
        System.out.println("Keep database? (y)/n");
        String response = in.nextLine();
        //drop database if "n" is given
        if (response.equalsIgnoreCase("n")) {
            dropScheduler();
        }
        System.exit(0);
    }

    //print command line arguments
    private static void printMenu(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
        System.out.print("Choose option : ");
    }

    //asking for file name to read and read file
    private boolean readFile() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Enter file name");
            String response = in.nextLine();
            if (response.equals(""))
                return false;
            File file = new File(response);
            if (file.exists()) {
                scheduler.readNewScheduleData(file);
                return true;
            } else {
                System.out.println("File not found!");
            }
        }
    }

    //in case user enters new text file, ask to keep existing schedule
    private void initializeScheduler() {
        Scanner in = new Scanner(System.in);
        if (scheduler.exists()) {
            System.out.println("Database exists, do you want to clear it and create a new one? y/(n)");
            String response = in.nextLine();
            if (response.equalsIgnoreCase("y")) {
                scheduler.createNewScheduler();
            }
        } else {
            scheduler.createNewScheduler();
        }
    }

    //drop schedule
    private void dropScheduler() {
        scheduler.drop();
    }

    //gen report by day
    private void generateReportByDayTime(){
        SchedulerDAO schedulerDAO = new SchedulerImplSQLite();
        schedulerDAO.generateReportByDayTime();
    }

    //gen report per prof with credits
    private void generateReportByFacultyMember() {
        SchedulerDAO schedulerDAO = new SchedulerImplSQLite();
        schedulerDAO.generateReportByFacultyMember();
    }

    //gen report per class
    private void generateReportByClass() {
        SchedulerDAO schedulerDAO = new SchedulerImplSQLite();
        schedulerDAO.generateReportByClass();
    }

}