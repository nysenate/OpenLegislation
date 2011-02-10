<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/xml" pageEncoding="utf-8"%><%


Agenda agenda = (Agenda)request.getAttribute("agenda");

JAXBContext context = JAXBContext.newInstance(Agenda.class);

Marshaller m = context.createMarshaller();
m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
m.marshal(agenda, response.getOutputStream());

 %>