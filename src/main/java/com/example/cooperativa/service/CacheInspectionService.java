package com.example.cooperativa.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheInspectionService {

    private final CacheManager cacheManager;

    @PostConstruct
    public void inspectCaches() {
        log.info("Caches disponíveis: {}", cacheManager.getCacheNames());
    }
}
