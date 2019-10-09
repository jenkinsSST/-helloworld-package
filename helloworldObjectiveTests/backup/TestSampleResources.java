package com.seastreetinc.test.jira;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seastreetinc.gglib.exceptions.RestResponseException;
import com.seastreetinc.test.reps.resource.items.ResourceRep;
import com.seastreetinc.test.utils.resource.Resource;

/**
 * Create, Update, GET and DELETE resources to make sure things function as
 * expected.
 */
public abstract class TestSampleResources extends AbstractTests {

	protected abstract String getResourceTypeName();

	protected abstract Collection<Entry<String, Map<String, Object>>> getCreationProperties();

	protected abstract void validate(ResourceRep rep);

	protected abstract boolean isUpdateSupported();

	protected Map<String, Object> updateProperties(Map<String, Object> properties) {
		return properties;
	}

	@Test
	public void testResource() throws Exception {

		final Deque<Resource> createdResources = new LinkedList<>();

		try {
			for (Entry<String, Map<String, Object>> entry : getCreationProperties()) {

				ResourceRep rep = Resource.createResource(getEnvironment(), driver.getRep(), entry.getKey(),
						entry.getValue(), MediaType.APPLICATION_JSON.toString());

				Assert.assertNotNull(rep, "Unable to create" + entry.getKey() + " resource.");
				Assert.assertNotNull(rep.getKey());

				createdResources.push(Resource.from(this.getEnvironment(), rep));
			}

			// get all resources
			for (ResourceRep rep : Resource.getResources(driver.getRep(), getResourceTypeName(), env.getContentType())) {
				validate(rep);
			}

			// get created resources
			for (Resource expected : createdResources) {

				final ResourceRep actual = Resource.getResource(expected.getSelf(), getResourceTypeName());
				Assert.assertEquals(expected.getSelf(), actual.getSelf());
			}

			if (isUpdateSupported()) {
				for (Resource resource : getCreatedResource(getResourceTypeName())) {

					final Map<String, Object> properties = resource.getRep().getProperties();
					resource.update(updateProperties(properties));
				}
			}
		} finally {

			final List<Exception> failedDeletes = new ArrayList<Exception>();

			while (!createdResources.isEmpty()) {
				final Resource resource = createdResources.pop();
				try {
					resource.delete();
				} catch (RestResponseException e) {
					if (e.getCode() != 404)
						failedDeletes.add(e);
				} catch (Exception e) {
					failedDeletes.add(e);
				}
			}

			if (!failedDeletes.isEmpty()) {
				final AssertionError assertionError = new AssertionError(
						"Not all resources deleted. You MUST delete them by hand or the next test run might fail!");

				failedDeletes.stream().forEach(assertionError::addSuppressed);

				throw assertionError;
			}
		}
	}
}
