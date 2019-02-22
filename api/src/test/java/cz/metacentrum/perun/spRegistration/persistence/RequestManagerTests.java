package cz.metacentrum.perun.spRegistration.persistence;

import cz.metacentrum.perun.spRegistration.Application;
import cz.metacentrum.perun.spRegistration.persistence.configs.Config;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.managers.impl.RequestManagerImpl;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestApproval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RequestManagerTests {

	@Autowired
	private RequestManagerImpl requestManager;

	@Autowired
	private Config config;

	private PerunAttributeDefinition def1;
	private PerunAttributeDefinition def2;
	private PerunAttribute attr1;
	private PerunAttribute attr2;
	private Request req1;
	private Request req2;
	private RequestApproval approval1;
	private RequestApproval approval2;

	@Before
	public void setUp() {
		def1 = config.getAppConfig().getAttrDefinition("urn:perun:facility:attribute-def:def:registrationURL");
		def2 = config.getAppConfig().getAttrDefinition("urn:perun:facility:attribute-def:def:dynamicRegistration");

		prepareAttributes();
		prepareRequests();
		prepareApprovals();

		Long req1Id = requestManager.createRequest(req1);
		req1.setReqId(req1Id);

		Long req2Id = requestManager.createRequest(req2);
		req2.setReqId(req2Id);

		requestManager.addSignature(approval1.getRequestId(), approval1.getSignerId(),
				approval1.getSignerName(), approval1.getSignerInput());
		List<RequestApproval> approvals = requestManager.getApprovalsForRequest(approval1.getRequestId());
		approval1.setSignedAt(approvals.get(0).getSignedAt());
	}

	private void prepareApprovals() {
		approval1 = new RequestApproval();
		approval1.setSignerId(1L);
		approval1.setRequestId(req1.getReqId());
		approval1.setSignerName("test_user");
		approval1.setSignerInput("test_approval");

		approval2 = new RequestApproval();
		approval2.setSignerId(2L);
		approval2.setRequestId(req1.getReqId());
		approval2.setSignerName("test_user2");
		approval2.setSignerInput("test_approval2");
	}

	private void prepareAttributes() {
		attr1 = new PerunAttribute(); // STRING ATTRIBUTE
		attr1.setDefinition(def1);
		attr1.setComment("test comment");
		attr1.setOldValue("old val");
		attr1.setValue("new val");
		attr1.setFullName(def1.getFullName());

		attr2 = new PerunAttribute(); // BOOLEAN ATTRIBUTE
		attr2.setDefinition(def2);
		attr2.setComment("test comment");
		attr2.setOldValue(false);
		attr2.setValue(true);
		attr2.setFullName(def2.getFullName());
	}

	private void prepareRequests() {
		req1 = new Request();
		req1.setAction(RequestAction.REGISTER_NEW_SP);
		req1.setStatus(RequestStatus.NEW);
		req1.setFacilityId(null);
		req1.setModifiedBy(1L);
		req1.setAttributes(Collections.singletonMap(attr1.getFullName(), attr1));
		req1.setReqUserId(1L);

		req2 = new Request();
		req2.setAction(RequestAction.UPDATE_FACILITY);
		req2.setStatus(RequestStatus.WFA);
		req2.setFacilityId(2L);
		req2.setModifiedBy(2L);
		req2.setAttributes(Collections.singletonMap(attr2.getFullName(), attr2));
		req2.setReqUserId(2L);
	}

	@Test
	public void createRequest() {
		Request request = new Request();
		request.setAction(RequestAction.REGISTER_NEW_SP);
		request.setStatus(RequestStatus.NEW);
		request.setFacilityId(null);
		request.setModifiedBy(3L);
		request.setReqUserId(3L);

		Long requestId = requestManager.createRequest(request);
		assertNotNull("Creating request shoudl generate an ID", requestId);
		request.setReqId(requestId);

		Request fetched = requestManager.getRequestByReqId(requestId);
		assertNotNull("Created request could not be fetched", fetched);
		assertEquals("Stored request is not the same", request, fetched);
	}

	@Test
	public void updateRequest() {
		req1.setAction(RequestAction.DELETE_FACILITY);
		req1.setStatus(RequestStatus.APPROVED);
		req1.setFacilityId(3L);

		boolean result = requestManager.updateRequest(req1);
		assertTrue("Updating shold return true", result);

		Request fetched = requestManager.getRequestByReqId(req1.getReqId());
		assertNotNull("Updated request has not been fetched", fetched);
		assertEquals("Updated request is different", req1, fetched);
	}

	@Test
	public void deleteRequest() {
		requestManager.deleteRequest(req1.getReqId());
		List<Request> inDb = requestManager.getAllRequests();

		assertNotNull("No results fetched", inDb);
		assertEquals("Only one request should be found", 1, inDb.size());
		assertFalse("Request should not be in DB", inDb.contains(req1));
	}

	@Test
	public void getRequestById() {
		Request fetched = requestManager.getRequestByReqId(req1.getReqId());
		assertEquals("Fetched request is different than expected", req1, fetched);
	}

	@Test
	public void getAllRequests() {
		List<Request> allRequests = requestManager.getAllRequests();

		assertNotNull("No result has been fetched", allRequests);
		assertEquals("Result size should be 2", 2, allRequests.size());
		assertThat("Result does not contain expected items", allRequests, hasItems(req1, req2));
	}

	@Test
	public void getRequestsByUserId() {
		List<Request> fetched = requestManager.getAllRequestsByUserId(req1.getReqUserId());

		assertNotNull("Should find one request but null collection returned", fetched);
		assertTrue("Result set should contain at least one request", fetched.size() > 0);
		assertEquals("Result set should contain exactly one request", 1, fetched.size());
		assertEquals("Request should be equal", req1, fetched.get(0));
	}

	@Test
	public void getAllRequestsByStatus() {
		List<Request> fetched = requestManager.getAllRequestsByStatus(req1.getStatus());

		assertNotNull("Should find one request but null collection returned", fetched);
		assertTrue("Result set should contain at least one request", fetched.size() > 0);
		assertEquals("Result set should contain exactly one request", 1, fetched.size());
		assertEquals("Request should be equal", req1, fetched.get(1));
	}

	@Test
	public void getAllRequestsByAction() {
		List<Request> fetched = requestManager.getAllRequestsByAction(req1.getAction());

		assertNotNull("Should find one request but null collection returned", fetched);
		assertTrue("Result set should contain at least one request", fetched.size() > 0);
		assertEquals("Result set should contain exactly one request", 1, fetched.size());
		assertEquals("Request should be equal", req1, fetched.get(1));
	}

	@Test
	public void getRequestsByFacilityId() {
		List<Request> fetched = requestManager.getAllRequestsByFacilityId(req1.getFacilityId());

		assertNotNull("Should find one request but null collection returned", fetched);
		assertTrue("Result set should contain at least one request", fetched.size() > 0);
		assertEquals("Result set should contain exactly one request", 1, fetched.size());
		assertEquals("Request should be equal", req1, fetched.get(1));

	}

	@Test
	public void getAllRequestsByFacilityIds() {
		Set<Long> ids = new HashSet<>();
		ids.add(req1.getFacilityId());
		ids.add(req2.getFacilityId());
		List<Request> fetched = requestManager.getAllRequestsByFacilityIds(ids);

		assertNotNull("Should find one request but null collection returned", fetched);
		assertTrue("Result set should contain at least one request", fetched.size() > 0);
		assertEquals("Result set should contain exactly two requests", 2, fetched.size());
		assertThat("Result does not contain expected items", fetched, hasItems(req1, req2));
	}

	@Test
	public void addSignature() {
		RequestApproval newApproval = new RequestApproval();
		newApproval.setRequestId(1L);
		newApproval.setSignerId(3L);
		newApproval.setSignerName("test_user_new");
		newApproval.setSignerInput("test_input_new");
		newApproval.setSignedAt(Timestamp.valueOf("2019-01-01 10:00:00.00"));

		boolean result = requestManager.addSignature(newApproval.getRequestId(), newApproval.getSignerId(),
				newApproval.getSignerName(), newApproval.getSignerInput(), newApproval.getSignedAt());
		assertTrue("Addition of signature should return true", result);

		List<RequestApproval> approvals = requestManager.getApprovalsForRequest(req1.getReqId());
		assertNotNull("Should find one approval but null collection returned", approvals);
		assertTrue("Result set should contain at least one approval", approvals.size() > 0);
		assertEquals("Result set should contain exactly three approvals", 3, approvals.size());
		assertThat("Result does not contain expected item", approvals, hasItems(approval1, approval2, newApproval));
	}

	@Test
	public void getApprovalsForRequest() {
		List<RequestApproval> approvals = requestManager.getApprovalsForRequest(req1.getReqId());

		assertNotNull("Should find one approval but null collection returned", approvals);
		assertTrue("Result set should contain at least one approval", approvals.size() > 0);
		assertEquals("Result set should contain exactly two approvals", 2, approvals.size());
		assertThat("Result does not contain expected item", approvals, hasItems(approval1, approval2));
	}

}
