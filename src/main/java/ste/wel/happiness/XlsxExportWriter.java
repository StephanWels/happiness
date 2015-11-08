package ste.wel.happiness;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

@Component
public class XlsxExportWriter {
    private static final int idxText = 2;
    private static final int idxTag = 3;
    private static final int idxSuggestion = 4;

    @Autowired
    @Qualifier("goodLearner")
    HappinessKeywordsLearner happinessKeywordsLearner;

    public void writeExportToXlsx(final OutputStream outputStream, HappinessIndexInputFile inputCsv) throws IOException, SolrServerException {
        XSSFWorkbook wb = inputCsv.getInputFile().get();
        XSSFSheet sh = wb.getSheet("Daten Was ist gut");

        Iterator<Row> rowIterator = sh.rowIterator();
        Row header = rowIterator.next();
        header.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellValue("Schlagwort Vorschlag");
        rowIterator.forEachRemaining(row -> suggestForRow(row));


//        sh = wb.getSheet("Daten Was verändern");
//        rowIterator = sh.rowIterator();
//        header = rowIterator.next();
//        header.getCell(4, Row.CREATE_NULL_AS_BLANK).setCellValue("Schlagwort Vorschlag");
//        rowIterator.forEachRemaining(row -> suggestForRow(row, "bad"));

        wb.write(outputStream);
        wb.close();
    }

    private void suggestForRow(final Row row) {
        if (row.getCell(idxTag, Row.RETURN_BLANK_AS_NULL) == null &&
                row.getCell(idxText, Row.RETURN_BLANK_AS_NULL) != null) {
            String text = row.getCell(idxText, Row.RETURN_BLANK_AS_NULL).toString();
            row.getCell(idxSuggestion, Row.CREATE_NULL_AS_BLANK)
                    .setCellValue(happinessKeywordsLearner.suggestTag(text));
        }
    }
}
