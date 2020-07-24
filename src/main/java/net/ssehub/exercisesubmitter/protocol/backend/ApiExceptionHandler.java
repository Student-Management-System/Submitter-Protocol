package net.ssehub.exercisesubmitter.protocol.backend;

import net.ssehub.studentmgmt.backend_api.ApiException;

/**
 * An exception handler, that handles typical cases of the exceptions which may be thrown by the APIs.
 * @author El-Sharkawy
 *
 */
class ApiExceptionHandler {

    /**
     * Avoids initialization of utility class.
     */
    private ApiExceptionHandler() {}
    
    /**
     * Handles some cases of thrown {@link ApiException}s or {@link IllegalArgumentException}s, which can independently
     * of the called REST function be handled.
     * If an {@link ApiException} is given but no NetworkException is thrown by this handler, than an individual case
     * occurred, which has to be handled separately. Usage:
     * <pre><code> } catch (Exception e) {
     *     ApiExceptionHandler.handleException(e, getBasePath());
     *     throw Some_Other_NetworkException(individual_answer);
     * }</code></pre>
     * @param exc The exception thrown when calling a REST function.
     * @param basePath The url of the REST server.
     * @throws NetworkException
     */
    static void handleException(Exception exc, String basePath) throws NetworkException {
        if (exc instanceof IllegalArgumentException) {
            throw new ServerNotFoundException(exc.getMessage(), basePath);
        }
        if (exc instanceof ApiException) {
            ApiException apiExc = (ApiException) exc;
            
            if (401 == apiExc.getCode()) {
                throw new UnauthorizedException("User not authorized, but required to query Assessments.");
            }
            
            String responseBody = apiExc.getResponseBody();
            if (responseBody != null && responseBody.contains("\"status\":500")
                && responseBody.contains("\"message\":\"pre:ZuulAuthorizationFilter\"")) {
                // Sparky services blocks the route because of access rules
                
                throw new ServerNotFoundException("Server could not be contacted, cause the authentication service "
                    + "blocks the route to the student management system due to missing access rights.", basePath);
            }
        }
    }
}
