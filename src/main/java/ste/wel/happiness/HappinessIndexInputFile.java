package ste.wel.happiness;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.stream.Collectors;

public class HappinessIndexInputFile {

    public enum Type {
        CSV,
        XLSX
    }

    private static final int idxTag = 3;

    private String[] header;
    private Map<Sheet, List<String[]>> content;
    private Type type;
    private XSSFWorkbook inputFile;
    private String inputFileName;

    public String getInputFileName() {
        return inputFileName;
    }

    public Optional<XSSFWorkbook> getInputFile() {
        return Optional.ofNullable(inputFile);
    }

    public HappinessIndexInputFile(final String[] header, final Map<Sheet, List<String[]>> content, final Type type, final XSSFWorkbook inputFile,
                                   final String inputFileName) {
        this.header = header;
        this.content = content;
        this.type = type;
        this.inputFile = inputFile;
        this.inputFileName = inputFileName;
    }

    public Type getType() {
        return type;
    }

    public String[] getHeader() {
        return header;
    }

    public List<String[]> getContent(Sheet sheet) {
        return content.getOrDefault(sheet, Collections.emptyList());
    }

    public List<Comment> getTaggedComments(Sheet sheet) {
        return content.get(sheet).stream()
                .filter(values -> values.length > 3)
                .filter(values -> values[idxTag].trim().length() > 0)
                .map(values -> new Comment(values[2], Arrays.asList(values[idxTag].split("\\s*,\\s*"))))
                .collect(Collectors.toList());
    }

    public List<Comment> getUntaggedComments(Sheet sheet) {
        return content.get(sheet).stream()
                .filter(values -> values.length > 3)
                .filter(values -> values[idxTag].trim().length() == 0)
                .map(values -> new Comment(values[2], Arrays.asList(values[idxTag].split("\\s*,\\s*"))))
                .collect(Collectors.toList());
    }
}
