package ste.wel.happiness;

import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Legacy {

    private static final Logger LOG = LoggerFactory.getLogger(Legacy.class);
    private static final String PATH = "main";

    static String inputPath = "/Users/stephanwels1/git_repositories/happinessreco/gut_input.csv";
    static String outputPath = "/Users/stephanwels1/git_repositories/happinessreco/gut_output.csv";


//    public void trainOnData(HappinessIndexInputFile inputCsv) throws Exception {
//        final BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outputPath), StandardOpenOption.CREATE);
//        final CSVWriter csvWriter = new CSVWriter(bufferedWriter, ',');
//
//        HappinessKeywordsLearner solr = new HappinessKeywordsLearner();
//
//        List<String[]> allData = readData();
//
//        double counterCorrect = 0;
//        double counterWrong = 0;
//        Set<String> correctPredictions = new HashSet<>();
//        Set<String> wrongPredictions = new HashSet<>();
//        Set<String> allTags = new HashSet<>();
//        for (int i = 0; i < allData.size(); i++) {
//            List<String[]> trainingData = extractTrainingSet(allData, i);
//            String[] testData = extractTestData(allData, i);
//            solr.trainOnData(trainingData);
//
//            final String suggestedTag = solr.suggestTag(testData);
//            final String expectedTags = testData[3];
//            allTags.addAll(Arrays.stream(expectedTags.split(",")).map(String::trim).collect(Collectors.toSet()));
//            boolean correct = expectedTags.contains(suggestedTag) && suggestedTag != "";
////            System.out.println("Expected Tag: '" + expectedTags + "' suggested Tag: '" + suggestedTag + "'");
//            if (correct) {
//                counterCorrect++;
//                correctPredictions.add(suggestedTag);
//            } else {
////                System.err.println("suggested: " + suggestedTag + " expected: " + expectedTags);
//                wrongPredictions.add(suggestedTag);
//                counterWrong++;
//            }
////            System.out.println();
//            final List<String> inputLine = Arrays.stream(allData.get(i)).collect(Collectors.toList());
//            inputLine.add(suggestedTag);
//            inputLine.add(String.valueOf(correct));
//
//            csvWriter.writeNext(inputLine.toArray(new String[inputLine.size()]));
//        }
//        double precision = counterCorrect / (counterCorrect + counterWrong);
//        System.out.println("Precision: " + precision);
//        System.out.println("Correct predictions: " + correctPredictions);
//        System.out.println("Failed predictions: " + wrongPredictions);
//        System.out.println("All tags: " + allTags);
//
//        csvWriter.flush();
//        csvWriter.close();
//    }

    private static List<String[]> extractTrainingSet(final List<String[]> allData, final int testIndex) {
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            if (i != testIndex) {
                result.add(allData.get(i));
            }
        }
        return result;
    }

    private static String[] extractTestData(final List<String[]> allData, final int i) {
        return allData.get(i);
    }

    public static List<String[]> readData() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath));
        reader.readLine();

        CSVReader csvReader = new CSVReader(reader, ',');

        final List<String[]> allValues = csvReader.readAll();

        reader.close();
        return allValues.stream().filter(values -> values[3].trim().length() > 0).collect(Collectors.toList());
    }
}
