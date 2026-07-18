package com.andres.course.agy.springboot.springapi.app.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super("Invoice not found with id: " + id);
    }
}
