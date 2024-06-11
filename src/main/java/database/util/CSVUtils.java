package database.util;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class CSVUtils {

    public static List<String[]> readCSV(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            return reader.lines()
                    .map(line -> line.split(","))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while reading CSV file", e);
            throw new IOException(e.getMessage());
        }
    }

    public static void writeCSV(String filePath, List<String[]> csvList) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (String[] csv : csvList) {
                writer.write(String.join(",", csv));
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("Error while writing CSV file", e);
            throw new IOException(e.getMessage());
        }
    }
}
