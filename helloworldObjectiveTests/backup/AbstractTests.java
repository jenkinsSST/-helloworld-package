package com.seastreetinc.test.jira;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.seastreetinc.test.environment.ExecutionEnvironment.Driver;
import com.seastreetinc.test.environment.ExecutionEnvironment.Drivers;
import com.seastreetinc.test.resource.usecases.UseCaseTest;

/**
 * This will test the drivers functionality.
 */
public abstract class AbstractTests extends UseCaseTest {

	public static final String RD_SAMPLE = "rd.sample";

	public static final String RESTYPE_SAMPLE = "resource.sample";
	
//	public static final String RESTYPE_INFOBLOX_NETWORK = "resource.allocation.address";
//	public static final String RESTYPE_INFOBLOX_RECORD_HOST = "resource.allocation.dns";
//	public static final String RESTYPE_INFOBLOX_FIXED_ADDRESS = "resource.allocation.host";
//	public static final String RESTYPE_INFOBLOX_REGION = "resource.region";
//	public static final String RESTYPE_INFOBLOX_ZONE_AUTH = "resource.infoblox.zone_auth";

	private static final String usecase = "usecase";

	protected Driver driver = null;

	@Parameters({ "environment" })
	@BeforeClass
	@Override
	public void beforeClass(@Optional(usecase) String envName) throws Exception {
		super.beforeClass(envName);

		// Verify we have a infoblox driver

		Drivers drivers = env.getDrivers();
		Map<String, Driver> driverMap = drivers.getDrivers();
		Set<Entry<String, Driver>> set = driverMap.entrySet();

		for (Entry<String, Driver> next : set) {
			Driver driver = next.getValue();
			String driverType = driver.getType();

			if (driverType.contains(RD_SAMPLE)) {
				Assert.assertNull(driver, "This test only expects a single sample driver.");
				this.driver = driver;
			}
		}

		Assert.assertNotNull(driver, "This test expects a sample Driver to be setup through properties.");
	}

	@Override
	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
		String leftOvers = cleanupTrackedResources();
		super.afterClass();
		if (leftOvers != null && !leftOvers.isEmpty()) {
			Assert.assertNotNull(leftOvers,
					"Expected all resources to be deleted the following where not: " + leftOvers);
		}
	}
}
