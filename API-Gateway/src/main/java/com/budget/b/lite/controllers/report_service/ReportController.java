package com.budget.b.lite.controllers.report_service;

import com.budget.b.lite.services.RoutingService;
import com.budget.b.lite.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final RoutingService routingService;
    private final String reportServiceUrl;
    private final TokenUtils tokenUtils;

    public ReportController(RoutingService routingService,
                              TokenUtils tokenUtils,
                              @Value("${services.report.url:}") String directUrl) {
        this.routingService = routingService;
        this.tokenUtils = tokenUtils;
        this.reportServiceUrl = directUrl;
    }


    @GetMapping
    public ResponseEntity<String> getReport(){
        String email = tokenUtils.extractEmailFromToken();
        String url = reportServiceUrl + "/report?email=" + email;
        return routingService.forward(url, HttpMethod.GET, null);
    }
}
