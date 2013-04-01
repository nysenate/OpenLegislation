package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/*
 * Tests if bill data is equal to the data in the associated SOBI file.
 */
public class BillTests
{
	public static void isBillInitiallyNull(Storage storage, String billKey)
	{
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill, nullValue());
	}

	public static void doesBillExistsAfterProcessing(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill, notNullValue());
	}

	public static void isSponserNameCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String expectedSponsorName)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		String billSponsorName = bill.getSponsor().getFullname();
		assertThat(billSponsorName, equalToIgnoringCase(expectedSponsorName));
	}

	public static void areCoSponsorsCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String[] expectedCoSponsors)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		List<Person> billCoSponsors = bill.getCoSponsors();
		String[] coSponsorNames = new String[billCoSponsors.size()];
		int i = 0;
		for(Person person: billCoSponsors) {
			coSponsorNames[i] = person.getFullname();
			i++;
		}
		assertThat(coSponsorNames, is(expectedCoSponsors));
	}

	public static void areMultiSponsorsCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String[] expectedCoSponsors)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		List<Person> billMultiSponsors = bill.getMultiSponsors();
		String[] multiSponsorNames = new String[billMultiSponsors.size()];
		int i = 0;
		for(Person person: billMultiSponsors){
			multiSponsorNames[i] = person.getFullname();
			i++;
		}
		assertThat(multiSponsorNames, is(expectedCoSponsors));
	}

	public static void doesBillTextExist(Environment env, File sobiDirectory,
			Storage storage, String billKey, String billTextSobi)
	{
		File[] billTextFile = TestHelper.getFilesByName(sobiDirectory, billTextSobi);
		TestHelper.processFile(env, billTextFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill.getFulltext(), notNullValue());
	}

	public static void testBillTitle(Environment env, File sobiDirectory,
			Storage storage, String billKey, String shortTitleSobi, String expectedShortTitle)
	{
		File[] shortTitleFile = TestHelper.getFilesByName(sobiDirectory, shortTitleSobi);
		TestHelper.processFile(env, shortTitleFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill.getTitle(), is(expectedShortTitle));
	}

	public static void doesEntireBillDeleteWork(Environment env, File sobiDirectory,
			Storage storage, String billKey, String deleteStatusSobi, String...initialSobis)
	{
		File[] commitFile;
		List<String> sobiCommits = Arrays.asList(initialSobis);
		for(String commit: sobiCommits){
			commitFile = TestHelper.getFilesByName(sobiDirectory, commit);
			TestHelper.processFile(env, commitFile);
		}
		File[] deleteCommit = TestHelper.getFilesByName(sobiDirectory, deleteStatusSobi);
		TestHelper.processFile(env, deleteCommit);
		Bill deletedBill = TestHelper.getBill(storage, billKey);
		assertThat(deletedBill, nullValue());
	}

	public static void doesFullTextGetDeleted(Environment env, File sobiDirectory,
			Storage storage, String billKey, String billTextSobi, String deleteTextSobi)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, billTextSobi);
		TestHelper.processFile(env, initialCommit);
		File[] deleteTextCommit = TestHelper.getFilesByName(sobiDirectory, deleteTextSobi);
		TestHelper.processFile(env, deleteTextCommit);
		Bill deletedTextBill = TestHelper.getBill(storage, billKey);
		// After deleted, JSON stores empty values as "" instead of null.
		String expectedText = "";
		assertThat(deletedTextBill.getFulltext(), is(expectedText));
	}

	/*
	 * Will "00000 00000 0000" in the first line(Status Line) of SOBI will delete anything from the bill?
	 */
	public static void testIrregularBillStatusLines(Environment env, File sobiDirectory,
			Storage storage, String billKey, String nullStatusSobi, String...billSobis)
	{
		// First, process correct Sobi's and get the bill.
		File[] commitFile;
		List<String> sobiCommits = Arrays.asList(billSobis);
		for(String commit: sobiCommits){
			commitFile = TestHelper.getFilesByName(sobiDirectory, commit);
			TestHelper.processFile(env, commitFile);
		}
		Bill initialBill = TestHelper.getBill(storage, billKey);
		// Now process the incorrect status line sobi and get its bill.
		File[] emptyCommit = TestHelper.getFilesByName(sobiDirectory, nullStatusSobi);
		TestHelper.processFile(env, emptyCommit);
		Bill nullSponsorBill = TestHelper.getBill(storage, billKey);
		assertThat(nullSponsorBill.getSponsor().getFullname(), is(initialBill.getSponsor().getFullname()));
		// Test if anything else got changed.
		assertThat(initialBill.equals(nullSponsorBill), is(true)); // TODO do these both reference the same object?
	}

	public static void testLawSection(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String expectedLawSection)
	{
		File[] initialSobi = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialSobi);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill.getLawSection(), is(expectedLawSection));
	}

	/*
	 * Tests the Bill Status Actions found in SOBI line type/prefix 4.
	 */
	public static void testBillStatusActions(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, ArrayList<String[]> actionString, String billNumber)
	{
		List<Action> expectedActions = TestHelper.convertIntoActions(actionString, billNumber);
		File[] initialSobi = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialSobi);
		Bill bill = TestHelper.getBill(storage, billKey);
		List<Action> actions = bill.getActions();
		for(int i = 0; i < actions.size(); i++) {
			// Test status action text
			assertThat(actions.get(i).getText(), is(expectedActions.get(i).getText()));
			// Test status action date. Tests year, month, and day values since that is the accuracy provided by SOBI files.
			Date actionDate = DateUtils.round(actions.get(i).getDate(), Calendar.DAY_OF_MONTH);
			Date expectedDate = DateUtils.round(expectedActions.get(i).getDate(), Calendar.DAY_OF_MONTH);
			assertThat(actionDate, is(expectedDate));
		}
	}

}