package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.InvoiceDto;
import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> findAll();
    InvoiceDto findById(Long id);
    List<InvoiceDto> findByCustomerId(Long customerId);
    InvoiceDto create(InvoiceDto invoiceDto);
    InvoiceDto update(Long id, InvoiceDto invoiceDto);
    void delete(Long id);
}
