package com.marketix.service;

import com.marketix.entity.Invoice;
import com.marketix.entity.User;

import java.util.List;
import java.util.Set;

public interface InvoiceService {
    List<Invoice> getAllInvoices();

    Set<Invoice> getAllUserInvoices(User user);

    Invoice getInvoiceById(Long id);

    Invoice add(Invoice invoice);

    Invoice deleteById(Long id);

//    Invoice update(Invoice invoice); //probably wont be used
}
