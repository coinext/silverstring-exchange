package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.CommandEnum;
import io.silverstring.domain.enums.PacketScopeEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessagePacket {
    private CommandEnum cmd;
    private PacketScopeEnum scope;
    private CoinEnum coin;
    private Long userId;
    private Object data;
    private CoinDTO.ResCoinAvgPrice coinAvgPrice;
}
