package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.repository.hibernate.ManualTransactionRepository;
import io.silverstring.core.repository.hibernate.TransactionRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.domain.dto.ManualTransactionDTO;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.ManualTransaction;
import io.silverstring.domain.hibernate.Transaction;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


@Slf4j
@Service
public class ManualTransactionService {

    private final ManualTransactionRepository manualTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final WalletRepository walletRepository;
    private final MqPublisher mqPublisher;
    private final TransactionService transactionService;

    @Autowired
    public ManualTransactionService(ManualTransactionRepository manualTransactionRepository, TransactionRepository transactionRepository, ModelMapper modelMapper, WalletRepository walletRepository, MqPublisher mqPublisher, TransactionService transactionService) {
        this.manualTransactionRepository = manualTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
        this.walletRepository = walletRepository;
        this.mqPublisher = mqPublisher;
        this.transactionService = transactionService;
    }

    public ManualTransactionDTO.ResManualTransaction getAll(CategoryEnum category, int pageNo, int pageSize) {
        ManualTransactionDTO.ResManualTransaction res = new ManualTransactionDTO.ResManualTransaction();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<ManualTransaction> result = manualTransactionRepository.findAllByCategoryOrderByRegDtDesc(category, new PageRequest(pageNo, pageSize));
        if (result.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(result.getTotalPages());
            return res;
        }

        res.setContents(result.getContent());
        res.setPageTotalCnt(result.getTotalPages());

        return res;
    }

    @HardTransational
    public void depositKrwAdd(ManualTransactionDTO.ReqDepositKrwAdd request) {
        Wallet wallet = walletRepository.findOneByCoinAndDepositDvcd(new Coin(request.getCoin()), request.getDepositDvcd());
        if (wallet == null) {
            throw new ExchangeException(CodeEnum.USER_NOT_EXIST);
        }
        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        manualTransaction.setId(KeyGenUtil.generateTxId());
        manualTransaction.setCoin(new Coin(request.getCoin()));
        manualTransaction.setUserId(wallet.getUserId());
        manualTransaction.setRegDt(LocalDateTime.now());
        manualTransaction.setStatus(StatusEnum.PENDING);
        manualTransactionRepository.save(manualTransaction);
    }

    @HardTransational
    public void depositKrwEdit(ManualTransactionDTO.ReqDepositKrwEdit request) {
        Wallet wallet = walletRepository.findOneByCoinAndDepositDvcd(new Coin(request.getCoin()), request.getDepositDvcd());
        if (wallet == null) {
            throw new ExchangeException(CodeEnum.USER_NOT_EXIST);
        }

        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        ManualTransaction existManualTransaction = manualTransactionRepository.findByIdAndUserId(manualTransaction.getId(), manualTransaction.getUserId());
        if (existManualTransaction == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existManualTransaction.setCategory(manualTransaction.getCategory());
        existManualTransaction.setAddress(manualTransaction.getAddress());
        existManualTransaction.setTag(manualTransaction.getTag());
        existManualTransaction.setBankNm(manualTransaction.getBankNm());
        existManualTransaction.setRecvNm(manualTransaction.getRecvNm());
        existManualTransaction.setAmount(manualTransaction.getAmount());
        existManualTransaction.setDepositDvcd(manualTransaction.getDepositDvcd());
        existManualTransaction.setStatus(manualTransaction.getStatus());
        existManualTransaction.setReason(manualTransaction.getReason());

        if (!StatusEnum.PENDING.equals(request.getStatus())) {
            if (StatusEnum.COMPLETED.equals(request.getStatus())) {
                WalletDTO.TransactionInfo transactionInfo = new WalletDTO.TransactionInfo();
                transactionInfo.setUserId(existManualTransaction.getUserId());
                transactionInfo.setCoinEnum(CoinEnum.KRW);
                transactionInfo.setAddress(existManualTransaction.getAddress());
                transactionInfo.setCategory(CategoryEnum.receive);
                transactionInfo.setAmount(existManualTransaction.getAmount());
                transactionInfo.setConfirmations(new BigInteger("10000"));
                transactionInfo.setTxid(existManualTransaction.getId());
                transactionInfo.setBankNm(existManualTransaction.getBankNm());
                transactionInfo.setRecvNm(existManualTransaction.getRecvNm());
                //transactionInfo.setTimereceived(LocalDateTime.now());
                mqPublisher.depositTransactionInfoPublish(transactionInfo);

                log.error("=====>>>>>>> depositKrwEdit transactionInfo {}", transactionInfo);
            } else {
                Transaction transaction = new Transaction();
                transaction.setId(existManualTransaction.getId());
                transaction.setUserId(existManualTransaction.getUserId());
                transaction.setCoin(new Coin(CoinEnum.KRW));
                transaction.setCategory(CategoryEnum.receive);
                transaction.setAddress(existManualTransaction.getAddress());
                transaction.setTag(existManualTransaction.getTag());
                transaction.setBankNm(existManualTransaction.getBankNm());
                transaction.setRecvNm(existManualTransaction.getRecvNm());
                transaction.setAmount(existManualTransaction.getAmount());
                transaction.setRegDt(LocalDateTime.now());
                transaction.setConfirmation(1000000l);
                transaction.setStatus(request.getStatus());
                transaction.setReason(request.getReason());
                transactionRepository.save(transaction);
            }
        }
    }

    @HardTransational
    public void depositKrwDel(ManualTransactionDTO.ReqDepositKrwDel request) {
        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        ManualTransaction existManualTransaction = manualTransactionRepository.findByIdAndUserId(manualTransaction.getId(), request.getUserId());
        if (existManualTransaction == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        manualTransactionRepository.delete(existManualTransaction);
    }

    @HardTransational
    public void withdrawalAdd(ManualTransactionDTO.ReqWithdrawalAdd request) {
        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        manualTransaction.setId(KeyGenUtil.generateTxId());
        manualTransaction.setCoin(new Coin(request.getCoin()));
        manualTransaction.setStatus(StatusEnum.PENDING);
        transactionService.regist(manualTransaction);
    }

    @HardTransational
    public void withdrawalEdit(ManualTransactionDTO.ReqWithdrawalEdit request) {
        Optional<Wallet> existWalletOp = walletRepository.findOneByUserIdAndCoin(request.getUserId(), new Coin(request.getCoin()));
        if (!existWalletOp.isPresent()) {
            throw new ExchangeException(CodeEnum.WALLET_NOT_EXIST);
        }

        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        ManualTransaction existManualTransaction = manualTransactionRepository.findByIdAndUserId(manualTransaction.getId(), manualTransaction.getUserId());
        if (existManualTransaction == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existManualTransaction.setAddress(manualTransaction.getAddress());
        existManualTransaction.setTag(manualTransaction.getTag());
        existManualTransaction.setBankNm(manualTransaction.getBankNm());
        existManualTransaction.setRecvNm(manualTransaction.getRecvNm());
        existManualTransaction.setAmount(manualTransaction.getAmount());
        existManualTransaction.setDepositDvcd(manualTransaction.getDepositDvcd());
        existManualTransaction.setStatus(manualTransaction.getStatus());
        existManualTransaction.setReason(manualTransaction.getReason());

        if (!StatusEnum.PENDING.equals(request.getStatus())) {
            if (StatusEnum.COMPLETED.equals(request.getStatus())) {
                WalletDTO.TransactionInfo transactionInfo = new WalletDTO.TransactionInfo();
                transactionInfo.setUserId(existManualTransaction.getUserId());
                transactionInfo.setCoinEnum(request.getCoin());
                transactionInfo.setAddress(existManualTransaction.getAddress());
                transactionInfo.setCategory(CategoryEnum.send);
                transactionInfo.setAmount(existManualTransaction.getAmount());
                transactionInfo.setConfirmations(new BigInteger("10000"));
                transactionInfo.setTxid(existManualTransaction.getId());
                transactionInfo.setBankNm(existManualTransaction.getBankNm());
                transactionInfo.setRecvNm(existManualTransaction.getRecvNm());
                //transactionInfo.setTimereceived(LocalDateTime.now());
                mqPublisher.withdrawalTransactionInfoPublish(transactionInfo);

                log.error("=====>>>>>>> withdrawalEdit transactionInfo {}", transactionInfo);
            } else {
                Transaction existTransaction = transactionRepository.findOneByCoinAndTxId(new Coin(request.getCoin()), existManualTransaction.getId());
                existTransaction.setAddress(manualTransaction.getAddress());
                existTransaction.setTag(manualTransaction.getTag());
                existTransaction.setBankNm(manualTransaction.getBankNm());
                existTransaction.setRecvNm(manualTransaction.getRecvNm());
                existTransaction.setAmount(manualTransaction.getAmount());
                existTransaction.setStatus(manualTransaction.getStatus());
                existTransaction.setReason(manualTransaction.getReason());
            }
        }

    }

    @HardTransational
    public void withdrawalDel(ManualTransactionDTO.ReqWithdrawalDel request) {
        ManualTransaction manualTransaction = modelMapper.map(request, ManualTransaction.class);
        ManualTransaction existManualTransaction = manualTransactionRepository.findByIdAndUserId(manualTransaction.getId(), request.getUserId());
        if (existManualTransaction == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        manualTransactionRepository.delete(existManualTransaction);
    }
}
