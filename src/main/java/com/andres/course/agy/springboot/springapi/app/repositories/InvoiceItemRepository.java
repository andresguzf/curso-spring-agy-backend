package com.andres.course.agy.springboot.springapi.app.repositories;

import com.andres.course.agy.springboot.springapi.app.models.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
