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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bbnthwa
 */
public class ImageObject {

    private String Createdate = null;
    private String Modifydate = null;
    private String Sourcetype = "??";
    private String Filename = null;
    private String Directory = null;
    private String Filesize = null;
    private String Filetype = null;
    private int Imagewidth = 0;
    private int Imageheight = 0;

    /**
     *
     */
    public ImageObject() {
    }

    public String getCreatedate() {
        return Createdate;
    }

    public String getModifydate() {
        return Modifydate;
    }

    public String getSourcetype() {
        return Sourcetype;
    }

    public String getFilename() {
        return Filename;
    }

    public String getDirectory() {
        return Directory;
    }

    public String getFilesize() {
        return Filesize;
    }

    public String getFiletype() {
        return Filetype;
    }

    public int getImagewidth() {
        return Imagewidth;
    }

    public int getImageheight() {
        return Imageheight;
    }
    
    
    

    /**
     *
     * @param jsonfile
     */
    public boolean parseJson(String inputfile,
            String inputCreatedate,
            String inputModifydate) {
        boolean retcode = true;
        Gson gson = new Gson();

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

        // data to extract from JSON/EXIF-file
        try {

            BufferedReader br2 = new BufferedReader(new FileReader(inputfile));
            BufferedReader br = new BufferedReader(new FileReader(inputfile));

            JsonElement je = new JsonParser().parse(br2);
            JsonObject jo = je.getAsJsonObject();

            //convert the json string to object  
            JsonFile jsonFile = gson.fromJson(br, JsonFile.class);

            // then get some data
            Filename = jsonFile.getFile().getFileName();
            Directory = jsonFile.getFile().getDirectory();
            Filesize = jsonFile.getFile().getFileSize();
            Filetype = jsonFile.getFile().getFileType();

            // rest of data depends of filetype (imgagetype)
            if (Filetype.equals("PNG")) {
                Imagewidth = jsonFile.getPNG().getImageWidth();
                Imageheight = jsonFile.getPNG().getImageHeight();
                Modifydate = jsonFile.getPNG().getModifyDate();

            } else if (Filetype.equals("GIF")) {
                Imagewidth = jsonFile.getGIF().getImageWidth();
                Imageheight = jsonFile.getGIF().getImageHeight();

            } else if (Filetype.equals("JPEG")) {
                Imagewidth = jsonFile.getFile().getImageWidth();
                Imageheight = jsonFile.getFile().getImageHeight();
                Modifydate = jsonFile.getEXIF().getModifyDate();
                Createdate = jsonFile.getEXIF().getCreateDate();

            } else {
                System.err.println("Unknown filetype " + Filetype + ", exit!");
                return false;
            }
            // override?
            if (inputCreatedate != null) {
                Createdate = inputCreatedate;
            }
            if (inputModifydate != null) {
                Modifydate = inputModifydate;
            }

            printit();

            if (Createdate != null) {
                Date cd = sdf.parse(Createdate);
                //System.out.println("Createdate as Date=" + cd);
            }
            // Ok, now put things in database

        } catch (IOException ioe) {
            System.err.println("Read error json-file " + ioe.getMessage());
        } catch (ParseException pe) {
            System.err.println("Date parse error " + pe.getMessage());
        }

        return retcode;

    }

    /**
     *
     */
    private void printit() {
        System.out.println("\nReading JSON from a file");
        System.out.println("----------------------------");
        System.out.println("Filename=" + Filename);
        System.out.println("Directory=" + Directory);
        System.out.println("Filesize=" + Filesize);
        System.out.println("Filetype=" + Filetype);
        System.out.println("Imagewidth=" + Imagewidth);
        System.out.println("Imageheight=" + Imageheight);
        System.out.println("Createdate=" + Createdate);
        System.out.println("Modifydate=" + Modifydate);

    }
}
