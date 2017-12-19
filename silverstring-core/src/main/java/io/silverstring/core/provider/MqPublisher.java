package io.silverstring.core.provider;

import io.silverstring.domain.dto.MessagePacket;
import io.silverstring.domain.dto.WalletDTO;
import io.silverstring.domain.hibernate.EmailConfirm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class MqPublisher {

    final RabbitTemplate rabbitTemplate;

    @Autowired
    public MqPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void emailConfirmPublish(EmailConfirm emailConfirm) {
        rabbitTemplate.convertAndSend("exchange", "email_confirm", emailConfirm);
    }

    public void depositTransactionInfoPublish(WalletDTO.TransactionInfo transactionInfo) {
        rabbitTemplate.convertAndSend("exchange", "deposit_transactions", transactionInfo);
    }

    public void withdrawalTransactionInfoPublish(WalletDTO.TransactionInfo transactionInfo) {
        rabbitTemplate.convertAndSend("exchange", "withdrawal_transactions", transactionInfo);
    }

    public void websockMessagePublish(MessagePacket messagePacket) {
        rabbitTemplate.convertAndSend("exchange", "websock_message", messagePacket);
    }
}
