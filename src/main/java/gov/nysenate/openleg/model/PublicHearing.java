package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.ArrayList;

public class PublicHearing extends SenateObject {
	public ArrayList<String> committees;
	public String title;
	public String location;
	public Date timeStamp;
	
	public ArrayList<Person> presidingSenators;
	public ArrayList<Person> presentSenators;
	public ArrayList<Person> presentAssemblyPersons;
	public ArrayList<Person> speakers;	
	
	public PublicHearing() {
		committees = new ArrayList<String>();
		presidingSenators = new ArrayList<Person>();
		presentSenators = new ArrayList<Person>();
		presentAssemblyPersons = new ArrayList<Person>();
		speakers = new ArrayList<Person>();
	}
	
	public void addPerson(Person person, ArrayList<Person> persons) {
		persons.add(person);
	}
	
	public static class Person {
		String name;
		String title;
		String committee;
		String organization;
		
		Integer page;
		Integer questions;
		
		public Person() {
			
		}
		
		public Person(String name, String title, String committee, String organization) {
			this.name = name;
			this.title = title;
			this.committee = committee;
			this.organization = organization;
		}

		public String getName() {
			return name;
		}

		public String getTitle() {
			return title;
		}

		public String getCommittee() {
			return committee;
		}

		public String getOrganization() {
			return organization;
		}

		public Integer getPage() {
			return page;
		}

		public Integer getQuestions() {
			return questions;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setCommittee(String committee) {
			this.committee = committee;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public void setQuestions(int questions) {
			this.questions = questions;
		}
	}
}
