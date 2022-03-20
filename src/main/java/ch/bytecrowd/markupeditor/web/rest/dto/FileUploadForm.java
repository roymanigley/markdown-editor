package ch.bytecrowd.markupeditor.web.rest.dto;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

public class FileUploadForm {

    public FileUploadForm() {
    }

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    @FormParam("file")
    @PartType("application/octet-stream")
    public void setData(byte[] data) {
        this.data = data;
    }

}
