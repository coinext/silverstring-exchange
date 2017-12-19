package io.silverstring.core.service;

import io.silverstring.core.repository.hibernate.AdminWalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AdminWalletService {
    private final AdminWalletRepository adminWalletRepository;

    @Autowired
    public AdminWalletService(AdminWalletRepository adminWalletRepository) {
        this.adminWalletRepository = adminWalletRepository;
    }
}
