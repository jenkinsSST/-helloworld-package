package com.seastreetinc.test.jira;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seastreetinc.test.reps.resource.items.ResourceRep;
import com.seastreetinc.test.reps.resource.items.ResourceTypeRep;
import com.seastreetinc.test.utils.TestProperties;
import com.seastreetinc.test.utils.resource.Resource;

public class TestResourceDriver extends AbstractTests {

	@Test
	public void resourceTypesExist() throws Exception {

		final ArrayList<String> missingResourceTypes = new ArrayList<>();

		for (String resName : new String[] { RESTYPE_SAMPLE, /*  RESTYPE_INFOBLOX_ZONE_AUTH, RESTYPE_INFOBLOX_RECORD_HOST */}) {
			final ResourceTypeRep serverType = driver.getResourceType(resName);
			if (serverType == null)
				missingResourceTypes.add(resName);
		}

		Assert.assertFalse(missingResourceTypes.contains(RESTYPE_SAMPLE));
//      Assert.assertFalse(missingResourceTypes.contains(RESTYPE_INFOBLOX_ZONE_AUTH));
//      Assert.assertFalse(missingResourceTypes.contains(RESTYPE_INFOBLOX_RECORD_HOST));
//      Assert.assertFalse(missingResourceTypes.contains(RESTYPE_INFOBLOX_NETWORK));

	}

	@Test
	public void testUpdatingDriverProperties() throws Exception {
		assertNotNull(driver);

		final Map<String, Object> oldProperties = driver.getProperties();
		assertTrue(oldProperties.containsKey("serverURL"));

		try
		{
			final Map<String, Object> newProperties = new HashMap<>();
			newProperties.put("serverURL", "http://www.google.com/21");
			driver.updateDriverProps(newProperties);

			final Map<String, Object> finalProperties = driver.getRep().getProperties();

			assertEquals(finalProperties, newProperties);
		}
		finally
		{
			// reset the driver properties to their original values
			driver.updateDriverProps(oldProperties);
		}

	}

	// // Walk through a list of device names and get the approvers and stakeholders
	@Test(enabled=false)
	public void testSampleGetApplicationResources() throws Exception {
		Map<String, String> devMap = new HashMap<String, String>();
		String[] apps = TestProperties.getProperty("resource.sample.apps").split(",");
//		for (String app : apps) {
//			String[] devs = TestProperties.getProperty("resource.sample.devices." + app).split(",");
//			for (String dev : devs) {
//				devMap.put(dev, app);
//			}
//		}
		// Process list of devices and
		ResourceRep[] reps = Resource.getResources(driver.getRep(), RESTYPE_SAMPLE,
				env.getContentType());
		for (ResourceRep rep : reps) {

//			String fqdn = (String) rep.getProperties().get("fqdn");
//			if (devMap.containsKey(fqdn)) {
//				String appId = devMap.get(fqdn);
//				// Remove the device from the list
//				devMap.remove(fqdn);
//				// Get the corresponding application
//				Map<String, String> des = Unsafe.<Map<String, String>>Cast(rep.getProperties().get("application"));
//				// get the information...
//				ResourceRep arep = Resource.getResource(des.get("url"), env.getContentType());
//				Assert.assertEquals(arep.getProperties().get("id"), Integer.parseInt(appId));
//				List<String> approvers = Unsafe.<List<String>>Cast(arep.getProperties().get("approvers"));
//				String approversString = TestProperties.getProperty("resource.itrc.approvers." + appId);
//				for (String approver : approvers) {
//					Assert.assertTrue(approversString.contains(approver));
//				}
//				List<String> stakeholders = Unsafe.<List<String>>Cast(arep.getProperties().get("stakeholders"));
//				String stakeholdersString = TestProperties.getProperty("resource.itrc.stakeholders." + appId);
//				for (String stakeholder : stakeholders) {
//					Assert.assertTrue(stakeholdersString.contains(stakeholder));
//				}
//
//			}
		}
		// verify that we found all the devices.
		Assert.assertTrue(devMap.size() == 0);

	}

//	@Test(groups = { "ITRC_UPDATE_RESOURCE" }, dependsOnGroups = {
//			"ITRC_GET_APPLICATION_RESOURCES" }, expectedExceptions = Exception.class)
//	public void testItrcResourceUpdateApplicationGroup() throws Exception {
//		ResourceRep[] reps = Resource.getResources(infobloxDriver.getRep(), RESTYPE_INFOBLOX_APPLICATIONGROUP,
//				env.getContentType());
//		for (ResourceRep rep : reps) {
//			Resource.updateResource(rep.getSelf(), rep.getProperties(), env.getContentType());
//		}
//	}
//
//	@Test(groups = { "ITRC_DELETE_RESOURCE" }, dependsOnGroups = {
//			"ITRC_UPDATE_RESOURCE" }, expectedExceptions = Exception.class)
//	public void testItrcResourceDeleteApplicationGroup() throws Exception {
//		ResourceRep[] reps = Resource.getResources(infobloxDriver.getRep(), RESTYPE_INFOBLOX_APPLICATIONGROUP,
//				env.getContentType());
//		for (ResourceRep rep : reps) {
//			Resource.deleteResource(env, rep.getSelf(), env.getContentType());
//		}
//	}
}
