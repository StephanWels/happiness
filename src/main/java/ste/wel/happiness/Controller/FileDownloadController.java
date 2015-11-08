package ste.wel.happiness.Controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ste.wel.happiness.CsvExportWriter;
import ste.wel.happiness.HappinessIndexInputFile;
import ste.wel.happiness.InputFileProvider;
import ste.wel.happiness.XlsxExportWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@Controller
public class FileDownloadController {
    private static final MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv", Charset.forName("utf-8"));

    private static final MediaType MEDIA_TYPE_XLSX = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Autowired
    XlsxExportWriter xlsxWriter;
    @Autowired
    CsvExportWriter csvExportWriter;
    @Autowired
    InputFileProvider inputFileProvider;

    @RequestMapping(value = {"/fileDownload"}, method = RequestMethod.GET)
    public void categories(final HttpServletResponse response) throws IOException, SolrServerException {

        if (inputFileProvider.getCurrentInputFile().get().getType().equals(HappinessIndexInputFile.Type.CSV)) {
            csvExportWriter.writeExportToCsv(response.getOutputStream(), inputFileProvider.getCurrentInputFile().get());
            response.addHeader("content-type", MEDIA_TYPE_CSV.toString());
            String fileName = inputFileProvider.getCurrentInputFile().get().getInputFileName().replace(".csv", "_Suggestions.csv").replace(" ", "_");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
        } else {
            response.addHeader("content-type", MEDIA_TYPE_XLSX.toString());
            String fileName = inputFileProvider.getCurrentInputFile().get().getInputFileName().replace(".xlsx", "_Suggestions.xlsx").replace(" ", "_");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            xlsxWriter.writeExportToXlsx(response.getOutputStream(), inputFileProvider.getCurrentInputFile().get());
        }
    }
}
