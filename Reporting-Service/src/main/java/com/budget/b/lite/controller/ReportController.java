package com.budget.b.lite.controller;

import com.budget.b.lite.payload.ReportResponse;
import com.budget.b.lite.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @Autowired
    ReportService reportService;

    @GetMapping("/report")
    ResponseEntity<ReportResponse> getReport(@RequestParam String email){
     return ResponseEntity.ok(reportService.getReport(email));
    }
}
