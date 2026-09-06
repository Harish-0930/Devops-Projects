package com.nexusbank.admin.exception;

public class FeignClientException extends RuntimeException {
    public FeignClientException(String message) { super(message); }
}