package gov.nysenate.openleg.api.servlets;

import javax.servlet.http.HttpServletRequest;

import gov.nysenate.openleg.model.SenateObject;

/**
 * Any SenateObject that requires a view but doesn't require any related objects
 * can be routed through this servlet
 */
@SuppressWarnings("serial")
public class SenateObjectServlet extends AbstractSenateObjectServlet<SenateObject> {
	public SenateObjectServlet() {
		super();
	}
	
	@Override
	protected void doRelated(String oid, HttpServletRequest request) {
		return;
	}
}
