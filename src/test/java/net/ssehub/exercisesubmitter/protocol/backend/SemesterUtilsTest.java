package net.ssehub.exercisesubmitter.protocol.backend;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Calendar;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * Tests for the {@link SemesterUtils} class.
 * 
 * @author Kunold
 *
 */
public class SemesterUtilsTest {
    
    /**
     * Declares all Parameters for the test method.
     * @return Stream of all Parameters to test them.
     */
    static Stream<Arguments>  data() {
        return Stream.of(
            arguments(Calendar.JANUARY, "wise1920"),
            arguments(Calendar.FEBRUARY, "wise1920"),
            arguments(Calendar.MARCH, "wise1920"),
            arguments(Calendar.APRIL, "sose20"),
            arguments(Calendar.MAY, "sose20"),
            arguments(Calendar.JUNE, "sose20"),
            arguments(Calendar.JULY, "sose20"),
            arguments(Calendar.AUGUST, "sose20"),
            arguments(Calendar.SEPTEMBER, "sose20"),
            arguments(Calendar.OCTOBER, "wise2021"),
            arguments(Calendar.NOVEMBER, "wise2021"),
            arguments(Calendar.DECEMBER, "wise2021")
        );        
    }
    
    /**
     * Method to run all tests and set the Year for the test to 2020.
     * @param monthOfYear The month of the year (0-based)
     * @param expectedSemester The expected semester if year 2020 is used.
     */
    @ParameterizedTest(name = "Semester {index}/2020 -> {1}")
    @MethodSource("data")
    public void testGetSemester(int monthOfYear, String expectedSemester) {
        Calendar cd = SemesterUtils.getCalendar();
        cd.set(Calendar.YEAR, 2020);
        cd.set(Calendar.MONTH, monthOfYear);
        String actualValue = SemesterUtils.getSemester();
        assertEquals(expectedSemester, actualValue);
    }

}
