package ste.wel.happiness;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class XlsxReader {

    private static final Logger LOG = LoggerFactory.getLogger(XlsxReader.class);

    public HappinessIndexInputFile readInputFile(final InputStream is, final String originalFilename) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(is);

        Map<Sheet, List<String[]>> inputLines = new HashMap<>();
        inputLines.put(Sheet.GOOD, readSheetContent(wb, "Daten Was ist gut"));
        inputLines.put(Sheet.BAD, readSheetContent(wb, "Daten Was verändern"));
        return new HappinessIndexInputFile(null, inputLines, HappinessIndexInputFile.Type.XLSX, wb, originalFilename);
    }

    private List<String[]> readSheetContent(XSSFWorkbook wb, String sheetName) {
        List<String[]> content = new ArrayList<>();

        XSSFSheet sh = wb.getSheet(sheetName);
        Iterator<Row> rowIterator = sh.rowIterator();
        //skip header
        rowIterator.next();
        rowIterator.forEachRemaining(row -> content.add(toStringArray(row)));
        return content;
    }

    private String[] toStringArray(final Row row) {
        String[] stringArray = new String[4];
        stringArray[0] = Optional.ofNullable(row.getCell(0)).map(Cell::toString).orElse("");
        stringArray[1] = Optional.ofNullable(row.getCell(1)).map(Cell::toString).orElse("");
        stringArray[2] = Optional.ofNullable(row.getCell(2)).map(Cell::toString).orElse("");
        stringArray[3] = Optional.ofNullable(row.getCell(3)).map(Cell::toString).orElse("");
        return stringArray;
    }
}
