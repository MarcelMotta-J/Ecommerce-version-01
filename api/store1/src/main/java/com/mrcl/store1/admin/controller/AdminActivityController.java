package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminActionLogRow;
import com.mrcl.store1.admin.dto.AdminPagedResponse;
import com.mrcl.store1.admin.service.AdminActionLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin/activity")
public class AdminActivityController {
    private final AdminActionLogService logService;

    public AdminActivityController(AdminActionLogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public AdminPagedResponse<AdminActionLogRow> getActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return logService.getLogsPaged(page, size);
    }


    @GetMapping("/export/txt")
    public ResponseEntity<byte[]> exportActivityTxt() {

        byte[] file = logService.exportActivityAsTxt();


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-activity.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportActivityPdf() {

        byte[] file = logService.exportActivityAsPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-activity.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    // pagination



}
