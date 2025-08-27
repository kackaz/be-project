package com.example.beproject.transfer.config;

import com.example.beproject.transfer.repository.TransferRepository;
import com.example.beproject.transfer.repository.TransferRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TransferConfig {

    @Bean
    public TransferRepository transferRepository(DataSource dataSource) {
        return new TransferRepositoryImpl(dataSource);
    }
}
