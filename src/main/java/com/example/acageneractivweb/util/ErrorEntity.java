package com.example.acageneractivweb.util;

public class ErrorEntity {
    String massage;
    public ErrorEntity(){}

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public ErrorEntity(String massage) {
        this.massage = massage;
    }
}
