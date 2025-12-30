package eu.ase.ro.parkingapplication.service;

import eu.ase.ro.parkingapplication.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    @Value("${app.receipts.dir:receipts}")
    private String receiptsDir;

    private static final DateTimeFormatter DF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());


    @Transactional
    public Path writeReceipt(Payment payment) {
        try {
            Path dir = Paths.get(receiptsDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String fileName = "receipt_payment_" + payment.getId() + ".txt";
            Path file = dir.resolve(fileName);

            var r = payment.getReservation();
            var spot = r.getSpot();

            String content = """
                    ===== PARKING RECEIPT =====
                    PaymentId: %d
                    Reference: %s
                    Status: %s
                    Method: %s
                    Amount: %d RON
                    PaidAt: %s

                    User: %s
                    ReservationId: %d
                    ReservationStatus: %s
                    Hours: %d
                    Spot: %s (%s)
                    ===========================
                    """.formatted(
                    payment.getId(),
                    payment.getReference(),
                    payment.getStatus(),
                    payment.getMethod(),
                    payment.getAmount(),
                    DF.format(payment.getCreatedAt()),
                    r.getUsername(),
                    r.getId(),
                    r.getStatus(),
                    r.getHours(),
                    spot.getNumber(),
                    spot.getZone()
            );

            Files.writeString(file, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return file;
        } catch (IOException e) {
            throw new IllegalStateException("Nu pot genera receipt: " + e.getMessage(), e);
        }
    }

    public Resource loadReceiptResource(Long paymentId) {
        Path dir = Paths.get(receiptsDir).toAbsolutePath().normalize();
        Path file = dir.resolve("receipt_payment_" + paymentId + ".txt");

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Receipt inexistent pentru paymentId=" + paymentId);
        }
        return new FileSystemResource(file);
    }
}
