package com.example.eval2;

import java.io.File;

public class DataFileGambar {
    private String pathfile;
    private File file;

    public DataFileGambar (String pathFile, File file) {
        this.pathfile = pathFile;
        this.file = file;
    }
    public String getPathfile() {
        return pathfile;
    }

    public void setPathfile(String pathfile) {
        this.pathfile = pathfile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
