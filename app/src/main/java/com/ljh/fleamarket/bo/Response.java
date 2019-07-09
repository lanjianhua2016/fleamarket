package com.ljh.fleamarket.bo;


public class Response {
    public int flag;
    public String message;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                '}';
    }
}
