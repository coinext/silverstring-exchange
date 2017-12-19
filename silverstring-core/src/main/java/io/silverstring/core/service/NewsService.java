package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.NewsRepository;
import io.silverstring.domain.dto.NewsDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.hibernate.News;
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
public class NewsService {

    private final NewsRepository newsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NewsService(NewsRepository newsRepository, ModelMapper modelMapper) {
        this.newsRepository = newsRepository;
        this.modelMapper = modelMapper;
    }

    public News getTopN1News() {
        Page<News> news = newsRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(0, 1));
        if (news.getContent().size() <= 0) {
            return null;
        }
        return news.getContent().get(0);
    }

    public List<News> getTopNNews(int size) {
        Page<News> news = newsRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(0, size));
        if (news.getContent().size() <= 0) {
            return new ArrayList<>();
        }
        return news.getContent();
    }

    public NewsDTO.ResNews getAll(int pageNo, int pageSize) {
        NewsDTO.ResNews resNotice = new NewsDTO.ResNews();
        resNotice.setPageNo(pageNo);
        resNotice.setPageSize(pageSize);

        Page<News> news = newsRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(new PageRequest(pageNo, pageSize));
        if (news.getContent().size() <= 0) {
            resNotice.setContents(new ArrayList<>());
            resNotice.setPageTotalCnt(news.getTotalPages());
            return resNotice;
        }

        resNotice.setContents(news.getContent());
        resNotice.setPageTotalCnt(news.getTotalPages());

        return resNotice;
    }

    @SoftTransational
    public void add(NewsDTO.ReqAdd request) {
        News news = modelMapper.map(request, News.class);
        news.setRegDtm(LocalDateTime.now());
        newsRepository.save(news);
    }

    @SoftTransational
    public void edit(NewsDTO.ReqEdit request) {
        News news = modelMapper.map(request, News.class);
        News existNews = newsRepository.findOne(news.getId());
        if (existNews == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existNews.setTitle(news.getTitle());
        existNews.setUrl(news.getUrl());
    }

    @SoftTransational
    public void del(NewsDTO.ReqDel request) {
        News news = modelMapper.map(request, News.class);
        News existNews = newsRepository.findOne(news.getId());
        if (existNews == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existNews.setDelDtm(LocalDateTime.now());
    }
}
