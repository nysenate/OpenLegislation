package gov.nysenate.openleg.lucene;

import java.util.Collection;

import org.apache.lucene.document.Fieldable;

public interface ILuceneObject
{
    /**
     * @return a unique identifier for the document that representing this object.
     */
	public String luceneOid();

    /**
     * @return the type of the document representing this object, e.g. bill, calendar, transcript
     */
    public String luceneOtype();

	/**
     * @return the string to be searched when no field is specified
     */
	public String luceneOsearch();

	/**
     * @return the summary of the document contents
     */
	public String luceneSummary();

	/**
     * @return the title of the document
     */
	public String luceneTitle();

	/**
     * @return a collection of custom fields to add to the document
     */
	public Collection<Fieldable> luceneFields();

    /**
     * Sets the active status of the object.
     */
	public void setActive(boolean active);

	/**
     * @return the active status of the object.
     */
	public boolean isActive();

	/**
     * Sets the time the object was last modified (milliseconds since epoch).
     */
	public void setModified(long modified);

	/**
     * @return the time the object was last modified (milliseconds since epoch).
     */
	public long getModified();
}