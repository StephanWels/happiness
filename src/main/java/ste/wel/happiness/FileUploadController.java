package ste.wel.happiness;

import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Controller
public class FileUploadController {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);
    public static final String PATH = "main";

    @Autowired
    @Qualifier("goodLearner")
    HappinessKeywordsLearner goodLearner;

    @Autowired
    @Qualifier("badLearner")
    HappinessKeywordsLearner badLearner;

    @Autowired
    InputFileProvider inputFileProvider;

    @Autowired
    XlsxReader xlsxReader;

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public ModelAndView importParse(@RequestParam("myFile") MultipartFile myFile) throws Exception {
        final ModelAndView modelAndView = new ModelAndView(PATH);
        if (myFile.getOriginalFilename().endsWith(".csv")) {
            uploadCsv(myFile);
            final HappinessIndexInputFile happinessIndexInputFile = inputFileProvider.getCurrentInputFile().get();
            modelAndView.addObject("csvPresent", true);
            modelAndView.addObject("success", statusMessage(happinessIndexInputFile));
            return modelAndView;
        } else if (myFile.getOriginalFilename().endsWith(".xlsx")) {
            uploadXlsx(myFile);
            final HappinessIndexInputFile happinessIndexInputFile = inputFileProvider.getCurrentInputFile().get();
            modelAndView.addObject("csvPresent", true);
            modelAndView.addObject("success", statusMessage(happinessIndexInputFile));
            return modelAndView;
        } else if (myFile.getOriginalFilename().isEmpty()) {
            modelAndView.addObject("csvPresent", inputFileProvider.getCurrentInputFile().isPresent());
            modelAndView.addObject("error", "Please select a file first.");
            return modelAndView;
        }
        modelAndView.addObject("error", "Unsupported file type.");
        return modelAndView;
    }

    private String statusMessage(final HappinessIndexInputFile happinessIndexInputFile){
        final int taggedGood = happinessIndexInputFile.getTaggedComments(Sheet.GOOD).size();
        final int taggedBad = happinessIndexInputFile.getTaggedComments(Sheet.BAD).size();
        final int untaggedGood = happinessIndexInputFile.getUntaggedComments(Sheet.GOOD).size();
        final int untaggedBad = happinessIndexInputFile.getUntaggedComments(Sheet.BAD).size();
        final int tagged = taggedBad + taggedGood;
        final int untagged = untaggedBad + untaggedGood;
        return happinessIndexInputFile.getType() + " File uploaded. " + tagged + " tagged comments, " + untagged + " untagged comments.";
    }

    private void uploadXlsx(final MultipartFile myFile) throws Exception {
        HappinessIndexInputFile inputXlsx = xlsxReader.readInputFile(myFile.getInputStream(), myFile.getOriginalFilename());
        inputFileProvider.setCurrentInputFile(inputXlsx);
        goodLearner.trainOnData(inputXlsx.getTaggedComments(Sheet.GOOD));
        badLearner.trainOnData(inputXlsx.getTaggedComments(Sheet.BAD));
    }

    private void uploadCsv(final MultipartFile myFile) throws Exception {
        HappinessIndexInputFile inputCsv = readData(myFile.getInputStream(), myFile.getOriginalFilename());
        inputFileProvider.setCurrentInputFile(inputCsv);
        goodLearner.trainOnData(inputCsv.getTaggedComments(Sheet.GOOD));
    }


    private HappinessIndexInputFile readData(InputStream inputStream, String fileName) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        CSVReader csvReader = new CSVReader(reader, ',');
        String[] header = csvReader.readNext();
        final List<String[]> allValues = csvReader.readAll();
        reader.close();
        return new HappinessIndexInputFile(header, Collections.singletonMap(Sheet.GOOD, allValues), HappinessIndexInputFile.Type.CSV, null, fileName);
    }
}
