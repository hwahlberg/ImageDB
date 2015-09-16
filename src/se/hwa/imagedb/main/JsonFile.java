/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.hwa.imagedb.main;

import se.hwa.imagedb.exif.ExifGroup;
import se.hwa.imagedb.exif.FileGroup;
import se.hwa.imagedb.exif.GifGroup;
import se.hwa.imagedb.exif.PngGroup;
import se.hwa.imagedb.exif.XmpGroup;

/**
 *
 * @author bbnthwa
 */
public class JsonFile {

    private String SourceFile;
    private FileGroup File;
    private ExifGroup EXIF;
    private GifGroup GIF;
    private PngGroup PNG;
    private XmpGroup XMP;

    public JsonFile() {
    }

    public String getSourceFile() {
        return SourceFile;
    }

    public FileGroup getFile() {
        return File;
    }

    public ExifGroup getEXIF() {
        return EXIF;
    }

    public PngGroup getPNG() {
        return PNG;
    }

    public GifGroup getGIF() {
        return GIF;
    }

    public XmpGroup getXMP() {
        return XMP;
    }

}
