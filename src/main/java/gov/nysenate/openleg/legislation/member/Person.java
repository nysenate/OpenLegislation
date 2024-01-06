package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.common.util.RegexUtils;
import org.apache.commons.lang3.StringUtils;

public record Person(Integer personId, PersonName name, String email, String imgName)
        implements Comparable<Person> {
    public Person(Integer personId, PersonName name, String email, String imgName) {
        this.personId = personId;
        this.name = name;
        this.email = email;
        this.imgName = StringUtils.isBlank(imgName) ? "no_image.jpg" : imgName;
    }
    /**
     * A consistent naming convention for image names.
     * This should be used when naming the image for all new legislators.
     * For newer images, this will likely be the same as <code>getImageName</code>, but it may
     * not be the same for older images which had a different naming conventions.
     */
    public String getSuggestedImageFileName() {
        String temp = personId + "_" + name.firstName() + "_" + name.lastName() + ".jpg";
        return RegexUtils.removeAccentedCharacters(temp);
    }

    @Override
    public int compareTo(Person o) {
        return ComparisonChain.start().compare(this.name, o.name)
                .compare(this.personId, o.personId).result();
    }
}
