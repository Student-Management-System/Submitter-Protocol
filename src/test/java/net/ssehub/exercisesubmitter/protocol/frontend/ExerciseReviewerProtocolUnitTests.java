//package net.ssehub.exercisesubmitter.protocol.frontend;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//
//
///**
// * Tests the {@link ExerciseReviewerProtocol} <b>without</b> querying the REST server.
// * @author Kunold
// *
// */
//public class ExerciseReviewerProtocolUnitTests {
//
//    private static final String TEST_BASE_PATH = "http://147.172.178.30:3000";
//    private static final String TEST_COURSE_NAME = "java";
//    private static final String TEST_ASSIGNMENT_ID = "1";
//    
//    /**
//     * Tests if the method is called.
//     */
//    @Disabled
//    @Test
//    public void testGetSubmissionRealUsersReviews() {
//       
//        ExerciseReviewerProtocol erp = new ExerciseReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME);
//        
//        String actual = erp.getSubmissionRealUsersReviews(TEST_ASSIGNMENT_ID);
//        
//        Assertions.assertNotNull(actual);
//    }
//    
//    /**
//     * Tests if the method is called.
//     */
//    @Disabled
//    @Test
//    public void testGetSubmissionReviewerUsers() {
//        
//        ExerciseReviewerProtocol erp = new ExerciseReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME);
//        
//        String actual = erp.getSubmissionReviewerUsers(TEST_ASSIGNMENT_ID);
//        
//        Assertions.assertNotNull(actual);
//    }
//    
//    /**
//     *  Tests if the method is called.
//     */
//    @Disabled
//    @Test
//    public void testGetSubmissionReviews() {
//        
//        ExerciseReviewerProtocol erp = new ExerciseReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME);
//        
//        String actual = erp.getSubmissionReviews(TEST_ASSIGNMENT_ID);
//        
//        Assertions.assertNotNull(actual);
//    }
//    
//}
