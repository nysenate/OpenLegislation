package gov.nysenate.openleg.api.legislation.law.view;

import org.elasticsearch.common.collect.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.LAW_CHAR_BLOCK_PATTERN;

/**
 * Data class for information on a block of characters.
 */
public class LawCharBlock {
    public final static LawCharBlock EMPTY = new LawCharBlock("", null);
    private final Tuple<String, LawCharBlockType> info;

    public LawCharBlock(String match, LawCharBlockType type) {
        this.info = new Tuple<>(match, type);
    }

    public String text() {
        return info.v1();
    }

    public LawCharBlockType type() {
        return info.v2();
    }

    /**
     * Converts text into the correct LawCharBlocks.
     * @param text to process.
     * @return the blocks.
     */
    public static List<LawCharBlock> getBlocksFromText(String text) {
        List<LawCharBlock> ret = new ArrayList<>();
        Matcher m = LAW_CHAR_BLOCK_PATTERN.matcher(text);
        while (m.find()) {
            // Finds the type associated with the matched block of text.
            Optional<LawCharBlockType> type = Arrays.stream(LawCharBlockType.values())
                    .filter(t -> m.group(t.name()) != null).findFirst();
            if (!type.isPresent())
                continue;
            ret.add(new LawCharBlock(m.group(), type.get()));
        }
        // Some extra lines for spacing.
        ret.add(new LawCharBlock("\n", LawCharBlockType.NEWLINE));
        ret.add(new LawCharBlock("\n", LawCharBlockType.NEWLINE));
        return ret;
    }
}
