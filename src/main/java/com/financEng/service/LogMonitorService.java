package com.financEng.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class LogMonitorService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${logging.file}")
    private String logFile;

    private StringBuilder stringBuilder;

    /***********************************
     * Just get the actual log file.
     * *********************************/
    public String getLogMonitor() {
        onTheFlyLog();
        return stringBuilder.toString();
    }

    /****************************************
     * Read up the log file and append the
     * logfile lines to the string builder.
     * **************************************/
    private void onTheFlyLog(){
        try {
            stringBuilder = new StringBuilder();
            FileReader fileReader = new FileReader(logFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while(true){
                String line = bufferedReader.readLine();
                if(line != null){
                    stringBuilder.append(line+"\n");
                }
                else{
                    fileReader.close();
                    break;
                }
            }

        } catch (IOException e) {
            log.error(">> [onTheFlyLog] - Something went wrong meanwhile reading log file.");
            log.error(">> [onTheFlyLog] - Error: "+e.getMessage());
        }
    }

}
