package ste.wel.happiness;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HappinessIndexInputFile {

    enum Type {
        CSV,
        XLSX
    }

    private static final int idxTag = 3;

    private String[] header;
    private List<String[]> content;
    private Type type;
    private XSSFWorkbook inputFile;
    private String inputFileName;

    public String getInputFileName() {
        return inputFileName;
    }

    public Optional<XSSFWorkbook> getInputFile() {
        return Optional.ofNullable(inputFile);
    }

    public HappinessIndexInputFile(final String[] header, final List<String[]> content, final Type type, final XSSFWorkbook inputFile,
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

    public List<String[]> getContent() {
        return content;
    }

    public List<Comment> getTaggedComments() {
        return content.stream()
                .filter(values -> values.length > 3)
                .filter(values -> values[idxTag].trim().length() > 0)
                .map(values -> new Comment(values[2], Arrays.asList(values[idxTag].split("\\s*,\\s*")), values[4]))
                .collect(Collectors.toList());
    }

    public int getNoTaggedComments() {
        return getTaggedComments().size();
    }

    public int getSize() {
        return content.size();
    }

    public List<Comment> getUntaggedComments() {
        return content.stream()
                .filter(values -> values.length > 3)
                .filter(values -> values[idxTag].trim().length() == 0)
                .map(values -> new Comment(values[2], Arrays.asList(values[idxTag].split("\\s*,\\s*")), values[4]))
                .collect(Collectors.toList());
    }

    public int getNoUntaggedComments() {
        return getUntaggedComments().size();
    }
}
