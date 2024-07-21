package com.example.cooperativa.config;

import com.example.cooperativa.service.EncerramentoSessaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class SchedulingConfig {

    @Value("${scheduler.updateRate}")
    private long fixedRate;

    private final EncerramentoSessaoService encerramentoSessaoService;

    public SchedulingConfig(final EncerramentoSessaoService encerramentoSessaoService) {
        this.encerramentoSessaoService = encerramentoSessaoService;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(encerramentoSessaoService::encerrarSessoesExpiradas,
                0,
                fixedRate,
                TimeUnit.MILLISECONDS);

        return executorService;
    }
}