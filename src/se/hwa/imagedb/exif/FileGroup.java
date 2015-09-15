/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.hwa.imagedb.exif;

/**
 *
 * @author bbnthwa
 */
public class FileGroup {
        private String FileName;
    private String Directory;
    private String FileType;
    private String FileSize;
    private int ImageWidth;
    private int ImageHeight;

    public void Filedata() {

    }

    public String getFileName() {
        return FileName;
    }

    public String getDirectory() {
        return Directory;
    }

    public String getFileType() {
        return FileType;
    }

    public String getFileSize() {
        return FileSize;
    }

    public int getImageWidth() {
        return ImageWidth;
    }

    public int getImageHeight() {
        return ImageHeight;
    }

    
}
