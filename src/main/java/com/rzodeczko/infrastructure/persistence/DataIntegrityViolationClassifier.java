package com.rzodeczko.infrastructure.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataIntegrityViolationClassifier {

    public boolean isOrderIdUniqueViolation(DataIntegrityViolationException exception) {
        Throwable current = exception;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("uk_invoice_order_id")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}