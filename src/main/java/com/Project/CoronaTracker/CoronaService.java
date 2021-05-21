package com.Project.CoronaTracker;

import com.Project.CoronaTracker.Corona;
import com.Project.CoronaTracker.CoronaRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.python.util.PythonInterpreter;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class provides methods for populating the database and accessing it.
*/

@Slf4j
@Service
public class CoronaService {
    private CoronaRepository coronaRepository;

    public CoronaService(CoronaRepository coronaRepository) {
        this.coronaRepository = coronaRepository;
    }
/**
* This method is used to store records in the table
* @param corona object of Corona class which contains all details(deaths,confirmed,active,etc.) about the records
 */
    public void save(Corona corona)
    {
        coronaRepository.save(corona);
    }

    private LocalDateTime localDateTime;


/**
 *  This method runs everyday at 6:30 AM to populate the database with latest data.
 */
    @Scheduled(cron = "0 30 06 * * *",zone="America/New_York")
    public void populateDatabase(){
        localDateTime = LocalDateTime.now();
        log.info("Executed now: "+ localDateTime.toString());

        // Checks if table in database is empty
        // If not empty, clears the table and populates it with new data
        if(checkEmpty()==0)
            log.info("Empty Table");
        else {
            deleteTableRecords();
            log.info("Table not Empty");
        }

        URL url = null;

        // Get recent date, append it to URL and establish a connection
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = Date.from(Instant.now().minus(Duration.ofDays(1)));


        String dateStr = simpleDateFormat.format(date);

        try {
            url = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/"+dateStr+".csv");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = 0;
        try {
            responseCode = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // On successful connection, read records line by line, store records in a table
        if(responseCode==200)
        {
            log.info("-- Successful Connection");
            CSVReader reader = null;
            try {

                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()),8192);
                reader = new CSVReader(input);
                String[] line;
                int skipFirstLine = 0;
                while ((line = reader.readNext()) != null)
                {
                    if(skipFirstLine==0)
                    {
                        skipFirstLine++;
                        continue;
                    }

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    Corona corona = new Corona();
                    corona.setLastUpdate(LocalDateTime.parse(line[4],dateTimeFormatter));
                    if(line[7].equals(""))
                        line[7]="0";
                    if(line[9].equals(""))
                        line[9]="0";
                    if(line[10].equals(""))
                        line[10]="0";
                    if(line[8].equals(""))
                        line[8]="0";
                    if(line[5].equals(""))
                        line[5]="0";
                    if(line[6].equals(""))
                        line[6]="0";

                    try {

                        corona.setConfirmed(Long.valueOf(line[7]));
                        corona.setRecovered(Long.valueOf(line[9]));
                        corona.setActive(Long.valueOf(line[10]));
                        corona.setDeaths(Long.valueOf(line[8]));
                        corona.setLatitude(Double.valueOf(line[5]));
                        corona.setLongitude(Double.valueOf(line[6]));
                    } catch (NumberFormatException n)
                    {
                        n.printStackTrace();
                    }
                    corona.setCountry(line[3]);
                    corona.setProvince(line[2]);
                    corona.setCombinedKey(line[11]);



                    coronaRepository.save(corona);
                    log.info(corona.toString());


                }
            } catch (IOException | CsvValidationException e){
                e.printStackTrace();
            } finally {
                if(reader!=null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



/**
* Method to execute python script for visualization of data
 */
    public void executePython()
    {

        Process pr;
        try {
            pr = Runtime.getRuntime().exec("/usr/local/bin//python3 /Users/jithinjose/Downloads/Corona-Tracker/src/main/resources/test12.py");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Method to get all records in the table
     */
    public List<Corona> findAll() {
        return coronaRepository.findAll();
    }
/**
* Method to check if table is empty
 */
    public Integer checkEmpty()
    {
        return coronaRepository.checkEmptyTable();
    }
/**
* Method to delete all records in a table
 */
    public void deleteTableRecords()
    {
        coronaRepository.deleteAll();
    }
}
