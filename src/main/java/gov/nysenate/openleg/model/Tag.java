package gov.nysenate.openleg.model;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PersistenceCapable
@XStreamAlias("tag")
public class Tag   extends SenateObject {

	@Persistent 
	@PrimaryKey
	private String id;
	
	@Persistent 
	@Column(name="tag_text")
	private String tagText;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the tagText
	 */
	public String getTagText() {
		return tagText;
	}

	/**
	 * @param tagText the tagText to set
	 */
	public void setTagText(String tagText) {
		this.tagText = tagText;
	}
	
}
