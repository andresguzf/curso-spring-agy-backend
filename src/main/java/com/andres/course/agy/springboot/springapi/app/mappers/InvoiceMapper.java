package com.andres.course.agy.springboot.springapi.app.mappers;

import com.andres.course.agy.springboot.springapi.app.dto.InvoiceDto;
import com.andres.course.agy.springboot.springapi.app.dto.InvoiceItemDto;
import com.andres.course.agy.springboot.springapi.app.models.Customer;
import com.andres.course.agy.springboot.springapi.app.models.Invoice;
import com.andres.course.agy.springboot.springapi.app.models.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    public InvoiceDto entityToDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        List<InvoiceItemDto> itemDtos = Collections.emptyList();
        if (invoice.getItems() != null) {
            itemDtos = invoice.getItems().stream()
                    .map(this::itemEntityToDto)
                    .collect(Collectors.toList());
        }

        return new InvoiceDto(
                invoice.getId(),
                invoice.getCustomer() != null ? invoice.getCustomer().getId() : null,
                invoice.getAmount(),
                invoice.getDescription(),
                invoice.getStatus(),
                itemDtos
        );
    }

    public Invoice dtoToEntity(InvoiceDto dto, Customer customer) {
        if (dto == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setId(dto.id());
        invoice.setCustomer(customer);
        invoice.setAmount(dto.amount());
        invoice.setDescription(dto.description());
        invoice.setStatus(dto.status());

        if (dto.items() != null) {
            for (InvoiceItemDto itemDto : dto.items()) {
                invoice.addItem(dtoToItemEntity(itemDto, invoice));
            }
        }

        return invoice;
    }

    public InvoiceItemDto itemEntityToDto(InvoiceItem item) {
        if (item == null) {
            return null;
        }
        return new InvoiceItemDto(
                item.getId(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    public InvoiceItem dtoToItemEntity(InvoiceItemDto itemDto, Invoice invoice) {
        if (itemDto == null) {
            return null;
        }
        InvoiceItem item = new InvoiceItem();
        item.setId(itemDto.id());
        item.setInvoice(invoice);
        item.setDescription(itemDto.description());
        item.setQuantity(itemDto.quantity());
        item.setUnitPrice(itemDto.unitPrice());
        item.setTotalPrice(itemDto.totalPrice());
        return item;
    }
}
