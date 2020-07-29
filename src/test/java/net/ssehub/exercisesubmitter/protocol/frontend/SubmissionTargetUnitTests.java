package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the correct behavior of the {@link SubmissionTarget}, which doesn't need any integration to remote server
 * for testing.
 * @author El-Sharkawy
 *
 */
public class SubmissionTargetUnitTests {
    
    /**
     * Tests that an absolute path is returned.
     */
    @Test
    public void testGetAbsolutePathInRepository() {
        SubmissionTarget target = new SubmissionTarget("a_url", new String[]{"root", "nested"});
        Assertions.assertTrue(target.getAbsolutePathInRepository().startsWith("/"));
    }

    /**
     * Tests that {@link SubmissionTarget#getAllSubmissionsURL()} creates a valid URL to the submissions folder.
     */
    @Test
    public void testGetAllSubmissionsURL() {
        // Provided an URL without an ending slash
        String url = "a_url";
        String assignmentFolder = "root";
        String submitterName = "a_user";
        SubmissionTarget target = new SubmissionTarget(url, new String[]{assignmentFolder, submitterName});
        Assertions.assertEquals(url + "/" + assignmentFolder, target.getAllSubmissionsURL());
        
        // Provided an URL with an ending slash (both situations may happen since this comes from configuration)
        target = new SubmissionTarget(url + "/", new String[]{assignmentFolder, submitterName});
        Assertions.assertEquals(url + "/" + assignmentFolder, target.getAllSubmissionsURL());
    }
    
    /**
     * Tests that {@link SubmissionTarget#getSubmissionURL()} creates a valid URL to the individual submission folder.
     */
    @Test
    public void testGetSubmissionURL() {
        // Provided an URL without an ending slash
        String url = "a_url";
        String assignmentFolder = "root";
        String submitterName = "a_user";
        SubmissionTarget target = new SubmissionTarget(url, new String[]{assignmentFolder, submitterName});
        Assertions.assertEquals(url + "/" + assignmentFolder + "/" + submitterName, target.getSubmissionURL());
        
        // Provided an URL with an ending slash (both situations may happen since this comes from configuration)
        target = new SubmissionTarget(url + "/", new String[]{assignmentFolder, submitterName});
        Assertions.assertEquals(url + "/" + assignmentFolder + "/" + submitterName, target.getSubmissionURL());
    }
}
