package com.example.services;

class InvalidTokenException extends Exception {
    InvalidTokenException() {
        super("Received invalid access token.");
    }
}
