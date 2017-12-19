package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.ManualTransactionRepository;
import io.silverstring.core.repository.hibernate.TransactionRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.domain.dto.TransactionDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.*;
import io.silverstring.domain.util.CompareUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
public class TransactionService {

    private final ManualTransactionRepository manualTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionService(ManualTransactionRepository manualTransactionRepository, TransactionRepository transactionRepository, WalletRepository walletRepository, ModelMapper modelMapper) {
        this.manualTransactionRepository = manualTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.modelMapper = modelMapper;
    }


    public TransactionDTO.ResTransactions getTransactions(Long userId, TransactionDTO.ReqTransactions reqTransactions) {
        Page<Transaction> transactionPage = transactionRepository.findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc(
                userId
                , new Coin(reqTransactions.getCoinName())
                , reqTransactions.getCategory()
                , new PageRequest(reqTransactions.getPageNo(),reqTransactions.getPageSize())
        );
        return TransactionDTO.ResTransactions.builder()
                .pageNo(reqTransactions.getPageNo())
                .pageSize(reqTransactions.getPageSize())
                .pageTotalCnt(transactionPage.getTotalPages())
                .transactions(transactionPage.getContent())
                .build();
    }

    @HardTransational
    public ManualTransaction regist(ManualTransaction manualTransaction) {
        //한도설정
        Optional<Wallet> existWalletOp = walletRepository.findOneByUserIdAndCoin(manualTransaction.getUserId(), manualTransaction.getCoin());
        if (!existWalletOp.isPresent()) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        Wallet wallet = existWalletOp.get();
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(wallet.getAvailableBalance(), manualTransaction.getAmount()))) {
            throw new ExchangeException(CodeEnum.AVAILABLE_BALANCE_NOT_ENOUGH);
        }


        if (CompareUtil.Condition.GT.equals(CompareUtil.compare(wallet.getCoin().getWithdrawalMinAmount(), manualTransaction.getAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(manualTransaction.getAmount()));
        wallet.setUsingBalance(wallet.getUsingBalance().add(manualTransaction.getAmount()));
        wallet.setTodayWithdrawalTotalBalance(wallet.getTodayWithdrawalTotalBalance().add(manualTransaction.getAmount()));

        //트랜잭션 생성
        TransactionPK transactionPK = new TransactionPK();
        transactionPK.setId(manualTransaction.getId());
        transactionPK.setUserId(manualTransaction.getUserId());
        Transaction existTransaction = transactionRepository.findOne(transactionPK);
        if (existTransaction != null) {
            throw new ExchangeException(CodeEnum.ALREADY_TRANSACTION_EXIST);
        }

        Transaction transaction = modelMapper.map(manualTransaction, Transaction.class);
        transaction.setTxId(manualTransaction.getId());
        transaction.setConfirmation(0l);
        transactionRepository.save(transaction);

        //메뉴얼 트랜잭션 생성
        ManualTransactionPK manualTransactionPK = new ManualTransactionPK();
        manualTransactionPK.setId(manualTransaction.getId());
        manualTransactionPK.setUserId(manualTransaction.getUserId());
        ManualTransaction existManualTransaction = manualTransactionRepository.findOne(manualTransactionPK);
        if (existManualTransaction != null) {
            throw new ExchangeException(CodeEnum.ALREADY_MANUAL_TRANSACTION_EXIST);
        }
        return manualTransactionRepository.save(manualTransaction);
    }

    @SoftTransational
    public ManualTransaction registManualTransaction(ManualTransaction manualTransaction) {
        ManualTransactionPK manualTransactionPK = new ManualTransactionPK();
        manualTransactionPK.setId(manualTransaction.getId());
        manualTransactionPK.setUserId(manualTransaction.getUserId());
        ManualTransaction existManualTransaction = manualTransactionRepository.findOne(manualTransactionPK);
        if (existManualTransaction != null) {
            throw new ExchangeException(CodeEnum.ALREADY_MANUAL_TRANSACTION_EXIST);
        }

        return manualTransactionRepository.save(manualTransaction);
    }

    @SoftTransational
    public void updateStatusManualTransaction(ManualTransactionPK manualTransactionPK, StatusEnum status, String reason) {
        ManualTransaction manualTransaction = manualTransactionRepository.findOne(manualTransactionPK);
        if (manualTransaction == null) {
            throw new ExchangeException(CodeEnum.MANUAL_TRANSACTION_NOT_EXIST);
        }

        if (!StatusEnum.PENDING.equals(manualTransaction.getStatus())) {
            throw new ExchangeException(CodeEnum.ALREADY_STATUS_IS_NOT_PENDING);
        }

        manualTransaction.setStatus(status);
        manualTransaction.setReason(reason);
    }

    @SoftTransational
    public Transaction registTransaction(Transaction transaction) {
        TransactionPK transactionPK = new TransactionPK();
        transactionPK.setId(transaction.getId());
        transactionPK.setUserId(transaction.getUserId());
        Transaction existTransaction = transactionRepository.findOne(transactionPK);
        if (existTransaction != null) {
            throw new ExchangeException(CodeEnum.ALREADY_TRANSACTION_EXIST);
        }

        return transactionRepository.save(transaction);
    }

    @SoftTransational
    public void updateStatusTransaction(TransactionPK transactionPK, StatusEnum status, String reason) {
        Transaction transaction = transactionRepository.findOne(transactionPK);
        if (transaction == null) {
            throw new ExchangeException(CodeEnum.TRANSACTION_NOT_EXIST);
        }

        if (!StatusEnum.PENDING.equals(transaction.getStatus())) {
            throw new ExchangeException(CodeEnum.ALREADY_STATUS_IS_NOT_PENDING);
        }

        transaction.setStatus(status);
        transaction.setReason(reason);
    }
}
