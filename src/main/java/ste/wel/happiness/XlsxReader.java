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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
public class XlsxReader {

    private static final Logger LOG = LoggerFactory.getLogger(XlsxReader.class);

    public HappinessIndexInputFile readInputFile(final InputStream is, final String originalFilename) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(is);


        List<String[]> content = new ArrayList<>();

        XSSFSheet sh = wb.getSheet("Daten Was ist gut");
        Iterator<Row> rowIterator = sh.rowIterator();
        //skip header
        rowIterator.next();
        rowIterator.forEachRemaining(row -> content.add(toStringArray(row, "good")));

//        sh = wb.getSheet("Daten Was verändern");
//        rowIterator = sh.rowIterator();
//        //skip header
//        rowIterator.next();
//        rowIterator.forEachRemaining(row -> content.add(toStringArray(row, "bad")));

        return new HappinessIndexInputFile(null, content, HappinessIndexInputFile.Type.XLSX, wb, originalFilename);
    }

    private String[] toStringArray(final Row row, String group) {
        String[] stringArray = new String[5];
        stringArray[0]= Optional.ofNullable(row.getCell(0)).map(Cell::toString).orElse("");
        stringArray[1]= Optional.ofNullable(row.getCell(1)).map(Cell::toString).orElse("");
        stringArray[2]= Optional.ofNullable(row.getCell(2)).map(Cell::toString).orElse("");
        stringArray[3]= Optional.ofNullable(row.getCell(3)).map(Cell::toString).orElse("");
        return stringArray;
    }
}
