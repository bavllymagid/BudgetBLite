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
    public ReportResponse getCache(String email, YearMonth date) {
        String key = email + ":" + date;
        Cache reportsCache = cacheManager.getCache("reports");

        if (reportsCache != null) {
            Cache.ValueWrapper wrapper = reportsCache.get(key);
            if (wrapper != null && wrapper.get() != null) {
                return (ReportResponse) wrapper.get();
            }
        }

        return null;
    }

    public void AddCache(ReportResponse report, YearMonth date) {
        String key = report.getUserEmail() + ":" + date;
        Cache reportsCache = cacheManager.getCache("reports");
        if (reportsCache != null) {
            reportsCache.put(key, report);
        }
    }
}