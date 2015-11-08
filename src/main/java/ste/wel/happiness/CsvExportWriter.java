package ste.wel.happiness;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class CsvExportWriter {

    private static final int idxId = 1;
    private static final int idxText = 2;
    private static final int idxTag = 3;

    @Autowired
    @Qualifier("goodLearner")
    HappinessKeywordsLearner happinessKeywordsLearner;

    public void writeExportToCsv(final OutputStream outputStream, HappinessIndexInputFile inputCsv) throws IOException, SolrServerException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), ',');
        writer.writeNext(augmentHeader(inputCsv.getHeader()));

        for (String[] line : inputCsv.getContent(Sheet.GOOD)) {
            String[] outputLine = Arrays.copyOf(line, line.length + 1);
            String text = line[idxText];
            boolean needsSuggestion = line[idxTag].trim().isEmpty();
            if (needsSuggestion) {
                String tagSuggestion = happinessKeywordsLearner.suggestTag(text);
                outputLine[outputLine.length - 1] = tagSuggestion;
            } else {
                outputLine[outputLine.length - 1] = StringUtils.EMPTY;
            }
            writer.writeNext(outputLine);
        }

        writer.close();
    }

    private String[] augmentHeader(String[] header) {
        String[] newHeader = Arrays.copyOf(header, header.length + 1);
        newHeader[newHeader.length - 1] = "Tag suggestion";
        return newHeader;
    }

}
