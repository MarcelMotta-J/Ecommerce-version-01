package com.mrcl.store1.admin.service;

import com.mrcl.store1.admin.dao.AdminActionLogRepository;

import com.mrcl.store1.admin.dto.AdminActionLogRow;
import com.mrcl.store1.admin.entity.AdminActionLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.mrcl.store1.admin.dto.AdminPagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;

@Service
public class AdminActionLogService {

    private final AdminActionLogRepository repository;



    public AdminActionLogService(AdminActionLogRepository repository) {
        this.repository = repository;
    }

    public void log(Authentication auth, String action) {
        String email = auth != null ? auth.getName() : "unknown";
        repository.save(new AdminActionLog(email, action));
    }

    public List<AdminActionLogRow> getAllLogs() {
        return repository.findAllByOrderByTimestampDesc()
                .stream()
                .map(log -> new AdminActionLogRow(
                        log.getId(),
                        log.getAdminEmail(),
                        log.getAction(),
                        log.getTimestamp()
                ))
                .toList();
    }

    // TXT
    public byte[] exportActivityAsTxt() {

        String content = repository.findAllByOrderByTimestampDesc()
                .stream()
                .map(log -> log.getTimestamp()
                        + " | "
                        + log.getAdminEmail()
                        + " | "
                        + log.getAction())
                .collect(Collectors.joining(System.lineSeparator()));

        return content.getBytes(StandardCharsets.UTF_8);
    }

    // PDF
    public byte[] exportActivityAsPdf() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Admin Activity Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("ID")));
            table.addCell(new PdfPCell(new Phrase("Admin Email")));
            table.addCell(new PdfPCell(new Phrase("Action")));
            table.addCell(new PdfPCell(new Phrase("Timestamp")));

            repository.findAllByOrderByTimestampDesc().forEach(log -> {
                table.addCell(String.valueOf(log.getId()));
                table.addCell(log.getAdminEmail());
                table.addCell(log.getAction());
                table.addCell(String.valueOf(log.getTimestamp()));
            });

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    // pagination
    public AdminPagedResponse<AdminActionLogRow> getLogsPaged(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AdminActionLog> result = repository.findAllByOrderByTimestampDesc(pageable);

        return new AdminPagedResponse<>(
                result.getContent().stream()
                        .map(log -> new AdminActionLogRow(
                                log.getId(),
                                log.getAdminEmail(),
                                log.getAction(),
                                log.getTimestamp()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

}
