package net.ssehub.exercisesubmitter.protocol.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.studentmgmt.backend_api.JSON;

/**
 * Abstract class that may be used to create a JSON based configuration.
 * Please note that methods are <tt>protected</tt> by default and may be declared as <tt>public</tt> in sub classes
 * if desired.
 * @param <C> the configuration class to be used.
 * @author El-Sharkawy
 *
 */
public abstract class AbstractSettings<C> {
    
    private static final Logger LOGGER = LogManager.getLogger(AbstractSettings.class);
       
    private C config;
    private JSON jsonParser;
    
    /**
     * Default constructor for inherited classes.
     */
    protected AbstractSettings() {
        jsonParser = JsonUtils.createParser();
      
    }
    
    /**
     * Loads the configuration from the settings file.
     * Needs to be done <b>once</b> at startup.
     * Tries to load the settings as follows:
     * <ol>
     *   <li>Searches for {@link #getSettingsFileName()} relative to JAR</li>
     *   <li>Searches for {@link #getSettingsFileName()} inside the JAR</li>
     * </ol>
     * @throws IOException If the default configuration could not be read.
     */
    public void init() throws IOException {
        // Based on https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
        try {
            String content;
            File inputFile = new File(getSettingsFileName());
            if (inputFile.exists()) {
                // Load relative to JAR
                LOGGER.debug("Loading application settings from file {}.", inputFile.getAbsoluteFile());
                content = Files.readString(inputFile.toPath());
            } else {
                // Load from resource folder (e.g. bundled inside jar)
                LOGGER.debug("Loading application settings from resource /{}.", getSettingsFileName());
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/" + getSettingsFileName())))) {
                    
                    StringBuilder tmp = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        tmp.append(line);
                    }
                    content = tmp.toString();
                }
            }
            loadConfig(content);
        } catch (IOException e) {
            LOGGER.warn("Could not read configuration from {}, cause {}", getSettingsFileName(), e);
            throw e;
        }
    }
    
    /**
     * Returns the configuration.
     * @return The configuration.
     */
    protected C getConfiguration() {
        return config;
    }
        
    /**
     * Returns the configuration class.
     * Required since generics are not available during runtime and the configuration class is needed to load the first
     * instance.
     * @return the configuration class.
     */
    protected abstract Class<C> getConfigClass();
    
    /**
     * Parsed the given configuration and loads it.
     * @param configAsJson The configuration to use at the whole application.
     */
    protected void loadConfig(String configAsJson) {
        config = jsonParser.deserialize(configAsJson, getConfigClass());
    }
    
    /**
     * The file name of the configuration to be used.
     * @return For instance <tt>settings.json</tt>
     */
    protected abstract String getSettingsFileName();
    
    /**
     * Allows setting the configuration in sub classes.
     * @param config The configuration to set.
     */
    protected void setConfiguration(C config) {
        this.config = config;
    }
    
    /**
     * Saves the currently used configuration.
     * May be used to create a new configuration or to create test cases.
     * @param out The writer to save the configuration.
     */
    protected void saveConfiguration(Writer out) {
        String configAsJson = jsonParser.serialize(config);
        try {
            out.write(configAsJson);
            out.flush();
        } catch (IOException e) {
            LOGGER.warn("Could not save configuration, cause {}", e);
        }
    }
    
}
