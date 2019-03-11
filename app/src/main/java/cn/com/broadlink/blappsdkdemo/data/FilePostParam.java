package cn.com.broadlink.blappsdkdemo.data;

import java.io.File;

public class FilePostParam {

    private byte[] text;

    private File picdata;

    private File file;

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public File getPicdata() {
        return picdata;
    }

    public void setPicdata(File picdata) {
        this.picdata = picdata;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
