package com.budget.b.lite.services;

import com.budget.b.lite.payload.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Check if a specific report is cached
     */
    public ReportResponse isReportCached(String email) {
        String key = email + ":" + YearMonth.now().toString();
        Cache reportsCache = cacheManager.getCache("reports");
        return reportsCache != null ? (ReportResponse) reportsCache.get(key) : null;
    }

}