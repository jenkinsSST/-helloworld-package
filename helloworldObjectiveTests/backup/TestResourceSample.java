package com.seastreetinc.test.jira;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.seastreetinc.test.reps.resource.items.ResourceRep;

public class TestResourceSample extends TestSampleResources {

	@Override
	protected String getResourceTypeName() {
		return RESTYPE_SAMPLE;
	}

	@Override
	protected Collection<Entry<String, Map<String, Object>>> getCreationProperties() {
		final Map<String, Map<String, Object>> res = new HashMap<>();

		final Random random = new Random();

		final Map<String, Object> props1 = new HashMap<String, Object>();
//		props1.put("subnet", String.format("192.168.%d.0", 1 + random.nextInt(255)));
//		props1.put("cidr", "24");

		res.put(RESTYPE_SAMPLE, props1);

		final Map<String, Object> props2 = new HashMap<String, Object>();
//		props2.put("pool", String.format("192.168.0.0/16", random.nextInt(256)));
//		props2.put("cidr", "24");

		res.put(RESTYPE_SAMPLE, props2);

		return res.entrySet();
	}

	@Override
	protected void validate(ResourceRep rep) {
		final Map<String, Object> properties = rep.getProperties();

//		Assert.assertTrue(properties.containsKey("cidr"));
//		Assert.assertNotNull(properties.get("cidr"));
//		Assert.assertTrue(properties.containsKey("subnet"));
//		Assert.assertNotNull(properties.get("subnet"));
//		Assert.assertTrue(properties.containsKey("pool"));
//		Assert.assertNotNull(properties.get("pool"));
	}

	@Override
	protected boolean isUpdateSupported() {
		return false;
	}
}
