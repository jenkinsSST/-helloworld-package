/*
 * Company Confidential. Copyright 2019 by Sea Street Technologies, Incorporated. All rights reserved.
 */
package com.seastreetinc.test.jira;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.seastreetinc.gglib.exceptions.RestResponseException;
import com.seastreetinc.test.environment.ExecutionEnvironment.Driver;
import com.seastreetinc.test.environment.ExecutionEnvironment.Drivers;
import com.seastreetinc.test.reps.resource.items.ResourceTypeRep;
import com.seastreetinc.test.resource.usecases.UseCaseTest;
import com.seastreetinc.test.utils.resource.Resource;

/**
 * Verifies expected behaviors of the Jira resource driver.
 */
public class TestJiraDriver extends UseCaseTest {
	
	private static final String RD_JIRA = "rd.jira";
	private static final String RESTYPE_TICKET = "resource.ticket";
	private static final String RESTYPE_ISSUE = "resource.jira.issue";

    private static final String CREATE_TASK_ISSUE_DATA_POS01 = "createIssueSpecPos01-NoCustom.json";
    private static final String CREATE_TASK_ISSUE_DATA_NEG01 = "createIssueSpecNeg01.json";
	
	Driver jiraDriver = null;
	Resource resourceIssue = null;
	
	@Parameters({"environment"})
	@BeforeClass
	@Override
	public void beforeClass(@Optional("usecase") String envName) throws Exception {
		super.beforeClass(envName);
		// Verify we have both a Jira driver 
		Drivers drivers = env.getDrivers();
		Map<String, Driver> driverMap = drivers.getDrivers();
		Set<Entry<String, Driver>> set = driverMap.entrySet();
		
		for(Entry<String, Driver> next : set) {
			Driver driver = next.getValue();
			String driverType = driver.getType();
			if(driverType.equals(RD_JIRA)) {
				Assert.assertNull(jiraDriver, "This test only expects a single Jira driver.");
				jiraDriver = driver;
			}
		}
		Assert.assertNotNull(jiraDriver, "This test expects a Jira Driver to be setup through properties.");
	}

    @BeforeMethod(alwaysRun=true)
	public void beforeMethod() {
	    resourceIssue = null;
	}
	
    @Override
    @AfterMethod(alwaysRun=true)
    public void afterMethod() {
        super.afterMethod();
        if (resourceIssue != null) {
            try {
                resourceIssue.delete();
            } catch (Exception e) {
                Assert.fail(String.format("Unexpected exception on resource deletion: %s", e.getMessage()));
            }
        }
    }

	/**
	 * Verify that the RD under test supports the expected resource types.
	 */
	@Test(groups={"smoke"})
	public void jiraResourceTypesExist() throws Exception {
		ResourceTypeRep ticketType = jiraDriver.getResourceType(RESTYPE_TICKET);
		Assert.assertNotNull(ticketType, "This Jira driver doesn't support: " + RESTYPE_TICKET);
		
		ResourceTypeRep issueType = jiraDriver.getResourceType(RESTYPE_ISSUE);
		Assert.assertNotNull(issueType, "This Jira driver doesn't support: " + RESTYPE_ISSUE);
	}
	
    /**
     * Verify that a task can be created with the minimum set of required properties
     * (the assumption is that one of those properties is "summary").
     */
    @SuppressWarnings("unchecked")
    @Test(enabled=true, groups={"smoke"}) 
    public void jiraResourceCreateIssue01() throws Exception {
        
        HashMap<String, Object> issueProps = TestFileHelper.readJSONFixtureResource(CREATE_TASK_ISSUE_DATA_POS01, HashMap.class);
        resourceIssue = Resource.create(env, jiraDriver, RESTYPE_ISSUE, issueProps);
        
        Assert.assertNotNull(issueProps.get("summary"));
        Assert.assertNotNull(resourceIssue);
        Assert.assertEquals(resourceIssue.getRep().getProperties().get("summary"), issueProps.get("summary"));
    }
    
    /**
     * Verify that an exception is raised when we omit a required property.
     */
    @SuppressWarnings("unchecked")
    @Test(enabled=true, expectedExceptions=RestResponseException.class, groups={"smoke"}) 
    public void jiraResourceCreateIssueNeg01() throws Exception {
        
        HashMap<String, Object> issueProps = TestFileHelper.readJSONFixtureResource(CREATE_TASK_ISSUE_DATA_NEG01, HashMap.class);
        resourceIssue = Resource.create(env, jiraDriver, RESTYPE_ISSUE, issueProps);
    }

    /**
     * Verify that a resource property can be updated.
     * 
     * NYI
     */
    @SuppressWarnings("unchecked")
    @Test(enabled=false, groups={"smoke"}) 
    public void jiraResourceUpdateIssue01() throws Exception {
        
        Assert.fail("jiraResourceUpdateIssue01 not yet implemented");
    }
    
    /**
     * Verify that a resource comment and non-comment can be updated in a single call.
     * 
     * NYI
     */
    @SuppressWarnings("unchecked")
    @Test(enabled=false, groups={"smoke"}) 
    public void jiraResourceUpdateIssue02() throws Exception {
        
        Assert.fail("jiraResourceUpdateIssue02 not yet implemented");
    }
    
    /**
     * Verify that a resource property and transition can be updated in a single call.
     * 
     * NYI
     */
    @SuppressWarnings("unchecked")
    @Test(enabled=false, groups={"smoke"}) 
    public void jiraResourceUpdateIssue03() throws Exception {
        
        Assert.fail("jiraResourceUpdateIssue03 not yet implemented");
    }
   
    /**
     * Verifies that a Jira issue can be deleted.
     */
    @Test(enabled=false, dependsOnMethods={"jiraResourceUpdateIssue"}, groups={"smoke"})
	public void jiraResourceDeleteIssue() throws Exception {
		resourceIssue.delete();
		resourceIssue = null;
	}
	
}
