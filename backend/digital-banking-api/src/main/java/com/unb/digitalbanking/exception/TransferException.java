package com.unb.digitalbanking.exception;

public class TransferException extends RuntimeException {

    public TransferException(String message) {
        super(message);
    }
}