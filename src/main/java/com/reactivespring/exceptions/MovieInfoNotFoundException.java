package com.reactivespring.exceptions;

public class MovieInfoNotFoundException extends RuntimeException{
    private String message;
    public MovieInfoNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
