package io.silverstring.web.listener.mq;

import io.silverstring.core.service.batch.DepositTransactionBatchService;
import io.silverstring.domain.dto.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DepositTransactionMqListener {

    private final DepositTransactionBatchService depositTransactionBatchService;

    @Autowired
    public DepositTransactionMqListener(DepositTransactionBatchService depositTransactionBatchService) {
        this.depositTransactionBatchService = depositTransactionBatchService;
    }

    @RabbitListener(queues = "deposit_transactions")
    public void onMessage(final WalletDTO.TransactionInfo transactionInfo) throws Exception {
        log.info("* deposit_transactions::onMessage : {}", transactionInfo);
        try {
            depositTransactionBatchService.doTransaction(transactionInfo);
        } catch (Exception ex) {
            log.error("deposit_transactions onMessage ERROR : {}", ex.getMessage());
        }
    }
}
