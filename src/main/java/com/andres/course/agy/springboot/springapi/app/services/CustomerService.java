package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.CustomerDto;
import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll();
    CustomerDto findById(Long id);
    CustomerDto create(CustomerDto customerDto);
    CustomerDto update(Long id, CustomerDto customerDto);
    void delete(Long id);
}
