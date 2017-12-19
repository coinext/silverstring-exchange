package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.SubmitDocumentRepository;
import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.domain.dto.DocDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.SubmitDocument;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Slf4j
@Service
public class SubmitDocumentService {

    final SubmitDocumentRepository submitDocumentRepository;
    final UserRepository userRepository;

    @Autowired
    public SubmitDocumentService(SubmitDocumentRepository submitDocumentRepository, UserRepository userRepository) {
        this.submitDocumentRepository = submitDocumentRepository;
        this.userRepository = userRepository;
    }

    public DocDTO.ResGet getAll(int pageNo, int pageSize) {

        DocDTO.ResGet res = new DocDTO.ResGet();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<SubmitDocument> result = submitDocumentRepository.findAll(new PageRequest(pageNo, pageSize));
        if (result.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(result.getTotalPages());
            return res;
        }

        res.setContents(result.getContent());
        res.setPageTotalCnt(result.getTotalPages());
        return res;
    }

    public void add(DocDTO.ReqAdd request) {

    }

    @SoftTransational
    public void edit(DocDTO.ReqEdit request) {
        User user = userRepository.findOne(request.getUserId());
        if (user == null) {
            throw new ExchangeException(CodeEnum.USER_NOT_EXIST);
        }

        SubmitDocument submitDocument = submitDocumentRepository.findOne(request.getUserId());
        submitDocument.setReason(request.getReason());
        submitDocument.setStatus(request.getStatus());

        if (StatusEnum.COMPLETED.equals(request.getStatus())) {
            user.setLevel(request.getLevel());
            submitDocument.setCompleteDtm(LocalDateTime.now());
        }
    }

    public void del(DocDTO.ReqDel request) {
        submitDocumentRepository.delete(request.getUserId());
    }
}
