package gov.nysenate.openleg.lucene;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;

public class OpenLegislationQueryParser extends StandardQueryParser
{
    public OpenLegislationQueryParser(Analyzer analyzer)
    {
        super(analyzer);

        // All fields indexed numerically need to be listed here in order to properly search on them.
        Map<String, NumericConfig> numericConfigMap = new HashMap<String, NumericConfig>();
        numericConfigMap.put("year", new NumericConfig(8, NumberFormat.getInstance(), FieldType.NumericType.INT));
        numericConfigMap.put("modified", new NumericConfig(8, NumberFormat.getInstance(), FieldType.NumericType.LONG));
        numericConfigMap.put("published", new NumericConfig(8, NumberFormat.getInstance(), FieldType.NumericType.LONG));
        numericConfigMap.put("when", new NumericConfig(8, NumberFormat.getInstance(), FieldType.NumericType.LONG));
        this.setNumericConfigMap(numericConfigMap);
    }
}
