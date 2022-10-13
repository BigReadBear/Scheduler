package main;

public class Scheduler {

    //helper class to keep schedule entry if opening found
    public static class ScheduleFound {
        public String className;
        public String days;
        public String startTime;
        public String endTime;

        public ScheduleFound(String className, String days, String startTime, String endTime) {
            this.className = className;
            this.days = days;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    // array representing half hour blocks per day M,T,W,R,F for class A
    // 8.30-9.00|9.00-9.30|9.30-10.00|10.00-10.30|10.30-11.00|11.00-11.30|11.30-12.00|
    // 12.00-12.30|12.30-1.00|1.00-1.30|1.30-2.00|2.00-2.30|2.30-3.00|3.00-3.30|3.30-4.00|4.00-4.30
    // a 0 representing free a 1 representing occupied.
    private static int[][] scheduleA = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    // array representing half hour blocks per day M,T,W,R,F for class B
    // 8.30-9.00|9.00-9.30|9.30-10.00|10.00-10.30|10.30-11.00|11.00-11.30|11.30-12.00|
    // 12.00-12.30|12.30-1.00|1.00-1.30|1.30-2.00|2.00-2.30|2.30-3.00|3.00-3.30|3.30-4.00|4.00-4.30
    // a 0 representing free a 1 representing occupied.
    private static int[][] scheduleB = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    // array representing half hour blocks per day M,T,W,R,F for class C
    // 8.30-9.00|9.00-9.30|9.30-10.00|10.00-10.30|10.30-11.00|11.00-11.30|11.30-12.00|
    // 12.00-12.30|12.30-1.00|1.00-1.30|1.30-2.00|2.00-2.30|2.30-3.00|3.00-3.30|3.30-4.00|4.00-4.30
    // a 0 representing free a 1 representing occupied.
    private static int[][] scheduleC = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    //main logic to schedule class
    public static ScheduleFound scheduleClass(int credits, String days, String preferredStartTime,
                                              String preferredEndTime) {
        //check if initial times are open
        ScheduleFound scheduleFound = checkTimeBlock(credits, days, preferredStartTime, preferredEndTime);
        //if schedule is not found we check alternatives
        if (scheduleFound == null) {
            scheduleFound = checkAlternative(credits, days, preferredStartTime, preferredEndTime);
        }
        return scheduleFound;
    }

    //helper function to check alternative timeslots
    private static ScheduleFound checkAlternative(int credits, String days, String preferredStartTime, String preferredEndTime) {
        //switch statements for credits, since this dictates where to look for alternative times
        switch (credits) {
            case 3, 4:
                //see if the same time is open on opposite days
                ScheduleFound scheduleFound = checkOppositeDays(credits, days, preferredStartTime, preferredEndTime);
                //if we cant find this
                if (scheduleFound == null) {
                    //we attempt to find a suitable time block
                    scheduleFound = findSuitableTimeBlock(credits, days, preferredStartTime, preferredEndTime);
                }
                return scheduleFound;
                //check other time blocks for 1 and 2 credits
            case 1,2:
                return findSuitableTimeBlock(credits, days, preferredStartTime, preferredEndTime);
        }
        return null;
    }

    //helper function to find suitable time block
    private static ScheduleFound findSuitableTimeBlock(int credits, String days, String preferredStartTime, String preferredEndTime) {
        ScheduleFound scheduleFound;
        switch (credits) {
            //switch statements for credits, since this dictates where to look for alternative times
            case 3, 4:
                //we attempt to find a suitable time block
                scheduleFound = findTimeBlockFourThreeCredits(credits, days, preferredStartTime);
                //last resort is we attempt to schedule it on fridays
                if (scheduleFound == null) {
                    return findTimeBlockFriday(credits);
                } else {
                    //else we found a schedule time
                    return scheduleFound;
                }
            case 2, 1:
                //we attempt to find a suitable time block
                scheduleFound = findTimeBlockTwoOneCredits(credits, days, preferredStartTime);
                //last resort is we attempt to schedule it on fridays
                if (scheduleFound == null) {
                    return findTimeBlockFriday(credits);
                } else {
                    //else we found a schedule time
                    return scheduleFound;
                }
        }
        return null;
    }

    //helper function to find timeblock on friday
    private static ScheduleFound findTimeBlockFriday(int credits) {
        // no timeslot found so try to schedule on Friday
        // credit 4 lastStartTime: 8
        // credit 3 lastStartTime: 9
        // credit 2 lastStartTime: 11
        // credit 1 lastStartTime: 13

        //switch cases for different credit hours
        switch (credits) {
            case 4:
                //check all timeblocks up to last starttime +1
                for (int i = 0; i < 9; i += 2) {
                    //see if we can schedule on friday for 4 half hours
                    ScheduleFound scheduleFound = checkTimeBlock(credits, "F", getTime(i), getTime(i + 4));
                    if (scheduleFound != null)
                        //if not it's not possible and we return an empty scheduleFound
                        return scheduleFound;
                }
                break;
            case 3:
                //check all timeblocks up to last starttime +1
                for (int i = 1; i < 10; i += 2) {
                    //see if we can schedule on friday for 3 half hours
                    ScheduleFound scheduleFound = checkTimeBlock(credits, "F", getTime(i), getTime(i + 3));
                    if (scheduleFound != null)
                        //if not it's not possible and we return an empty scheduleFound
                        return scheduleFound;
                }
                break;
            case 2:
                //check all timeblocks up to last starttime +1
                for (int i = 1; i < 12; i += 2) {
                    //see if we can schedule on friday for 2 half hours
                    ScheduleFound scheduleFound = checkTimeBlock(credits, "F", getTime(i), getTime(i + 4));
                    if (scheduleFound != null)
                        //if not it's not possible and we return an empty scheduleFound
                        return scheduleFound;
                }
                break;
            case 1:
                //check all timeblocks up to last starttime +1
                for (int i = 1; i < 14; i += 2) {
                    //see if we can schedule on friday for 2 half hours
                    ScheduleFound scheduleFound = checkTimeBlock(credits, "F", getTime(i), getTime(i + 2));
                    if (scheduleFound != null)
                        //if not it's not possible and we return an empty scheduleFound
                        return scheduleFound;
                }
                break;
        }
        return null;
    }


    //helper function to find a time block with 3-4 credit hour rules applied
    private static ScheduleFound findTimeBlockFourThreeCredits(int credits, String days, String preferredStartTime) {
        String newStartTime;
        String newEndTime;
        String nextDays;
        //get start index in 2d array
        int startIndex = getStartIndex(credits, preferredStartTime);
        ScheduleFound scheduleFound;
        int i = startIndex + credits; //set duration
        //loop through the whole day
        while (i < 16 - credits) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + credits);
            //check timeblocks
            scheduleFound = checkTimeBlock(credits, days, newStartTime, newEndTime);
            //if no block was found
            if (scheduleFound != null) {
                return scheduleFound;
            }
            if (credits == 4) {
                i += 4; //go to next start time
            } else {
                i += 2; // credit three starts at the hour mark
            }
        }
        // not found, try TR, same thing
        i = credits == 4 ? 0 : 1;
        nextDays = days.equals("MW") ? "TR" : "MW";
        while (i < 16 - credits) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + 4);
            scheduleFound = checkTimeBlock(credits, nextDays, newStartTime, newEndTime);
            if (scheduleFound != null) {
                return scheduleFound;
            }
            if (credits == 4) {
                i += 4;
            } else {
                i += 2;
            }
        }
        // not found, loop around, check before startTime at the same preferred day, same thing
        i = credits == 4 ? 0 : 1;
        while (i < startIndex - credits + 1) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + 4);
            scheduleFound = checkTimeBlock(credits, nextDays, newStartTime, newEndTime);
            if (scheduleFound != null) {
                return scheduleFound;
            }
            if (credits == 4) {
                i += 4;
            } else {
                i += 2;
            }
        }
        return null;
    }

    //helper function to find a time block with 3-4 credit hour rules applied
    private static ScheduleFound findTimeBlockTwoOneCredits(int credits, String days, String preferredStartTime) {
        String newStartTime;
        String newEndTime;
        //get start index in 2d array
        int startIndex = getStartIndex(credits, preferredStartTime);
        ScheduleFound scheduleFound;
        int i = startIndex + credits*2; //set duration
        //loop through the whole day
        while (i < 16 - credits*2) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + credits*2);
            //check timeblocks
            scheduleFound = checkTimeBlock(credits, days, newStartTime, newEndTime);
            //if no block was found
            if (scheduleFound != null) {
                return scheduleFound;
            }
            i+=2;
        }
        //same thing for opposite day
        i = 1;
        String nextDay = getNextDay(days);
        while (i < 16 - credits*2 && !nextDay.equals(days)) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + 4);
            nextDay = getNextDay(days);
            scheduleFound = checkTimeBlock(credits, nextDay, newStartTime, newEndTime);
            if (scheduleFound != null) {
                return scheduleFound;
            }
            i += 2;
            nextDay=getNextDay(nextDay);
        }
        // loop around, check before startTime at the same preferred day
        while (i < startIndex - credits + 1) {
            newStartTime = getTime(i);
            newEndTime = getTime(i + 4);
            scheduleFound = checkTimeBlock(credits, days, newStartTime, newEndTime);
            if (scheduleFound != null) {
                return scheduleFound;
            }
            i += 2;
        }
        return null;
    }

    //helper function to get the next day
    private static String getNextDay(String days) {
        if (days.equals("M"))
            return "T";
        if (days.equals("T"))
            return "W";
        if (days.equals("W"))
            return "R";
        if (days.equals("R"))
            return "M";
        return null;
    }

    //helper function to check the opposite day per classroom
    private static ScheduleFound checkOppositeDays(int credits, String days, String preferredStartTime, String preferredEndTime) {
        if (days.equals("MW")) {
            // check for opposite days TR first
            if (check(scheduleA, credits, getStartIndex(credits, preferredStartTime), "TR")) {
                return new ScheduleFound("Classroom A", "TR", preferredStartTime, preferredEndTime);
            }
            if (check(scheduleB, credits, getStartIndex(credits, preferredStartTime), "TR")) {
                return new ScheduleFound("Classroom B", "TR", preferredStartTime, preferredEndTime);
            }
            if (check(scheduleC, credits, getStartIndex(credits, preferredStartTime), "TR")) {
                return new ScheduleFound("Classroom C", "TR", preferredStartTime, preferredEndTime);
            }
        } else {
            // check for opposite days MW first
            if (check(scheduleA, credits, getStartIndex(credits, preferredStartTime), "MW")) {
                return new ScheduleFound("Classroom A", "MW", preferredStartTime, preferredEndTime);
            }
            if (check(scheduleB, credits, getStartIndex(credits, preferredStartTime), "MW")) {
                return new ScheduleFound("Classroom B", "MW", preferredStartTime, preferredEndTime);
            }
            if (check(scheduleC, credits, getStartIndex(credits, preferredStartTime), "MW")) {
                return new ScheduleFound("Classroom C", "MW", preferredStartTime, preferredEndTime);
            }
        }
        return null;
    }

    //helper fucntion to check time blocks per classroom
    private static ScheduleFound checkTimeBlock(int credits, String days, String preferredStartTime, String preferredEndTime) {
        if (!days.equals("F")) {
            if (check(scheduleA, credits, getStartIndex(credits, preferredStartTime), days)) {
                return new ScheduleFound("Classroom A", days, preferredStartTime, preferredEndTime);
            }
            if (check(scheduleB, credits, getStartIndex(credits, preferredStartTime), days)) {
                return new ScheduleFound("Classroom B", days, preferredStartTime, preferredEndTime);
            }
            if (check(scheduleC, credits, getStartIndex(credits, preferredStartTime), days)) {
                return new ScheduleFound("Classroom C", days, preferredStartTime, preferredEndTime);
            }
        } else {
            // Friday so only check classroom A
            if (check(scheduleA, credits, getStartIndex(credits, preferredStartTime), days)) {
                return new ScheduleFound("Classroom A", days, preferredStartTime, preferredEndTime);
            }
        }
        return null;
    }

    //helper function to check 2d array aka timeblocks
    private static boolean check(int[][] schedule, int credits, int index, String days) {
        //switch case to differentiate between credit hour classes
        switch (credits) {
            case 3, 4:
                if (days.equals("MW")) {
                    for (int i = index; i < index + credits; i++) {
                        //check MW
                        if (schedule[0][i] != 0 || schedule[2][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits, index, 0);
                    setSchedule(schedule, credits, index, 2);
                } else if (days.equals("TR")) {
                    for (int i = index; i < index + credits; i++) {
                        //check TR
                        if (schedule[1][i] != 0 || schedule[3][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits, index, 1);
                    setSchedule(schedule, credits, index, 3);
                } else {
                    // check friday
                    for (int i = index; i < index + credits; i++) {
                        if (schedule[4][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits, index, 4);
                }

                // there is room for this class in this classroom
                return true;
            case 2, 1:
                if (days.equals("M")) {
                    for (int i = index; i < index + credits * 2; i++) {
                        //check M
                        if (schedule[0][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits * 2, index, 0);
                }
                if (days.equals("T")) {
                    for (int i = index; i < index + credits * 2; i++) {
                        //check T
                        if (schedule[1][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits * 2, index, 1);
                }
                if (days.equals("W")) {
                    for (int i = index; i < index + credits * 2; i++) {
                        //check W
                        if (schedule[2][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits * 2, index, 2);
                }
                if (days.equals("R")) {
                    for (int i = index; i < index + credits * 2; i++) {
                        //check R
                        if (schedule[3][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits * 2, index, 3);
                }
                if (days.equals("F")) {
                    for (int i = index; i < index + credits * 2; i++) {
                        //check F
                        if (schedule[4][i] != 0) {
                            return false;
                        }
                    }
                    setSchedule(schedule, credits * 2, index, 4);
                }
                // there is room for this class in this classroom
                return true;
            default:
                return false;
        }
    }

    //actually schedule day in DB
    private static void setSchedule(int[][] schedule, int credits, int index, int day) {
        for (int i = index; i < index + credits; i++) {
            schedule[day][i] = 1;
        }
    }

    //helper function to get start index of class by check credits and start time
    private static int getStartIndex(int credit, String startTime) {
        int index;
        String[] times = startTime.split(":");
        int hours = Integer.parseInt(times[0]);
        if (hours < 5)
            hours += 12; //convert to 24-hours clock;
        if (credit == 4) {
            index = (hours - 8) * 2;
        } else {
            index = (hours - 9) * 2 + 1;
        }
        return index;
    }

    //helper function to convert 2d array index to time value
    private static String getTime(int index) {
        String hours = "00";
        String minutes = "00";
        int tmpHours;
        if ((index % 2) == 0) {
            tmpHours = index / 2 + 8;
            if (tmpHours > 12)
                tmpHours -= 12;
            hours = String.valueOf(tmpHours);
            minutes = "30";
        } else {
            tmpHours = index / 2 + 9;
            if (tmpHours > 12)
                tmpHours -= 12;
            hours = String.valueOf(tmpHours);
            minutes = "00";
        }
        return hours + ":" + minutes;
    }
}
