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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author bbnthwa
 */
public class ImageObject {

    private String Image_UUID = null;
    private String inputCreatedate = null;
    private String inputModifydate = null;
    private String Createdate = null;
    private String Modifydate = null;
    private String Sourcetype = "??";
    private String Filename = null;
    private String Directory = null;
    private String Filesize = null;
    private String Filetype = null;
    private int Imagewidth = 0;
    private int Imageheight = 0;
    private JsonObject jo = null;

    private static final Logger LOG = Logger.getLogger(ImageObject.class.getSimpleName());

    /**
     *
     */
    public ImageObject() {
    }

    public String getImage_UUID() {
        return Image_UUID;
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

    public String getInputCreatedate() {
        return inputCreatedate;
    }

    public void setInputCreatedate(String inputCreatedate) {
        this.inputCreatedate = inputCreatedate;
    }

    public String getInputModifydate() {
        return inputModifydate;
    }

    public void setInputModifydate(String inputModifydate) {
        this.inputModifydate = inputModifydate;
    }

    public JsonObject getJo() {
        return jo;
    }

    /**
     *
     * @return Createdate as a Timestamp
     */
    public Timestamp getCreatedateAsTimestamp() {
        return StringToTimestamp(Createdate);
    }

    /**
     *
     * @return Modifydate as a Timestamp
     */
    public Timestamp getModifydateAsTimestamp() {
        return StringToTimestamp(Modifydate);
    }

    /**
     * Convert string to Timestamp
     *
     * @param datetime
     * @return
     */
    private Timestamp StringToTimestamp(String datetime) {
        Timestamp timestamp;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            Date parsedDate = dateFormat.parse(datetime);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            return null;
        }

        return timestamp;

    }

    /**
     *
     * @return
     */
    public String getCreatedateAsToTimestring() {
        return DateAsToTimestamp(Createdate);
    }

    /**
     *
     * @return
     */
    public String getModifydateAsToTimestring() {
        return DateAsToTimestamp(Modifydate);
    }

    /**
     * Returns datetime as a "to_timestamp(...)" string
     *
     * @param datetime
     * @return
     */
    private String DateAsToTimestamp(String datetime) {
        String timestampString = null;
        if (datetime != null) {
            timestampString = "to_timestamp('" + datetime + "','YYYYMMDDHH24MISS')";
        }
        return timestampString;
    }

    /**
     *
     * @param jsonfile
     */
    public boolean parseJson(String inputfile) {
        boolean retcode = true;
        Gson gson = new Gson();

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

        // data to extract from JSON/EXIF-file
        try {

            BufferedReader br2 = new BufferedReader(new FileReader(inputfile));
            BufferedReader br = new BufferedReader(new FileReader(inputfile));

            JsonElement je = new JsonParser().parse(br2);
            jo = je.getAsJsonObject();

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
                Image_UUID = jsonFile.getEXIF().getUserComment();

            } else if (Filetype.equals("GIF")) {
                Imagewidth = jsonFile.getGIF().getImageWidth();
                Imageheight = jsonFile.getGIF().getImageHeight();
                Image_UUID = jsonFile.getXMP().getUserComment();

            } else if (Filetype.equals("JPEG")) {
                Imagewidth = jsonFile.getFile().getImageWidth();
                Imageheight = jsonFile.getFile().getImageHeight();
                Modifydate = jsonFile.getEXIF().getModifyDate();
                Createdate = jsonFile.getEXIF().getCreateDate();
                Image_UUID = jsonFile.getEXIF().getUserComment();

            } else {
                LOG.error("Unknown filetype " + Filetype + ", exit!");
                return false;
            }
            // override?
            if (inputCreatedate != null) {
                Createdate = inputCreatedate;
            }
            if (inputModifydate != null) {
                Modifydate = inputModifydate;
            }

            /**
             * Get Image_UUID from UserComment-string If not found, don't accept
             * image
             */
            Image_UUID = checkImage_UUID(Image_UUID);
            if (Image_UUID == null) {
                return false;
            }

            printit();

            if (Createdate != null) {
                Date cd = sdf.parse(Createdate);
                //System.out.println("Createdate as Date=" + cd);
            }
            // Ok, now put things in database

        } catch (IOException ioe) {
            LOG.error("Read error json-file " + ioe.getMessage());
        } catch (ParseException pe) {
            LOG.error("Date parse error " + pe.getMessage());
        }

        return retcode;

    }

    /**
     *
     * @param usercomment Check that image/json file includes Image_UUID i
     * UserComment, ie. format: Image_UUID:a8bfc53c-f078-401e-974f-a9b84edbcfe9
     *
     * @return
     */
    private String checkImage_UUID(String usercomment) {
        String uuid = null;

        if (usercomment == null) {
            LOG.error("Missing UserComment tag!");
            return null;
        }

        StringTokenizer tk = new StringTokenizer(usercomment, ":");
        if (tk.countTokens() != 2) {
            LOG.error("Illegal format UserComment");
            return null;
        }
        String t = tk.nextToken();
        if (t.equals(Constants.IMAGE_UUID) == false) {
            LOG.error("Illegal Image_UUID " + t);
            return null;
        }

        uuid = tk.nextToken();
        if (uuid == null || uuid.length() != 36) {
            LOG.error("Image_UUID missformat, must be 36 chars");
            return null;
        }

        return uuid;
    }

    /**
     *
     */
    private void printit() {
        LOG.debug("\nReading JSON from a file");
        LOG.debug("----------------------------");
        LOG.debug("Image_UUID   =" + Image_UUID);
        LOG.debug("Filename     =" + Filename);
        LOG.debug("Directory    =" + Directory);
        LOG.debug("Filesize     =" + Filesize);
        LOG.debug("Filetype     =" + Filetype);
        LOG.debug("Imagewidth   =" + Imagewidth);
        LOG.debug("Imageheight  =" + Imageheight);
        LOG.debug("Createdate   =" + Createdate);
        LOG.debug("Modifydate   =" + Modifydate);

    }
}
