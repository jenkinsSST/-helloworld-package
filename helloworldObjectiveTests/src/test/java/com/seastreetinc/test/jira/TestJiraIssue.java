/*
 * Company Confidential. Copyright 2019 by Sea Street Technologies, Incorporated. All rights reserved.
 */
package com.seastreetinc.test.jira;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seastreetinc.test.objective.dev.TestObjectivesCommon20;
import com.seastreetinc.test.objective.dev.helpers.AssertGenesys;
import com.seastreetinc.test.objective.dev.helpers.GenesysEvents;
import com.seastreetinc.test.objective.dev.helpers.GenesysIssues;
import com.seastreetinc.test.objective.dev.helpers.ObjectiveClient;
import com.seastreetinc.test.objective.dev.helpers.Timings;
import com.seastreetinc.test.objective.dev.helpers.event.EventStreamValidator;
import com.seastreetinc.test.objective.dev.helpers.event.IssueConditionBuilder.IssueSeverity;
import com.seastreetinc.test.reps.objective.ObjectiveRep;
import com.seastreetinc.test.reps.objective.ObjectiveRepBuilder;
import com.seastreetinc.test.reps.resource.items.ResourceRep;
import com.seastreetinc.test.utils.CurrentTestContext;
import com.seastreetinc.test.utils.TestProperties;
import com.seastreetinc.test.utils.resource.Resource;

/**
 * Test generic Jira issue objectives.  Requires real gear.
 * 
 * Note that these tests are dependent upon a Jira issue schema created for the
 * lab instance of our Jira server, and it is correctly configured for Jira webhooks
 * (which the RD-under-test's driver properties are configured for).  
 * The properties for creating issues against that schema are defined in this test, 
 * and are hard-coded throughout.<p/>
 * 
 * Note that some method invocations are dependent on state established by previous
 * method invocations. <p/>
 * 
 * Also note that some of these tests test against comment.  The schema for
 * an issue comment is copied here, to help you understand how the verified value is introspected.
 * <p/>
 * <pre>
      "comment":{
         "comments":[
            {
               "self":"http://10.70.0.75:8080/rest/api/2/issue/10114/comment/10058",
               "id":"10058",
               "author":{
                  "self":"http://10.70.0.75:8080/rest/api/2/user?username=admin",
                  "name":"admin",
                  "key":"admin",
                  "emailAddress":"tlainhart@seastreet.com",
                  "avatarUrls":{
                     "48x48":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=48",
                     "24x24":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=24",
                     "16x16":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=16",
                     "32x32":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=32"
                  },
                  "displayName":"Administrator",
                  "active":true,
                  "timeZone":"America/New_York"
               },
               "body":"Comment added by Postman",
               "updateAuthor":{
                  "self":"http://10.70.0.75:8080/rest/api/2/user?username=admin",
                  "name":"admin",
                  "key":"admin",
                  "emailAddress":"tlainhart@seastreet.com",
                  "avatarUrls":{
                     "48x48":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=48",
                     "24x24":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=24",
                     "16x16":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=16",
                     "32x32":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=32"
                  },
                  "displayName":"Administrator",
                  "active":true,
                  "timeZone":"America/New_York"
               },
               "created":"2019-03-29T10:14:57.310-0400",
               "updated":"2019-03-29T10:14:57.310-0400"
            },
            {
               "self":"http://10.70.0.75:8080/rest/api/2/issue/10114/comment/10059",
               "id":"10059",
               "author":{
                  "self":"http://10.70.0.75:8080/rest/api/2/user?username=admin",
                  "name":"admin",
                  "key":"admin",
                  "emailAddress":"tlainhart@seastreet.com",
                  "avatarUrls":{
                     "48x48":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=48",
                     "24x24":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=24",
                     "16x16":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=16",
                     "32x32":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=32"
                  },
                  "displayName":"Administrator",
                  "active":true,
                  "timeZone":"America/New_York"
               },
               "body":"comment added via postman",
               "updateAuthor":{
                  "self":"http://10.70.0.75:8080/rest/api/2/user?username=admin",
                  "name":"admin",
                  "key":"admin",
                  "emailAddress":"tlainhart@seastreet.com",
                  "avatarUrls":{
                     "48x48":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=48",
                     "24x24":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=24",
                     "16x16":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=16",
                     "32x32":"https://www.gravatar.com/avatar/f9c38521a0acd385c7a19328022310e9?d=mm&s=32"
                  },
                  "displayName":"Administrator",
                  "active":true,
                  "timeZone":"America/New_York"
               },
               "created":"2019-03-29T14:05:10.703-0400",
               "updated":"2019-03-29T14:05:10.703-0400"
            }
         ],
         "maxResults":2,
         "total":2,
         "startAt":0
      }
</pre>      
 */
public class TestJiraIssue extends TestObjectivesCommon20 {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ObjectiveTypes
    //
    private static final String OBJ_TYPE_JIRA_ISSUE = "jira/2.0/jiraIssue";
    
    // Event types
    //
    private static final String JIRA_UPDATE_EVENT_TYPE = "jira/2.0/event.jira.issue.update";
    
    // IssueTypes
    //
    private static final String ISSUE_TRANSITION_IN_PROGRESS = "issue.transition.in.progress";
    private static final String ISSUE_ADD_COMMENT_IN_PROGRESS = "issue.add.comment.in.progress";    
    private static final String ISSUE_ADDITIONAL_UPDATES_REQUIRED = "issue.additional.updates.required";
    
    // Objective/Resource Property Name Values
    // 
    private static final String PROP_JIRA_ISSUE = "jiraIssue";
    private static final String CUSTOM_FIELD_PROP_NAME = "customFieldMap";
    private static final String ID_PROP_NAME = "id";
    private static final String COMMENT_PROP_NAME = "addComment";
    private static final String STATUS_PROP_NAME = "status";
    private static final String TRANSITION_PROP_NAME = "transition";
    private static final String SUMMARY_PROP_NAME = "summary";
    private static final String ASSIGNEE_PROP_NAME = "assignee";
    private static final String NAME_PROP_NAME = "name";
    
    
    // Test schema property values.
    //
    private static final String ISSUE_IN_PROGRESS_ID = "21";
    
    private static final String ISSUE_IN_PROGRESS_NAME = "In Progress";

    protected boolean usingRealGear = false;

    private static final String CUSTOM_FIELD_TASK_NAME = "Associated Task";

    private static final String CUSTOM_FIELD_USER_NAME = "Interested User";

    private static final String CUSTOM_FIELD_USER_NAME_FIELD = "name";

    // Fields in support of a radio option custom field
    private static final String CUSTOM_FIELD_DELEGATE_NAME = "Delegate";
    private static final String CUSTOM_FIELD_DELEGATE_VALUE_FIELD = "value";
    private static final String CUSTOM_FIELD_SPEC_DELEGATE_VALUE1 = "No";
    
    // Fields in support of a datetime picker custom field
    private static final String CUSTOM_FIELD_HOLIDAY_NAME = "Favorite Holiday";
    private static final String CUSTOM_FIELD_SPEC_HOLIDAY_VALUE1 = "2019-05-24";    

    private static final String CUSTOM_FIELD_SPEC_TASK_VALUE0 = "some other task";
    private static final String CUSTOM_FIELD_SPEC_TASK_VALUE1 = "Parent Task";
    private static final String CUSTOM_FIELD_SPEC_USER_VALUE2 = "user1";
    private static final String CUSTOM_FIELD_SPEC_USER_VALUE3 = "user2";
    
    // Fields in support of a select option custom field
    private static final String CUSTOM_FIELD_COLOR_NAME = "Select Color";
    private static final String CUSTOM_FIELD_COLOR_VALUE_FIELD = "value";
    private static final String CUSTOM_FIELD_SPEC_COLOR_VALUE1 = "Green";
    
    // Fields in support of a multi-select option custom field
    private static final String CUSTOM_FIELD_CAR_NAME = "Cars";
    private static final String CUSTOM_FIELD_CAR_VALUE_FIELD = "value";
    private static final String CUSTOM_FIELD_SPEC_CAR_VALUE1 = "Golf";
    private static final String CUSTOM_FIELD_SPEC_CAR_VALUE2 = "Passat";

    // Fields in support of a datetime picker custom field
    private static final String CUSTOM_FIELD_OVERDUE_NAME = "Overdue";
    private static final String CUSTOM_FIELD_SPEC_OVERDUE_VALUE1 = "2019-05-27T12:42:00.000-0400";    

    // Fields in support of a multi-select user custom field
    private static final String CUSTOM_FIELD_MULTIUSER_NAME = "Multi-user picker";
    private static final String CUSTOM_FIELD_MULTIUSER_NAME_FIELD = "name";
    private static final String CUSTOM_FIELD_SPEC_MULTIUSER_VALUE1 = "user1";
    private static final String CUSTOM_FIELD_SPEC_MULTIUSER_VALUE2 = "user2";
    
    // Fields in support of a number valued (floating point) custom field
    private static final String CUSTOM_FIELD_NUMBER_NAME = "Number";
    private static final String CUSTOM_FIELD_SPEC_NUMBER_VALUE1 = "5.1";

    
    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC0 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_TASK_NAME + "\" : {\r\n" + 
            "        \"value\" :\"" + CUSTOM_FIELD_SPEC_TASK_VALUE0 + "\" \r\n" + 
            "    },\r\n" + 
            "    \"" + CUSTOM_FIELD_USER_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_USER_NAME_FIELD + "\" : \"" + CUSTOM_FIELD_SPEC_USER_VALUE2 + "\"} \r\n" + 
            "    }\r\n" + 
            "}";
    
    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC1 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_TASK_NAME + "\" : {\r\n" + 
            "        \"value\" :\"" + CUSTOM_FIELD_SPEC_TASK_VALUE1 + "\" \r\n" + 
            "    },\r\n" + 
            "    \"" + CUSTOM_FIELD_USER_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_USER_NAME_FIELD + "\" : \"" + CUSTOM_FIELD_SPEC_USER_VALUE2 + "\"} \r\n" + 
            "    }\r\n" + 
            "}";
    
    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC2 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_TASK_NAME + "\" : {\r\n" + 
            "        \"value\" : \"" + CUSTOM_FIELD_SPEC_TASK_VALUE0 + "\" \r\n" + 
            "    },\r\n" + 
            "    \"" + CUSTOM_FIELD_USER_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_USER_NAME_FIELD + "\" : \"" + CUSTOM_FIELD_SPEC_USER_VALUE3 + "\"} \r\n" + 
            "    }\r\n" + 
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC3 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_TASK_NAME + "\" : {\r\n" + 
            "        \"value\" : \"" + CUSTOM_FIELD_SPEC_TASK_VALUE1 + "\" \r\n" + 
            "    },\r\n" + 
            "    \"" + CUSTOM_FIELD_USER_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_USER_NAME_FIELD + "\" : \"" + CUSTOM_FIELD_SPEC_USER_VALUE3 + "\"} \r\n" + 
            "    }\r\n" + 
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC4 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_DELEGATE_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_DELEGATE_VALUE_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_DELEGATE_VALUE1 + "\"} \r\n" + 
            "    }\r\n" +
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC5 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_HOLIDAY_NAME + "\" : {\r\n" + 
            "        \"value\" : \"" + CUSTOM_FIELD_SPEC_HOLIDAY_VALUE1 + "\" \r\n" + 
            "    }\r\n" +
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC6 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_COLOR_NAME + "\" : {\r\n" + 
            "        \"value\" : { \"" + CUSTOM_FIELD_COLOR_VALUE_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_COLOR_VALUE1 + "\"} \r\n" + 
            "    }\r\n" +
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC7 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_CAR_NAME + "\" : {\r\n" + 
            "        \"value\" : ["
            +           "{ \"" + CUSTOM_FIELD_CAR_VALUE_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_CAR_VALUE1 + "\"},"
            +           "{ \"" + CUSTOM_FIELD_CAR_VALUE_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_CAR_VALUE2 + "\"}"
            +        "] \r\n" + 
            "    }\r\n" +
            "}";    

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC8 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_OVERDUE_NAME + "\" : {\r\n" + 
            "        \"value\" : \"" + CUSTOM_FIELD_SPEC_OVERDUE_VALUE1 + "\" \r\n" + 
            "    }\r\n" +
            "}";

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC9 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_MULTIUSER_NAME + "\" : {\r\n" + 
            "        \"value\" : ["
            +           "{ \"" + CUSTOM_FIELD_MULTIUSER_NAME_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_MULTIUSER_VALUE1 + "\"},"
            +           "{ \"" + CUSTOM_FIELD_MULTIUSER_NAME_FIELD + "\" : \"" +  CUSTOM_FIELD_SPEC_MULTIUSER_VALUE2 + "\"}"
            +        "] \r\n" + 
            "    }\r\n" +
            "}";    

    // String used for updating custom field
    private static final String CUSTOM_FIELD_SPEC10 = "{\r\n" + 
            "    \"" + CUSTOM_FIELD_NUMBER_NAME + "\" : {\r\n" + 
            "        \"value\" : " + CUSTOM_FIELD_SPEC_NUMBER_VALUE1 + " \r\n" + 
            "    }\r\n" +
            "}";

    
    // The specification for initial task creation
    private static final String INITIAL_TASK_SPEC = "{\r\n" + 
            "    \"project\" : {\r\n" + 
            "        \"key\" : \"TP\"\r\n" + 
            "    },\r\n" + 
            "    \"issuetype\" : {\r\n" + 
            "        \"name\" : \"Task\"\r\n" + 
            "    },\r\n" + 
            "    \"" + SUMMARY_PROP_NAME + "\" : \"Task created via createIssuePositive\",\r\n" + 
            "    \"reporter\" : {\"name\" : \"admin\"},\r\n" + 
            "    \"" + ASSIGNEE_PROP_NAME + "\" : {\"name\" : \"admin\"},\r\n" + 
            "    \"" + CUSTOM_FIELD_PROP_NAME + "\" :" + CUSTOM_FIELD_SPEC0 + "}";
    
    /**
     * An objective representation that lives across a subset of the test methods. 
     */
    private ObjectiveRep newIssueRep;
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        String useMockDriversStr = 
                TestProperties.getInstance().getTestProperties().getProperty("use_mock_drivers", "true");
        usingRealGear = useMockDriversStr.equals("false");
    }
    
	@Override
	public boolean cleanupObjectivesAfterMethod() {
		return false;
	}
	
    /**
     * Tests that an objective can be created.
     */
    @Test(enabled = true, groups = {"smoke"})
    public void testCreateNew() throws Exception {
        newIssueRep = createAndVerifyJiraIssueObjective("jiraIssue", newIssueProps());
    }
    
    /**
     * Tests that an objective can be realized.
     */
    @Test(enabled = true, dependsOnMethods = "testCreateNew", groups = {"smoke"})
    public void testRealize() throws Exception {
        newIssueRep = realizeAndVerifyIssue(newIssueRep);
    }
    
    /**
     * Tests that an objective can be unrealized, and then realized.
     */
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUnrealizeRealize() throws Exception {
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                    .expectIssueCondition().removedFromObjective(newIssueRep)
                            .withCode(GenesysIssues.ISSUE_UNREALIZE_PENDING)
                            .withSeverity(IssueSeverity.INFO)
                            .buildForEventStreamValidator()                   
                    .expectEventCondition()
                            .fromResource(newIssueRep)
                            .objectiveEventsOfType(GenesysEvents.EVENT_GENESYS_OBJECTIVE_BL_UNREALIZE_SUCCESS)
                            .buildForEventStreamValidator()                            
                    .build();                            
        validator.attachForEvents();
        
        newIssueRep = unrealizeObjective(newIssueRep);
        
        EventStreamValidator.verifyStream(validator);
        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertFullyUnrealized(newIssueRep);
        
        // === Realize
        validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                            .evaluateConditionsInAnyOrder()
                            .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .useEnvironmentAwareTimeout()
                    .expectIssueCondition().removedFromObjective(newIssueRep)
                            .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                            .withSeverity(IssueSeverity.INFO)
                            .buildForEventStreamValidator()
                    .build();                            
        validator.attachForEvents();
        
        newIssueRep = realizeObjective(newIssueRep);
        EventStreamValidator.verifyStream(validator);        

        newIssueRep = getObjective(newIssueRep);
        Assert.assertNotNull(newIssueRep);
    }
    
    /**
     * Tests that an objective instance can be created from the self-link
     * of another.
     */
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testCreateExisting() throws Exception {
        
        newIssueRep = getObjective(newIssueRep);
        
        List<String> resources = newIssueRep.getResources();
        Assert.assertEquals(1, resources.size());
        String resourceLink = newIssueRep.getResources().get(0);
        
        ObjectiveRep newObjRep = objectiveClient.createObjective(
                new ObjectiveRepBuilder()
                    .withName(newIssueRep.getName())
                    .withType(newIssueRep.getType())
                    .withDescription(newIssueRep.getDescription())
                    .withProperties(Collections.singletonMap("jiraIssue", resourceLink))
                    .build()
                );

        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                            .evaluateConditionsInAnyOrder()
                            .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .useEnvironmentAwareTimeout()
                    .expectIssueCondition().removedFromObjective(newObjRep)
                            .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                            .withSeverity(IssueSeverity.INFO)
                            .buildForEventStreamValidator()
                    .build();                            
        validator.attachForEvents();
        
        newObjRep = realizeObjective(newObjRep);
        EventStreamValidator.verifyStream(validator);
        
        newObjRep = getObjective(newObjRep);
        Assert.assertNotNull(newObjRep);
        AssertGenesys.assertFullyRealized(newObjRep); 
        
        Assert.assertEquals(newIssueRep.getProperty("jiraIssue"), newObjRep.getProperty("jiraIssue"));
    }
    
    /**
     * Verifies that the summary and transition properties can be successfully
     * updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testPropertyUpdates01() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_TRANSITION_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADDITIONAL_UPDATES_REQUIRED)
                                    .withSeverity(IssueSeverity.WARN)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        final String summaryVal = "updated via testPropertyUpdates01";
        final String inProgressId = ISSUE_IN_PROGRESS_ID;
        final String inProgressName = ISSUE_IN_PROGRESS_NAME;
        
        Map<String,Object> updateProps = new HashMap<>();
        Map<String,Object> transition = new HashMap<>();
        transition.put(ID_PROP_NAME, inProgressId);
        updateProps.put(TRANSITION_PROP_NAME, transition);
        updateProps.put(SUMMARY_PROP_NAME, summaryVal);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summaryVal);
        Assert.assertEquals(((HashMap<String, Object>)objRep.getProperty(STATUS_PROP_NAME)).get(NAME_PROP_NAME), inProgressName);
        
        /*
         * Get the resource, and verify the modified transition and summary.  The
         * modified transition will be reflected in the "status" property map as "name"
         * (the Jira RD does not return a 'transition' property.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summaryVal);
        
        Map<String, Object> statusProps = (Map<String, Object>) resProps.get(STATUS_PROP_NAME);
        Assert.assertEquals(statusProps.get(NAME_PROP_NAME), inProgressName);
    }
    
    /**
     * Verifies that the summary and comment properties can be successfully
     * updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testPropertyUpdates02() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADD_COMMENT_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADDITIONAL_UPDATES_REQUIRED)
                                    .withSeverity(IssueSeverity.WARN)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        final String summaryVal = "updated via testPropertyUpdates02";
        final String commentVal = "comment added via testPropertyUpdates02";
        
        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(SUMMARY_PROP_NAME, summaryVal);
        updateProps.put(COMMENT_PROP_NAME, commentVal);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);        
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summaryVal);
        
        /*
         * Get the resource, and verify the summary and added comment.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summaryVal);
        
        Map<String, Object> commentProps = (Map<String, Object>) resProps.get("comment");
        Assert.assertEquals(commentProps.get("total"), 1);
        List<Map<String, Object>> comments = (List<Map<String, Object>>)commentProps.get("comments");
        Assert.assertEquals(comments.size(), 1);
        Assert.assertEquals(comments.get(0).get("body"), commentVal);
    }
    
    /**
     * Verifies that the summary, transition and comment properties can be successfully
     * updated.
     * 
     * Note that there periodic timing issues experienced when, under some conditions,
     * the updated summary isn't being returned, as if the server is caching.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testPropertyUpdates03() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.MINS_1)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(3)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(3)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADD_COMMENT_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_TRANSITION_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADDITIONAL_UPDATES_REQUIRED)
                                    .withSeverity(IssueSeverity.WARN)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        final String summaryVal = "updated via testPropertyUpdates03";
        final String commentVal = "comment added via testPropertyUpdates03";
        
        final String inProgressId = ISSUE_IN_PROGRESS_ID;
        final String inProgressName = ISSUE_IN_PROGRESS_NAME;
        
        Map<String,Object> updateProps = new HashMap<>();
        Map<String,Object> transition = new HashMap<>();
        transition.put(ID_PROP_NAME, inProgressId);
        updateProps.put(TRANSITION_PROP_NAME, transition);
        
        updateProps.put(SUMMARY_PROP_NAME, summaryVal);
        updateProps.put(COMMENT_PROP_NAME, commentVal);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);        
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summaryVal);
        Assert.assertEquals(((HashMap<String, Object>)objRep.getProperty(STATUS_PROP_NAME)).get(NAME_PROP_NAME), inProgressName);
        
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summaryVal);
         
        Map<String, Object> commentProps = (Map<String, Object>) resProps.get("comment");
        Assert.assertEquals(commentProps.get("total"), 1);
        List<Map<String, Object>> comments = (List<Map<String, Object>>)commentProps.get("comments");
        Assert.assertEquals(comments.size(), 1);
        Assert.assertEquals(comments.get(0).get("body"), commentVal);
        
        Map<String, Object> statusProps = (Map<String, Object>) resProps.get(STATUS_PROP_NAME);
        Assert.assertEquals(statusProps.get(NAME_PROP_NAME), inProgressName);        
    }
    
    /**
     * Verifies that transition and comment properties can be successfully
     * updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testPropertyUpdates04() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(2)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADD_COMMENT_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_TRANSITION_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADDITIONAL_UPDATES_REQUIRED)
                                    .withSeverity(IssueSeverity.WARN)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        /*
         * The addComment property value is a string - transition value is
         * a map containing an 'id' identifier.
         */
        final String commentVal = "comment added via testPropertyUpdates04";
        final String inProgressId = ISSUE_IN_PROGRESS_ID;
        final String inProgressName = ISSUE_IN_PROGRESS_NAME;
        
        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(COMMENT_PROP_NAME, commentVal);
        Map<String,Object> transition = new HashMap<>();
        transition.put(ID_PROP_NAME, inProgressId);
        updateProps.put(TRANSITION_PROP_NAME, transition);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);        
        Assert.assertEquals(((HashMap<String, Object>)objRep.getProperty(STATUS_PROP_NAME)).get(NAME_PROP_NAME), inProgressName);
        
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        
        // Validate comment value
        Map<String, Object> commentProps = (Map<String, Object>) resProps.get("comment");
        Assert.assertEquals(commentProps.get("total"), 1);
        List<Map<String, Object>> comments = (List<Map<String, Object>>)commentProps.get("comments");
        Assert.assertEquals(comments.size(), 1);
        Assert.assertEquals(comments.get(0).get("body"), commentVal);

        // Validate transition value
        Map<String, Object> statusProps = (Map<String, Object>) resProps.get(STATUS_PROP_NAME);
        Assert.assertEquals(statusProps.get(NAME_PROP_NAME), inProgressName);        
    }
    
    /**
     * Verifies that the transition and comment properties can be successfully
     * updated along with a couple of other "normal" properties (summary, assignee).
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testPropertyUpdates05() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.MINS_1)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(3)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(3)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADD_COMMENT_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_TRANSITION_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(ISSUE_ADDITIONAL_UPDATES_REQUIRED)
                                    .withSeverity(IssueSeverity.WARN)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        final String summaryVal = "updated via testPropertyUpdates05";
        final String commentVal = "comment added via testPropertyUpdates05";
        final String userVal = "user1";
        
        final String inProgressId = ISSUE_IN_PROGRESS_ID;
        final String inProgressName = ISSUE_IN_PROGRESS_NAME;
        
        Map<String,Object> updateProps = new HashMap<>();
        Map<String,Object> transition = new HashMap<>();
        transition.put(ID_PROP_NAME, inProgressId);
        updateProps.put(TRANSITION_PROP_NAME, transition);
        
        updateProps.put(SUMMARY_PROP_NAME, summaryVal);
        updateProps.put(COMMENT_PROP_NAME, commentVal);
        
        final Map<String,String> assignee = Collections.singletonMap(NAME_PROP_NAME, userVal);
        updateProps.put(ASSIGNEE_PROP_NAME, assignee);        
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);        
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summaryVal);
        Assert.assertEquals(((HashMap<String, Object>)objRep.getProperty(ASSIGNEE_PROP_NAME)).get(NAME_PROP_NAME), userVal);
        Assert.assertEquals(((HashMap<String, Object>)objRep.getProperty(STATUS_PROP_NAME)).get(NAME_PROP_NAME), inProgressName);
        
        // Summary verification
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summaryVal);
        
        // Assignee verification
        Map<String, Object> assigneeProps = (Map<String, Object>)resProps.get(ASSIGNEE_PROP_NAME);
        Assert.assertEquals(assigneeProps.get(NAME_PROP_NAME), userVal);
        
        // Comment verification
        Map<String, Object> commentProps = (Map<String, Object>) resProps.get("comment");
        Assert.assertEquals(commentProps.get("total"), 1);
        List<Map<String, Object>> comments = (List<Map<String, Object>>)commentProps.get("comments");
        Assert.assertEquals(comments.size(), 1);
        Assert.assertEquals(comments.get(0).get("body"), commentVal);
        
        // Transition verification
        Map<String, Object> statusProps = (Map<String, Object>) resProps.get(STATUS_PROP_NAME);
        Assert.assertEquals(inProgressName, statusProps.get(NAME_PROP_NAME));        
    }
    
    /**
     * Verify that a simple map based property with a key identifier can be updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUpdateAssignee() throws Exception {
        newIssueRep = getObjective(newIssueRep);
                
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();

        final String userVal = "user1";
        final Map<String,Object> updateProps = new HashMap<>();
        final Map<String,String> assignee = Collections.singletonMap(NAME_PROP_NAME, userVal);
        updateProps.put(ASSIGNEE_PROP_NAME, assignee);
        
        ObjectiveRep updateRep = createUpdateRep(newIssueRep);
        updateRep.setProperties(updateProps);
        newIssueRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertHasNoIssues(newIssueRep);
        AssertGenesys.assertPropertyEquals(newIssueRep, ASSIGNEE_PROP_NAME, assignee);
        
        // Verify the resource update
        ResourceRep resource = Resource.getResource((String)newIssueRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        
        // Assignee verification
        Map<String, Object> assigneeProps = (Map<String, Object>)resProps.get(ASSIGNEE_PROP_NAME);
        Assert.assertEquals(assigneeProps.get(NAME_PROP_NAME), userVal);        
    }
    
    /**
     * The lab Jira has a custom field named "Associated Tasks", which has a string value.
     * Verify that the value can be updated on the resource, and re-read.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUpdateCustomField() throws Exception {
        newIssueRep = getObjective(newIssueRep);
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(newIssueRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC1);
        newIssueRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertHasNoIssues(newIssueRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(newIssueRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC1);
        
        /*
         * Get the resource, and verify the modified summary, and that our Jira customfield
         * was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)newIssueRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(CUSTOM_FIELD_TASK_NAME), CUSTOM_FIELD_SPEC_TASK_VALUE1);       
        
        Map<String, Object> interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE2);        
    }
    
    /**
     * Another variant of updating just one field of the custom field.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUpdateCustomField02() throws Exception {
        newIssueRep = getObjective(newIssueRep);
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(newIssueRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC3);
        newIssueRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertHasNoIssues(newIssueRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(newIssueRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC3);
        
        /*
         * Get the resource, and verify the modified summary, and that our Jira customfield
         * was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)newIssueRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(CUSTOM_FIELD_TASK_NAME), CUSTOM_FIELD_SPEC_TASK_VALUE1);       
        
        Map<String, Object> interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE3);        
    }
    
    /**
     * Test updating a custom field with radio button representation, and "option"
     * schema.  Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10200":{
      "required":false,
      "schema":{
         "type":"option",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons",
         "customId":10200
      },
      "name":"Delegate",
      "hasDefaultValue":false,
      "operations":[
         "set"
      ],
      "allowedValues":[
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10100",
            "value":"Yes",
            "id":"10100"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10101",
            "value":"No",
            "id":"10101"
         }
      ]
   }
}     * 
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldRadio() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC4);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC4);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Map<String, Object> delegate = (Map<String, Object>)resProps.get(CUSTOM_FIELD_DELEGATE_NAME);
        Assert.assertEquals(delegate.get(CUSTOM_FIELD_DELEGATE_VALUE_FIELD), CUSTOM_FIELD_SPEC_DELEGATE_VALUE1);        
    }
    
    /**
     * Test updating a custom field for a date schema.
     * Assumption is our test issue is configured as such, e.g.
     * 
{  
   "customfield_10201":{  
      "required":false,
      "schema":{  
         "type":"date",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:datepicker",
         "customId":10201
      },
      "name":"Favorite Holiday",
      "operations":[  
         "set"
      ]
   },
   "labels":{  
      "required":false,
      "schema":{  
         "type":"array",
         "items":"string",
         "system":"labels"
      },
      "name":"Labels",
      "autoCompleteUrl":"http://10.70.0.75:8080/rest/api/1.0/labels/suggest?query=",
      "operations":[  
         "add",
         "set",
         "remove"
      ]
   }
}     * 
     */
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldDate() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC5);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC5);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        String holiday = (String)resProps.get(CUSTOM_FIELD_HOLIDAY_NAME);
        Assert.assertEquals(holiday, CUSTOM_FIELD_SPEC_HOLIDAY_VALUE1);
    }    

    /**
     * Test updating a custom field with a multi-choice string schema, configured for
     * single-select.
     * 
     * Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10202":{
      "required":false,
      "schema":{
         "type":"option",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:select",
         "customId":10202
      },
      "name":"Select Color",
      "operations":[
         "set"
      ],
      "allowedValues":[
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10102",
            "value":"Red",
            "id":"10102"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10103",
            "value":"Blue",
            "id":"10103"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10104",
            "value":"Green",
            "id":"10104"
         }
      ]
   }
}     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldStringSingleSelect() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC6);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC6);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Map<String, Object> color = (Map<String, Object>)resProps.get(CUSTOM_FIELD_COLOR_NAME);
        Assert.assertEquals(color.get(CUSTOM_FIELD_COLOR_VALUE_FIELD), CUSTOM_FIELD_SPEC_COLOR_VALUE1);        
    }    

    /**
     * Test updating a custom field with an array of strings schema.
     * 
     * Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10203":{
      "required":false,
      "schema":{
         "type":"array",
         "items":"option",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:multiselect",
         "customId":10203
      },
      "name":"Cars",
      "operations":[
         "add",
         "set",
         "remove"
      ],
      "allowedValues":[
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10105",
            "value":"Golf",
            "id":"10105"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10106",
            "value":"Jetta",
            "id":"10106"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10107",
            "value":"Passat",
            "id":"10107"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10108",
            "value":"Tiguan",
            "id":"10108"
         },
         {
            "self":"http://10.70.0.75:8080/rest/api/2/customFieldOption/10109",
            "value":"Toureg",
            "id":"10109"
         }
      ]
   }
}        */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldStringArray() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC7);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC7);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        List<Map<String, Object>> color = (List<Map<String,Object>>)resProps.get(CUSTOM_FIELD_CAR_NAME);
        Assert.assertNotNull(color);
        Assert.assertTrue(color.size() == 2);
        String car1 = (String)color.get(0).get(CUSTOM_FIELD_CAR_VALUE_FIELD);
        String car2 = (String)color.get(1).get(CUSTOM_FIELD_CAR_VALUE_FIELD);

        Assert.assertFalse(car1.equals(car2));
        
        Assert.assertTrue(Arrays.asList(CUSTOM_FIELD_SPEC_CAR_VALUE1, CUSTOM_FIELD_SPEC_CAR_VALUE2)
                .stream().allMatch(c -> car1.equals(c) || car2.equals(c)));
    }    

    /**
     * Test updating a custom field with a datetime schema.
     * 
     * Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10204":{
      "required":false,
      "schema":{
         "type":"datetime",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:datetime",
         "customId":10204
      },
      "name":"Overdue",
      "operations":[
         "set"
      ]
   }
}
        */
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldDatetime() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC8);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC8);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        String overdue = (String)resProps.get(CUSTOM_FIELD_OVERDUE_NAME);
        Assert.assertEquals(overdue, CUSTOM_FIELD_SPEC_OVERDUE_VALUE1);
     }    

    /**
     * Test updating a custom field with an array of users schema.
     * 
     * Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10206":{
      "required":false,
      "schema":{
         "type":"array",
         "items":"user",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:multiuserpicker",
         "customId":10206
      },
      "name":"Multi-user picker",
      "autoCompleteUrl":"http://10.70.0.75:8080/rest/api/1.0/users/picker?fieldName=customfield_10206&query=",
      "operations":[
         "add",
         "set",
         "remove"
      ]
   }
}
        */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldUserArray() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC9);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC9);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        List<Map<String, Object>> users = (List<Map<String,Object>>)resProps.get(CUSTOM_FIELD_MULTIUSER_NAME);
        Assert.assertNotNull(users);
        Assert.assertTrue(users.size() == 2);
        String user1 = (String)users.get(0).get(CUSTOM_FIELD_MULTIUSER_NAME_FIELD);
        String user2 = (String)users.get(1).get(CUSTOM_FIELD_MULTIUSER_NAME_FIELD);

        Assert.assertFalse(user1.equals(user2));
        
        Assert.assertTrue(Arrays.asList(CUSTOM_FIELD_SPEC_MULTIUSER_VALUE1, CUSTOM_FIELD_SPEC_MULTIUSER_VALUE2)
                .stream().allMatch(c -> user1.equals(c) || user2.equals(c)));
    }    

    /**
     * Test updating a custom field with a number.
     * 
     * Assumption is our test issue is configured as such e.g.
     * 
{
   "customfield_10207":{
      "required":false,
      "schema":{
         "type":"number",
         "custom":"com.atlassian.jira.plugin.system.customfieldtypes:float",
         "customId":10207
      },
      "name":"Number",
      "operations":[
         "set"
      ]
   }
}
    */
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateCustomFieldNumber() throws Exception {
        ObjectiveRep objRep = createRealizedObjective();       
        
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperty(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC10);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        // Verify the custom field value, locally and remotely
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC10);
        
        /*
         * Get the resource, and verify and that our Jira customfield was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Double numVal = (Double)resProps.get(CUSTOM_FIELD_NUMBER_NAME);
        Assert.assertEquals(numVal, Double.parseDouble(CUSTOM_FIELD_SPEC_NUMBER_VALUE1));        
    }    
    
    /**
     * Verify that a string-based property can be successfully updated.
     */
    @Test(dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUpdateSummary01() throws Exception {
        newIssueRep = getObjective(newIssueRep);
                
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(newIssueRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        Map<String,Object> updateProps = new HashMap<>();
        final String summary = "Summary update from testUpdateSummary";
        updateProps.put(SUMMARY_PROP_NAME, summary);
        
        ObjectiveRep updateRep = createUpdateRep(newIssueRep);
        updateRep.setProperties(updateProps);
        newIssueRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertHasNoIssues(newIssueRep);
        AssertGenesys.assertPropertyEquals(newIssueRep, SUMMARY_PROP_NAME, summary);
    }
    
    /**
     * Verify that a string-based property can be successfully updated,
     * along with only one property of a customFieldMap.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateSummary02() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();        
                
        /*
         * Verify that the following issues are added then removed.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();

        /*
         * Add in the summary and custom fields map
         */
        Map<String,Object> updateProps = new HashMap<>();
        final String summary = "Summary update from testUpdateSummary02";
        updateProps.put(SUMMARY_PROP_NAME, summary);

        updateProps.put(CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC2);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summary);
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC2);   
        
        /*
         * Get the resource, and verify the modified summary, and that our Jira customfield
         * was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summary);
        
        Assert.assertEquals(resProps.get(CUSTOM_FIELD_TASK_NAME), CUSTOM_FIELD_SPEC_TASK_VALUE0);
        Map<String, Object> interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE3);
    }
    
    /**
     * Verifies that updating a resource "out-of-band" (not updating via objective BL)
     * results in an eventual update of the objective property, and generates a
     * custom event in kind.<p/>
     * 
     * This test tests for changes to custom fields.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testOutOfBandUpdates() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();        
                
        final String summary = "Summary update from testOutOfBandUpdates";
        final String customFields = CUSTOM_FIELD_SPEC2; 
        
        /*
         * Verify that the following issues are added then removed, and that
         * we receive the expected event.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectEventCondition().objectiveEventsOfType(JIRA_UPDATE_EVENT_TYPE)
                                    .withPropertyKeyAndAnyValue("changedProperties")
                                    .buildForEventStreamValidator()
                            .build();
        validator.attachForEvents();

        /*
         * Add in the summary and custom fields map
         */
        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(SUMMARY_PROP_NAME, summary);
        updateProps.put(CUSTOM_FIELD_PROP_NAME, customFields);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summary);
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, customFields);
        
        /*
         * Get the resource, and verify the modified summary, and that our Jira customfield
         * was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summary);
        
        Assert.assertEquals(resProps.get(CUSTOM_FIELD_TASK_NAME), CUSTOM_FIELD_SPEC_TASK_VALUE0);
        Map<String, Object> interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE3);
        
        /*
         * Update the resource out-of-band (change the summary), verify that the
         * objective got updated.
         */
        validator = EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectEventCondition().objectiveEventsOfType(JIRA_UPDATE_EVENT_TYPE)
                                    .withPropertyKeyAndAnyValue("changedProperties")
                                    .buildForEventStreamValidator()
                            .build();
        validator.attachForEvents();
        
        final String newSummary = "test out-of-band update";
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put(SUMMARY_PROP_NAME, newSummary);
        resource = Resource.updateResource(resource.getSelf(), summaryMap, MediaType.APPLICATION_JSON);
        resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), newSummary);
        
        EventStreamValidator.verifyStream(validator);

        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, newSummary);
    }
    
    /**
     * Verifies that updating a resource "out-of-band" (not updating via objective BL)
     * results in an eventual update of the objective property, and generates a
     * custom event in kind.<p/>
     * 
     * This test tests for changes to custom fields.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testOutOfBandUpdates02() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();        
                
        final String summary = "Summary update from testOutOfBandUpdates";
        final String customFields = CUSTOM_FIELD_SPEC2; 
        
        /*
         * Verify that the following issues are added then removed, and that
         * we receive the expected event.
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                    .withSeverity(IssueSeverity.INFO)
                                    .buildForEventStreamValidator()
                            .expectEventCondition().objectiveEventsOfType(JIRA_UPDATE_EVENT_TYPE)
                                    .withPropertyKeyAndAnyValue("changedProperties")
                                    .buildForEventStreamValidator()
                            .build();
        validator.attachForEvents();

        /*
         * Add in the summary and custom fields map
         */
        Map<String,Object> updateProps = new HashMap<>();
        updateProps.put(SUMMARY_PROP_NAME, summary);
        updateProps.put(CUSTOM_FIELD_PROP_NAME, customFields);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, SUMMARY_PROP_NAME, summary);
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, customFields);
        
        /*
         * Get the resource, and verify the modified summary, and that our Jira customfield
         * was updated as expected.
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        Assert.assertEquals(resProps.get(SUMMARY_PROP_NAME), summary);
        
        Assert.assertEquals(resProps.get(CUSTOM_FIELD_TASK_NAME), CUSTOM_FIELD_SPEC_TASK_VALUE0);
        Map<String, Object> interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE3);        
        
        /*
         * Update the resource out-of-band (change the custom fields), verify that the
         * objective got updated.
         */
        validator = EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .expectIssueCondition().removedFromObjective(objRep)
                                    .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                    .withSeverity(IssueSeverity.INFO)
                                    .times(1)
                                    .buildForEventStreamValidator()
                            .expectEventCondition().objectiveEventsOfType(JIRA_UPDATE_EVENT_TYPE)
                                    .withPropertyKeyAndAnyValue("changedProperties")
                                    .buildForEventStreamValidator()
                            .build();
        validator.attachForEvents();
        
        /*
         * We're specifying a different user and task values for the customFieldMap, and verifying
         * that the objective will get updated for those properties, as captured in the
         * objective's customFieldMap objective property. 
         */
        Map<String, Object> fieldsMap = new HashMap<>();
        
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(CUSTOM_FIELD_USER_NAME_FIELD, CUSTOM_FIELD_SPEC_USER_VALUE2);
        fieldsMap.put(CUSTOM_FIELD_USER_NAME, newUser);
        
        fieldsMap.put(CUSTOM_FIELD_TASK_NAME, CUSTOM_FIELD_SPEC_TASK_VALUE1);
        
        resource = Resource.updateResource(resource.getSelf(), fieldsMap, MediaType.APPLICATION_JSON);
        
        /*
         * Verify the resource got updated
         */
        resProps = resource.getProperties();
        
        interestedUser = (Map<String, Object>)resProps.get(CUSTOM_FIELD_USER_NAME);
        Assert.assertEquals(interestedUser.get(CUSTOM_FIELD_USER_NAME_FIELD), CUSTOM_FIELD_SPEC_USER_VALUE2);
        
        String taskValue = (String)resProps.get(CUSTOM_FIELD_TASK_NAME);
        Assert.assertEquals(taskValue, CUSTOM_FIELD_SPEC_TASK_VALUE1);
        
        /*
         * Verify the objective' customFieldMap got updated
         */
        EventStreamValidator.verifyStream(validator);

        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        AssertGenesys.assertPropertyEquals(objRep, CUSTOM_FIELD_PROP_NAME, CUSTOM_FIELD_SPEC1);
    }
    
    
    /**
     * Verifies that just a comment can be updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, dependsOnMethods = "testRealize", groups = {"smoke"})
    public void testUpdateComment() throws Exception {
        newIssueRep = getObjective(newIssueRep);
        
        /*
         * Verify that the following issues are added then removed
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                        .expectIssueCondition().removedFromObjective(newIssueRep)
                                .withCode(ISSUE_ADD_COMMENT_IN_PROGRESS)
                                .withSeverity(IssueSeverity.INFO)
                                .buildForEventStreamValidator()                                    
                        .expectIssueCondition().removedFromObjective(newIssueRep)
                                .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                .withSeverity(IssueSeverity.INFO)
                                .buildForEventStreamValidator()
                        .expectIssueCondition().removedFromObjective(newIssueRep)
                                .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                .withSeverity(IssueSeverity.INFO)
                                .times(1)
                                .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        
        Map<String,Object> updateProps = new HashMap<>();
        final String comment = "Updated comment via 'testUpdateComment'";
        updateProps.put(COMMENT_PROP_NAME, comment);
        
        ObjectiveRep updateRep = createUpdateRep(newIssueRep);
        updateRep.setProperties(updateProps);
        newIssueRep = updateObjective(updateRep);     
        
        EventStreamValidator.verifyStream(validator);

        newIssueRep = getObjective(newIssueRep);
        AssertGenesys.assertHasNoIssues(newIssueRep);
        
        /*
         * Retrieve/verify the comment from the server resource.
         */
        ResourceRep resource = Resource.getResource((String)newIssueRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        
        Map<String, Object> commentProps = (Map<String, Object>) resProps.get("comment");
        Assert.assertEquals(1, commentProps.get("total"));
        List<Map<String, Object>> comments = (List<Map<String, Object>>)commentProps.get("comments");
        Assert.assertEquals(1, comments.size());
        Assert.assertEquals(comment, comments.get(0).get("body"));        
    }
    
    /**
     * Tests that just the transition property can be updated.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled = true, groups = {"smoke"})
    public void testUpdateTransition() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
                
        /*
         * Verify that the following issues are added then removed
         */
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                                    .useEnvironmentAwareTimeout()
                                    .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                        .expectIssueCondition().removedFromObjective(objRep)
                                .withCode(ISSUE_TRANSITION_IN_PROGRESS)
                                .withSeverity(IssueSeverity.INFO)
                                .buildForEventStreamValidator()                                    
                        .expectIssueCondition().removedFromObjective(objRep)
                                .withCode(GenesysIssues.ISSUE_RESOURCE_UPDATE_IN_PROGRESS)
                                .withSeverity(IssueSeverity.INFO)
                                .buildForEventStreamValidator()
                        .expectIssueCondition().removedFromObjective(objRep)
                                .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
                                .withSeverity(IssueSeverity.INFO)
                                .times(1)
                                .buildForEventStreamValidator()
                        .build();
        validator.attachForEvents();
        
        final String inProgressId = ISSUE_IN_PROGRESS_ID;
        final String inProgressName = ISSUE_IN_PROGRESS_NAME;
        
        Map<String,Object> updateProps = new HashMap<>();
        Map<String,Object> transition = new HashMap<>();
        transition.put(ID_PROP_NAME, inProgressId);
        transition.put(NAME_PROP_NAME, inProgressName);
        updateProps.put(TRANSITION_PROP_NAME, transition);
        
        ObjectiveRep updateRep = createUpdateRep(objRep);
        updateRep.setProperties(updateProps);
        objRep = updateObjective(updateRep);     

        EventStreamValidator.verifyStream(validator);
        
        objRep = getObjective(objRep);
        AssertGenesys.assertHasNoIssues(objRep);
        
        /*
         * Verify the resource update. The transition state is identified in the "status"
         * property. 
         */
        ResourceRep resource = Resource.getResource((String)objRep.getProperty(PROP_JIRA_ISSUE), MediaType.APPLICATION_JSON);
        Map<String, Object> resProps = resource.getProperties();
        
        // Transition verification
        Map<String, Object> statusProps = (Map<String, Object>) resProps.get(STATUS_PROP_NAME);
        Assert.assertEquals(inProgressName, statusProps.get(NAME_PROP_NAME));              
    }

    /**
     * Tests that an objective can be unrealized.
     */
    @Test(enabled = true, groups = {"smoke"})
    public void testUnrealize() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                    .expectIssueCondition().removedFromObjective(objRep)
                            .withCode(GenesysIssues.ISSUE_UNREALIZE_PENDING)
                            .withSeverity(IssueSeverity.INFO)
                            .buildForEventStreamValidator()                   
                    .expectEventCondition()
                            .fromResource(objRep)
                            .objectiveEventsOfType(GenesysEvents.EVENT_GENESYS_OBJECTIVE_BL_UNREALIZE_SUCCESS)
                            .buildForEventStreamValidator()                            
                    .build();                            
        validator.attachForEvents();
        
        objRep = unrealizeObjective(objRep);
        
        EventStreamValidator.verifyStream(validator);
        objRep = getObjective(objRep);
        AssertGenesys.assertFullyUnrealized(objRep);
    }
    
    /**
     * Tests that the objective can be destroyed, and that its associated resource
     * continues to exist.
     */
    @Test(enabled = true, groups = {"smoke"})
    public void testDestroy() throws Exception {
        
        ObjectiveRep objRep = createRealizedObjective();
        
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                    .expectIssueCondition().removedFromObjective(objRep)
                            .withCode(GenesysIssues.ISSUE_UNREALIZE_PENDING)
                            .withSeverity(IssueSeverity.INFO)
                            .buildForEventStreamValidator()                   
                    .expectEventCondition()
                            .fromResource(objRep)
                            .objectiveEventsOfType(GenesysEvents.EVENT_GENESYS_OBJECTIVE_BL_UNREALIZE_SUCCESS)
                            .buildForEventStreamValidator()                            
                    .build();                            
        validator.attachForEvents();
        
        String repSelf = objRep.getSelf();
        String resSelf = (String)objRep.getProperty(PROP_JIRA_ISSUE);
        
        objRep = unrealizeObjective(objRep);        
        EventStreamValidator.verifyStream(validator);
        
        destroyObjectiveAndConstituents(objRep, Status.OK);
        
        // Verify that the objective is gone, but that the Jira issue continues to exist.
        assertNull(ObjectiveClient.getObjective(repSelf, Status.NOT_FOUND));
        ResourceRep resource = Resource.getResource(resSelf, MediaType.APPLICATION_JSON);
        assertNotNull(resource);
    }
    
    /**
     * Verify that the objective cannot be destroyed in the realized state (i.e
     * the server returns 500).
     */
    @Test(enabled = true, groups = {"smoke"})
    public void testDestroyFailure() throws Exception {
        ObjectiveRep rep = createAndVerifyJiraIssueObjective("jiraIssue", newIssueProps());
        realizeAndVerifyIssue(rep);
        
        // exception will be thrown since the instance is still realized
        destroyObjectiveAndConstituents(rep, Status.INTERNAL_SERVER_ERROR);        
    }
    
    /**
     * Creates an unrealized objective on the StratOS server with the specified
     * name and properties.
     * 
     * @param name The name used as a basename for the objective. Must not be <code>null</code>. 
     * @param properties The properties used to create the objective. Must not be <code>null</code>.
     * @return The created objective. Never <code>null</code>.
     * @throws Exception Thrown on creation errors.
     */
    private ObjectiveRep createAndVerifyJiraIssueObjective(String name, Map<String,Object> properties) throws Exception
    {
        ObjectiveRep issueRep = objectiveClient.createObjective(buildObjectiveName(name),
                OBJ_TYPE_JIRA_ISSUE, "", properties);
        Assert.assertNotNull(issueRep);
        AssertGenesys.assertNotRealized(issueRep);
        return issueRep;
    }
    
    /**
     * Changes the state of the referenced issueRep to "realized", and verifies same.
     * @param issueRep The objective to change state on. Must not be <code>null</code>.
     * @return The realized objective.
     * @throws Exception Thrown if the objective's state cannot be transitioned to
     * realized.
     */
    private ObjectiveRep realizeAndVerifyIssue(ObjectiveRep issueRep) throws Exception {
        EventStreamValidator validator =
                EventStreamValidator.getValidatorBuilderOnServer(this.dbServerConfiguration)
                            .evaluateConditionsInAnyOrder()
                            .timeoutAfterNoEventsFor(Timings.Pad.SECS_30)
                            .useEnvironmentAwareTimeout()
                    .expectIssueCondition().removedFromObjective(issueRep)
                            .withCode(GenesysIssues.ISSUE_RESOURCE_MISSING)
                            .withParameter("jiraIssue")
                            .withSeverity(IssueSeverity.WARN)
                            .buildForEventStreamValidator()
//                    .expectIssueCondition().removedFromObjective(issueRep)
//                            .withCode(GenesysIssues.ISSUE_RESOURCE_STATE_OUT_OF_SYNC)
//                            .withSeverity(IssueSeverity.INFO)
//                            .buildForEventStreamValidator()
                    .build();                            
        validator.attachForEvents();
        
        issueRep = realizeObjective(issueRep);
        
        EventStreamValidator.verifyStream(validator);
        
        return issueRep;
    }  
    
    /**
     * Returns an issue name starting with <code>prefix</code>, qualified by the test name.
     */
    static private String buildObjectiveName(String prefix) {
        return prefix + "-" + CurrentTestContext.getQualifiedTestName();
    }    

    /**
     * Creates and returns a realized objective (which will create the Jira resource
     * as well). The returned objective is populated with properties retrieved from
     * its associated resource.
     *  
     * @return The realized/populated objective. Never <code>null</code>.
     */
    private ObjectiveRep createRealizedObjective()
            throws Exception, JsonParseException, JsonMappingException, IOException
    {
        ObjectiveRep objRep = createAndVerifyJiraIssueObjective("jiraIssue", newIssueProps());
        objRep = realizeAndVerifyIssue(objRep);
        
        /*
         * Retrieve the objective again, so that the "possibleTransitions" property will
         * get loaded.
         */
        objRep = getObjective(objRep);
        return objRep;
    }
    
    
    /**
     * Returns a map of properties required to create a Jira issue on the target server.
     * 
     * @return The property map - never <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> newIssueProps() throws JsonParseException, JsonMappingException, IOException {
        return MAPPER.readValue(INITIAL_TASK_SPEC, HashMap.class);
    }
    
}
