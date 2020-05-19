package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.Calendar;

/**
 * Manages the calculation for the actual semester.
 * 
 * @author Kunold
 *
 */
public class SemesterUtils {

    /**
     * The actual semester.
     */
    private static String semester;
    
    /**
     * Generates an instance for the calendar.
     */
    private static Calendar calendar = Calendar.getInstance();
    
    /**
     * The constructor of the SemesterUtils class.
     */
    private SemesterUtils() { }
    
    /**
     * Returns the internal {@link Calendar} for testing.
     * @return The internal {@link Calendar} for testing.
     */
    static Calendar getCalendar() {
        return calendar;
    }
    
    /**
     * Getter for the current semester. Calculates the current semester with the actual date.
     * @return The current semester as String.
     */
    public static String getSemester() {
        
        if (calendar.get(Calendar.MONTH) >= Calendar.APRIL && calendar.get(Calendar.MONTH) <= Calendar.SEPTEMBER) {
            semester = "sose" + formateDate(calendar.get(Calendar.YEAR));
        } else {
            if (calendar.get(Calendar.MONTH) >= Calendar.JANUARY && calendar.get(Calendar.MONTH) <= Calendar.MARCH) {
                semester = "wise" + formateDate(calendar.get(Calendar.YEAR) - 1)
                    + formateDate(calendar.get(Calendar.YEAR)); 
            } else {
                semester = "wise" + formateDate(calendar.get(Calendar.YEAR))
                    + (formateDate(calendar.get(Calendar.YEAR) + 1));
            }
            
        }
        
        return semester;
    }
    
    /**
     * Helper method to get only the last two digits of a year for the semester.
     * @param year the year of the semester.
     * @return the last two digits of the year.
     */
    private static int formateDate(int year) {
        return year % 100;
    }
    
}
