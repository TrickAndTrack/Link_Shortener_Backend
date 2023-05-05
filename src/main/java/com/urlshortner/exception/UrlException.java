package com.urlshortner.exception;

public class UrlException extends  RuntimeException {

    public UrlException(){
        super("Resource not found on server !!");

    }
    public UrlException(String message){
        super(message);
    }
}