package com.example.backend.controller;

import com.example.backend.model.Invoice;
import com.example.backend.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        // Admin use case
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @GetMapping("/subscriptions/{id}/invoices")
    public ResponseEntity<List<Invoice>> getSubscriptionInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoicesBySubscription(id));
    }
}
