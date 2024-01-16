package com.marketix.service.impl;

import com.marketix.dao.InvoiceRepository;
import com.marketix.dao.UserRepository;
import com.marketix.entity.Invoice;
import com.marketix.entity.User;
import com.marketix.exception.UserNotFoundException;
import com.marketix.service.InvoiceService;
import com.marketix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Set<Invoice> getAllUserInvoices(User user) {
        return invoiceRepository.findByUser(user);
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() ->
                new RuntimeException(String.format("Invoice with id %d does not exist", id)));
    }

    @Override
    public Invoice add(Invoice invoice) {
        var now = LocalDateTime.now();
        invoice.setCreated(now);
        invoice.setModified(now);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice deleteById(Long id) {
        var deleted = getInvoiceById(id);
        invoiceRepository.deleteById(id);
        return deleted;
    }
}
