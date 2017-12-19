package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.VirtualAccountRepository;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.VirtualAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class VirtualAccountService {

    private final VirtualAccountRepository virtualAccountRepository;

    @Autowired
    public VirtualAccountService(VirtualAccountRepository virtualAccountRepository) {
        this.virtualAccountRepository = virtualAccountRepository;
    }

    @SoftTransational
    public VirtualAccount allocVirtualAccount(Long userId) {
        Page<VirtualAccount> virtualAccountPage = virtualAccountRepository.findAllByAllocDtmIsNull(new PageRequest(0, 1));
        if (virtualAccountPage.getContent().size() <= 0) {
            throw new ExchangeException(CodeEnum.NOT_ENOUGH_VIRTUAL_ACCOUNT);
        }

        virtualAccountPage.getContent().get(0).setAllocDtm(LocalDateTime.now());
        virtualAccountPage.getContent().get(0).setUserId(userId);
        return virtualAccountPage.getContent().get(0);
    }
}
