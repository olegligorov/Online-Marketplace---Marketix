package com.marketix.dao;

import com.marketix.entity.Invoice;
import com.marketix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Set<Invoice> findByUser(User user);
}
