package database.util;

import logger.MyLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class NotesCVSParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public static Map<YearMonth, String> readNotesFromCSV(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            return reader.lines()
                    .map(line -> line.split(",", 2))
                    .collect(Collectors.toMap(
                            arr -> YearMonth.parse(arr[0], FORMATTER),
                            arr -> arr.length > 1 ? arr[1] : ""
                    ));
        } catch (IOException e) {
            MyLogger.error("Error while reading CSV note file");
            throw new IOException(e.getMessage());
        }
    }

    public static void writeNotesToCSV(String filePath, Map<YearMonth, String> notesByMonth) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Map.Entry<YearMonth, String> entry : notesByMonth.entrySet()) {
                writer.write(entry.getKey().format(FORMATTER) + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            MyLogger.error("Error while writing CSV note file");
            throw new IOException(e.getMessage());
        }
    }
}
