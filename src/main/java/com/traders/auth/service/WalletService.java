package com.traders.auth.service;

import com.traders.auth.domain.User;
import com.traders.auth.domain.Wallet;
import com.traders.auth.repository.WalletRepository;
import com.traders.common.utils.DateTimeUtil;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void createUserWallet(User savedUser) {
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(DateTimeUtil.getCurrentDateTime());
        wallet.setUser(savedUser);
        wallet.setBalance(0.0);
        walletRepository.save(wallet);
    }
}
