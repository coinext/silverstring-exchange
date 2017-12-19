package io.silverstring.web.listener.mq;

import io.silverstring.core.service.batch.WithdrawalTransactionBatchService;
import io.silverstring.domain.dto.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WithdrawalTransactionMqListener {

    private final WithdrawalTransactionBatchService withdrawalTransactionBatchService;

    @Autowired
    public WithdrawalTransactionMqListener(WithdrawalTransactionBatchService withdrawalTransactionBatchService) {
        this.withdrawalTransactionBatchService = withdrawalTransactionBatchService;
    }

    @RabbitListener(queues = "withdrawal_transactions")
    public void onMessage(final WalletDTO.TransactionInfo transactionInfo) {
        log.info("* withdrawal_transactions::onMessage : {}", transactionInfo);
        try {
            withdrawalTransactionBatchService.doTransaction(transactionInfo);
        } catch(Exception ex) {
            log.error("withdrawal_transactions onMessage ERROR : {}", ex.getMessage());
        }
    }
}
