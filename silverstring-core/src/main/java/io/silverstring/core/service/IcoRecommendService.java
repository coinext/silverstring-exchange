package io.silverstring.core.service;

import io.silverstring.core.repository.hibernate.IcoRecommendRepository;
import io.silverstring.domain.hibernate.IcoRecommend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class IcoRecommendService {

    private final IcoRecommendRepository icoRecommendRepository;

    @Autowired
    public IcoRecommendService(IcoRecommendRepository icoRecommendRepository) {
        this.icoRecommendRepository = icoRecommendRepository;
    }

    public List<IcoRecommend> getAll() {
        return icoRecommendRepository.findAll();
    }
}
