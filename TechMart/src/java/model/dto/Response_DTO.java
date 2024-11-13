package model.dto;

import java.io.Serializable;

public class Response_DTO implements Serializable {

    private boolean success;
    private Object content;

    public Response_DTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
