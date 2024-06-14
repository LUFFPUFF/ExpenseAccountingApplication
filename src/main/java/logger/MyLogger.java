package logger;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MyLogger {

    public static void error(String title) {
        log.error(title);
    }

    public static void info(String title) {
        log.info(title);
    }
}
