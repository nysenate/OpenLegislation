package gov.nysenate.openleg.spotchecks.keymapper;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.spotchecks.model.SpotCheckContentType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SpotCheckDaoKeyMapperIT extends BaseTests {

    @Autowired private List<SpotCheckDaoKeyMapper> keyMappers;

    @Test
    public void contentTypeCoverageTest() {
        Set<Class> keyMapperClasses = keyMappers.stream()
                .map(SpotCheckDaoKeyMapper::getKeyClass)
                .collect(Collectors.toSet());
        Set<Class> contentTypeKeyClasses = Arrays.stream(SpotCheckContentType.values())
                .map(SpotCheckContentType::getContentKeyClass)
                .collect(Collectors.toSet());

        assertEquals("Every content type class should have a corresponding key mapper",
                contentTypeKeyClasses, keyMapperClasses);
    }
}
