package net.ssehub.exercisesubmitter.protocol.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;


/**
 * This class declares <b>unit</b> tests for the {@link NetworkProtocol} class.
 * These tests won't communicate with the REST test server.
 * 
 * @author El-Sharkawy
 *
 */
public class NetworkProtocolUnitTests {
    
    private static final String TEST_COURSE_NAME = "java";
    
    /**
     * Test if the REST server is not found.
     */
    @Test
    public void testServerNotFound() throws ApiException {
        String url = "http://www.uni-hildesheim.de";
        
        // Mock used APIs: Throw internally expected exception when API is used with invalid URL
        UsersApi userApiMock = Mockito.mock(UsersApi.class);
        Mockito.when(userApiMock.getCoursesOfUser(Mockito.anyString()))
            .thenThrow(new IllegalArgumentException("Some detailed description why the server is not reachable"));
        
        NetworkProtocol np = new NetworkProtocol(url, TEST_COURSE_NAME, userApiMock, null, null);
        try {
            np.getCourses("userID");
            Assertions.fail("Expected ServerNotFoundException, but did not occur.");
        } catch (ServerNotFoundException e) {
            Assertions.assertEquals(url, e.getURL());
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }

}
