package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.NoticeRepository;
import io.silverstring.domain.dto.NoticeDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.Notice;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, ModelMapper modelMapper) {
        this.noticeRepository = noticeRepository;
        this.modelMapper = modelMapper;
    }

    public Notice getTopN1Notice() {
        Page<Notice> notices = noticeRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(0, 1));
        if (notices.getContent().size() <= 0) {
            return null;
        }
        return notices.getContent().get(0);
    }

    public List<Notice> getTopNNotices(int size) {
        Page<Notice> notices = noticeRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(0, size));
        if (notices.getContent().size() <= 0) {
            return new ArrayList<>();
        }
        return notices.getContent();
    }

    public NoticeDTO.ResNotice getAll(int pageNo, int pageSize) {
        NoticeDTO.ResNotice resNotice = new NoticeDTO.ResNotice();
        resNotice.setPageNo(pageNo);
        resNotice.setPageSize(pageSize);

        Page<Notice> notices = noticeRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(pageNo, pageSize));
        if (notices.getContent().size() <= 0) {
            resNotice.setContents(new ArrayList<>());
            resNotice.setPageTotalCnt(notices.getTotalPages());
            return resNotice;
        }

        resNotice.setContents(notices.getContent());
        resNotice.setPageTotalCnt(notices.getTotalPages());

        return resNotice;
    }

    @SoftTransational
    public void add(NoticeDTO.ReqAdd request) {
        Notice notice = modelMapper.map(request, Notice.class);
        notice.setRegDtm(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    @SoftTransational
    public void edit(NoticeDTO.ReqEdit request) {
        Notice notice = modelMapper.map(request, Notice.class);
        Notice existNotice = noticeRepository.findOne(notice.getId());
        if (existNotice == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existNotice.setTitle(notice.getTitle());
        existNotice.setContent(notice.getContent());
        existNotice.setUrl(notice.getUrl());
    }

    @SoftTransational
    public void del(NoticeDTO.ReqDel request) {
        Notice notice = modelMapper.map(request, Notice.class);
        Notice existNotice = noticeRepository.findOne(notice.getId());
        if (existNotice == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existNotice.setDelDtm(LocalDateTime.now());
    }
}
