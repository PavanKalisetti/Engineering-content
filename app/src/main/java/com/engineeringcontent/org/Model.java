package com.engineeringcontent.org;

public class Model {
    String name, pdf_link, drive_id;
    Model(){

    }
    public Model(String drive_id, String name, String pdf_link) {
        this.name = name;
        this.pdf_link = pdf_link;
        this.drive_id = drive_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPdf_link(String pdf_link) {
        this.pdf_link = pdf_link;
    }

    public void setDrive_id(String drive_id) {
        this.drive_id = drive_id;
    }

    public String getName() {
        return name;
    }

    public String getPdf_link() {
        return pdf_link;
    }

    public String getDrive_id() {
        return drive_id;
    }
}
