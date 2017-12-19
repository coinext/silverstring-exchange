package io.silverstring.core.service;

import io.silverstring.core.repository.hibernate.FingerPrintRepository;
import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.hibernate.FingerPrint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class FingerPrintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public FingerPrintService(FingerPrintRepository fingerPrintRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.fingerPrintRepository = fingerPrintRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean avaliableDeviceAccess(Long userId, String fingerprint) {
        List<FingerPrint> fingerPrints = fingerPrintRepository.findAllByUserIdAndDelDtmIsNullAndActive(userId, ActiveEnum.Y);
        if (fingerPrints == null || fingerPrints.size() <= 0) {
            return false;
        }

        for (FingerPrint fingerPrint : fingerPrints) {
            if (bCryptPasswordEncoder.matches(fingerprint, fingerPrint.getHashKey())) {
                return true;
            }
        }
        return false;
    }
}
