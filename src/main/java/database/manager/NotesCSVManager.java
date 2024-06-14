package database.manager;

import database.util.NotesCVSParser;
import logger.MyLogger;

import java.io.IOException;
import java.time.YearMonth;
import java.util.Map;

public class NotesCSVManager {

    private static final String FILE_PATH = "files/notes.csv";
    private Map<YearMonth, String> notesByMonth;

    public NotesCSVManager(Map<YearMonth, String> notesByMonth) {
        this.notesByMonth = notesByMonth;
    }

    public void saveNotesToCSV() {
        try {
            NotesCVSParser.writeNotesToCSV(FILE_PATH, notesByMonth);
        } catch (IOException e) {
            MyLogger.error("Error save notes csv");
        }
    }
}
