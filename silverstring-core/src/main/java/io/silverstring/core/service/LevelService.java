package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.LevelRepository;
import io.silverstring.core.repository.hibernate.SubmitDocumentRepository;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.domain.dto.LevelDTO;
import io.silverstring.domain.enums.*;
import io.silverstring.domain.hibernate.Level;
import io.silverstring.domain.hibernate.LevelPK;
import io.silverstring.domain.hibernate.SubmitDocument;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class LevelService {

    public final static String UPLOAD_PATH = "/data/upload/";

    private final LevelRepository levelRepository;
    private final ModelMapper modelMapper;
    private final SubmitDocumentRepository submitDocumentRepository;

    @Autowired
    public LevelService(LevelRepository levelRepository, ModelMapper modelMapper, SubmitDocumentRepository submitDocumentRepository) {
        this.levelRepository = levelRepository;
        this.modelMapper = modelMapper;
        this.submitDocumentRepository = submitDocumentRepository;
    }

    public List<LevelDTO.GroupInfo> getAllGroups() {

        Map<CoinEnum, LevelDTO.GroupInfo> levels = new HashMap();

        for (Level level : levelRepository.findAll()) {
            LevelDTO.GroupInfo groupInfo = null;
            if (levels.containsKey(level.getCoinName())) {
                groupInfo = levels.get(level.getCoinName());
            } else {
                groupInfo = new LevelDTO.GroupInfo();
                groupInfo.setCoinName(level.getCoinName());
            }

            if (level.getLevel().equals(LevelEnum.LEVEL1)) {
                groupInfo.setLevel1(level);
            } else if (level.getLevel().equals(LevelEnum.LEVEL2)) {
                groupInfo.setLevel2(level);
            } else if (level.getLevel().equals(LevelEnum.LEVEL3)) {
                groupInfo.setLevel3(level);
            }

            levels.put(level.getCoinName(), groupInfo);
        }

        return new ArrayList(levels.values());
    }

    public List<Level> getAll() {
        return levelRepository.findAll();
    }

    public LevelDTO.ResLevel getAll(int pageNo, int pageSize) {
        LevelDTO.ResLevel res = new LevelDTO.ResLevel();
        res.setPageNo(pageNo);
        res.setPageSize(pageSize);

        Page<Level> result = levelRepository.findAll(new PageRequest(pageNo, pageSize));
        if (result.getContent().size() <= 0) {
            res.setContents(new ArrayList<>());
            res.setPageTotalCnt(result.getTotalPages());
            return res;
        }

        res.setContents(result.getContent());
        res.setPageTotalCnt(result.getTotalPages());
        return res;
    }

    @SoftTransational
    public void add(LevelDTO.ReqAdd request) {
        Level res = modelMapper.map(request, Level.class);
        levelRepository.save(res);
    }

    @SoftTransational
    public void edit(LevelDTO.ReqEdit request) {
        LevelPK res = modelMapper.map(request, LevelPK.class);
        Level existOne = levelRepository.findOne(res);
        if (existOne == null) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }
        existOne.setLevel(request.getLevel());
        existOne.setOnceAmount(request.getOnceAmount());
        existOne.setOnedayAmount(request.getOnceAmount());
    }

    @SoftTransational
    public void del(LevelDTO.ReqDel request) {
        LevelPK res = modelMapper.map(request, LevelPK.class);
        levelRepository.delete(res);
    }

    @SoftTransational
    public void uploadDoc(String type, User user, MultipartFile file) throws IOException {
        File directory = new File(UPLOAD_PATH + type);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String hash = user.getId() + KeyGenUtil.generateDocFileName();
        SubmitDocument submitDocument = submitDocumentRepository.findOne(user.getId());
        if ("idcard".equals(type)) {
            submitDocument.setLevelIdcardUrlHash(hash);
            submitDocument.setRegDtm(LocalDateTime.now());
            submitDocument.setStatus(StatusEnum.COMPLETED);
        } else if ("doc".equals(type)) {
            submitDocument.setLevelDocUrlHash(hash);
            submitDocument.setRegDtm(LocalDateTime.now());
            submitDocument.setStatus(StatusEnum.COMPLETED);
        }

        File convFile = new File(UPLOAD_PATH + type + "/" + hash);
        if (!convFile.exists()) {
            convFile.createNewFile();
        } else {
            log.warn("upload file exist.");
        }

        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
    }

    public void flushDoc(String type, User user, String hash, HttpServletResponse response) throws IOException {
        if (!RoleEnum.ADMIN.equals(user.getRole())) {
            throw new ExchangeException(CodeEnum.BAD_REQUEST);
        }

        response.setContentType("image/png");
        String url = LevelService.UPLOAD_PATH + type + "/" + hash;
        log.info("image : {}", url);
        File file = new File(url);
        BufferedImage bi = ImageIO.read(file);
        OutputStream out = response.getOutputStream();
        ImageIO.write(bi, "png", out);
        out.close();
    }
}
