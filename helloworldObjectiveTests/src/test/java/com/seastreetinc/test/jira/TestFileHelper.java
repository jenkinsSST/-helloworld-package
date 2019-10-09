/*
 * Company Confidential. Copyright 2019 by Sea Street Technologies, Incorporated. All rights reserved.
 */
package com.seastreetinc.test.jira;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper methods for reading test data.
 */
public class TestFileHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * Read test data from {@literal src/test/resources/fixtures} into specified type.
     * 
     * @return The deserialized <T>, else <code>null</code> on error.
     */
    public static <T> T readJSONFixtureResource(String filename, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return readJSONResource("dataSpecs/" + filename, clazz);
    }
    
    /**
     * Read test data into specified type.
     * 
     * @return The deserialized <T>, else <code>null</code> on error.
     */
    public static <T> T readJSONResource(String filename, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        InputStream is = TestJiraDriver.class.getResourceAsStream("/" + filename);
        if (is != null) {
            T textFixture = MAPPER.readValue(is, clazz);
            return textFixture;
        }
        else {
            return null;
        }
    }
    
    /**
     * Read test data into specified type.
     * 
     * @return The deserialized <T>, else <code>null</code> on error.
     */
    public static <T> T readJSONFromFilesystem(String filename, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        InputStream is = new FileInputStream(filename);
        T textFixture = MAPPER.readValue(is, clazz);
        return textFixture;
    }
    
}
