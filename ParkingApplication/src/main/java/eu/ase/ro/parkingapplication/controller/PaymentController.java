package eu.ase.ro.parkingapplication.controller;

import eu.ase.ro.parkingapplication.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ReceiptService receiptService;

    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable Long paymentId) {
        Resource resource = receiptService.loadReceiptResource(paymentId);

        String filename = "receipt_payment_" + paymentId + ".txt";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
