package gov.nysenate.openleg.api.servlets;

import javax.servlet.http.HttpServletRequest;

import gov.nysenate.openleg.model.SenateObject;

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
