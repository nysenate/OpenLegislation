<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/xml" pageEncoding="utf-8"%><%



Committee committee = (Committee)request.getAttribute("meetings");

JAXBContext context = JAXBContext.newInstance(Committee.class);

Marshaller m = context.createMarshaller();
m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
m.marshal(committee, response.getOutputStream());

 %>