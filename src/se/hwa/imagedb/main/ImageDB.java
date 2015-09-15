/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.hwa.imagedb.main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gnu.getopt.Getopt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import se.hwa.imagedb.db.Postgresql;

/**
 *
 * @author bbnthwa
 */
public class ImageDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        String propertyFile = System.getenv("HOME") + "/etc/imagedb.properties";
        String inputCreatedate = null;
        String inputModifydate = null;
        int c;
        boolean writeflag = false;

        Getopt g = new Getopt("imagedb", args, "Vwp:C:M:");
        g.setOpterr(false); // We'll do our own error handling
        //
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'w':
                    writeflag = true;
                    break;
                case 'p':
                    propertyFile = g.getOptarg();
                    break;
                case 'C':
                    inputCreatedate = g.getOptarg();
                    break;
                case 'M':
                    inputModifydate = g.getOptarg();
                    break;
                case 'V':
                    usage();
                    break;
            }
        }

        /*
         Need propertyfile for application
         */
        FileInputStream fis = new FileInputStream(propertyFile);
        File file = new File(propertyFile);
        if (file.exists() == false) {
            System.err.println("Missing propertyfile " + propertyFile + ", exit!");
            System.exit(1);
        }

        Properties envProperties = new Properties();

        //loading properites from properties file
        envProperties.load(fis);

        // init logging configuration
        if (envProperties.getProperty("log4j.rootLogger") != null) {
            PropertyConfigurator.configure(propertyFile);
        } else {
            BasicConfigurator.configure();
        }

        // open database
        Postgresql pgdb = new Postgresql();
        pgdb.open(envProperties.getProperty("db.server"),
                envProperties.getProperty("db.database"),
                envProperties.getProperty("db.user"),
                envProperties.getProperty("db.password"));

        pgdb.setWriteflag(writeflag);

        // we need at least one file on commandline
        int numberOfFiles = args.length - g.getOptind();
        if (numberOfFiles < 1) {
            usage();
        }

        /**
         * For every json inputfile, create ImageObject
         * and write to database
         */
        for (int i = 0; i < numberOfFiles; i++) {
            String inputfile = args[g.getOptind() + i];
            ImageObject img = new ImageObject();
            if (img.parseJson(inputfile, inputCreatedate, inputModifydate) == true) {
                pgdb.writeDb(img);
            }
        }

    }

    /**
     *
     */
    private static void usage() {
        System.out.println("imagedb version 0.1 April 28");
        System.out.println("Usage: imagedb [-V ][-w ][-p propertyfile] [-C Createdate] [-M Modifydate] file1 [file2 ...]");
        System.exit(0);
    }

}
