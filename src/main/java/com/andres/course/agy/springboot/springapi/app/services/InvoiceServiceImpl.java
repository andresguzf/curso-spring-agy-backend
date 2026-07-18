package com.andres.course.agy.springboot.springapi.app.services;

import com.andres.course.agy.springboot.springapi.app.dto.InvoiceDto;
import com.andres.course.agy.springboot.springapi.app.dto.InvoiceItemDto;
import com.andres.course.agy.springboot.springapi.app.exceptions.CustomerNotFoundException;
import com.andres.course.agy.springboot.springapi.app.exceptions.InvoiceNotFoundException;
import com.andres.course.agy.springboot.springapi.app.mappers.InvoiceMapper;
import com.andres.course.agy.springboot.springapi.app.models.Customer;
import com.andres.course.agy.springboot.springapi.app.models.Invoice;
import com.andres.course.agy.springboot.springapi.app.repositories.CustomerRepository;
import com.andres.course.agy.springboot.springapi.app.repositories.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, CustomerRepository customerRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findAll() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        return invoiceMapper.entityToDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(invoiceMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDto create(InvoiceDto invoiceDto) {
        Customer customer = customerRepository.findById(invoiceDto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(invoiceDto.customerId()));

        Invoice invoice = invoiceMapper.dtoToEntity(invoiceDto, customer);
        invoice.setId(null); // Ensure creation
        if (invoice.getItems() != null) {
            invoice.getItems().forEach(item -> item.setId(null));
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.entityToDto(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        Customer customer = customerRepository.findById(invoiceDto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(invoiceDto.customerId()));

        existingInvoice.setCustomer(customer);
        existingInvoice.setAmount(invoiceDto.amount());
        existingInvoice.setDescription(invoiceDto.description());
        existingInvoice.setStatus(invoiceDto.status());

        // Update items (clear existing and add new/updated ones)
        existingInvoice.getItems().clear();
        if (invoiceDto.items() != null) {
            for (InvoiceItemDto itemDto : invoiceDto.items()) {
                existingInvoice.addItem(invoiceMapper.dtoToItemEntity(itemDto, existingInvoice));
            }
        }

        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        return invoiceMapper.entityToDto(updatedInvoice);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new InvoiceNotFoundException(id);
        }
        invoiceRepository.deleteById(id);
    }
}
