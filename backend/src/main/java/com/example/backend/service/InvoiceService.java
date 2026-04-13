package com.example.backend.service;

import com.example.backend.model.Invoice;
import com.example.backend.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesBySubscription(Long subscriptionId) {
        return invoiceRepository.findBySubscriptionId(subscriptionId);
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
}
