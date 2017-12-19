package io.silverstring.core.handler;

import io.silverstring.domain.dto.MessagePacket;
import io.silverstring.domain.enums.CoinEnum;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class WebsockMsgBrocker {

    @Data
    @Builder
    public static class SendToDTO {
        private String cmd;
        private CoinEnum coin;
        private Long id;
        private Object data;
    }

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void send(MessagePacket messagePacket) {
        log.info(" ======= {}", messagePacket);
        try {
            simpMessagingTemplate.convertAndSend("/topic/exchange", messagePacket);
        } catch (Exception ex) {

        }
    }
}
