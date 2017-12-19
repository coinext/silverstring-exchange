package io.silverstring.core.service;

import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.repository.hibernate.CoinRepository;
import io.silverstring.core.repository.hibernate.EmailConfirmRepository;
import io.silverstring.core.repository.hibernate.FingerPrintRepository;
import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.core.util.KeyGenUtil;
import io.silverstring.domain.dto.UserDTO;
import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.LevelEnum;
import io.silverstring.domain.hibernate.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final CoinRepository coinRepository;
    private final FingerPrintRepository fingerPrintRepository;
    private final EmailConfirmRepository emailConfirmRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MqPublisher mqPublisher;
    private final OtpService otpService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, WalletService walletService, CoinRepository coinRepository, FingerPrintRepository fingerPrintRepository, EmailConfirmRepository emailConfirmRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MqPublisher mqPublisher, OtpService otpService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.coinRepository = coinRepository;
        this.fingerPrintRepository = fingerPrintRepository;
        this.emailConfirmRepository = emailConfirmRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mqPublisher = mqPublisher;
        this.otpService = otpService;
        this.modelMapper = modelMapper;
    }

    public UserDTO.ResReleaseMember releaseMember(User user, UserDTO.ReqReleaseMember request) {
        return null;
    }

    @SoftTransational
    public UserDTO.ResChangePassword changePassword(User user, UserDTO.ReqChangePassword request) {
        if (!request.getNewPassword().equals(request.getNewPasswordRe())) {
            throw new ExchangeException(CodeEnum.INVALID_PASSWORD);
        }

        if (request.getPassword().equals(request.getPassword())) {
            throw new ExchangeException(CodeEnum.INVALID_PASSWORD);
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPwd())) {
            throw new ExchangeException(CodeEnum.INVALID_PASSWORD);
        }

        User existUser = userRepository.findOne(user.getId());
        if (existUser.getDelDtm() != null) {
            throw new ExchangeException(CodeEnum.USER_NOT_EXIST);
        }

        existUser.setPwd(bCryptPasswordEncoder.encode(request.getNewPassword()));
        return modelMapper.map(existUser, UserDTO.ResChangePassword.class);
    }

    public Optional<User> getActiveUserByEmailAndPwd(String email, String pwd) {
        return Optional.ofNullable(userRepository.findOneByEmailAndPwdAndDelDtmIsNullAndActive(email, pwd, ActiveEnum.Y));
    }

    public Optional<User> getActiveUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findOneByEmailAndDelDtmIsNullAndActive(email, ActiveEnum.Y));
    }

    @SoftTransational
    public UserDTO.ResEmailConfirm emailConfirm(String hashEmaul, String code) {

        EmailConfirmPK emailConfirmPK = new EmailConfirmPK();
        emailConfirmPK.setHashEmail(hashEmaul);
        emailConfirmPK.setCode(code);

        EmailConfirm emailConfirm = emailConfirmRepository.findOne(emailConfirmPK);
        if (emailConfirm == null) {
            UserDTO.ResEmailConfirm resEmailConfirm = new UserDTO.ResEmailConfirm();
            resEmailConfirm.setTitle("이메일 인증에 실패하였습니다.");
            resEmailConfirm.setMsg("존재하지 않는 인증메일입니다. 다시한번 확인해주세요.");
            resEmailConfirm.setUrl("/regist");
            resEmailConfirm.setUrlTitle("가입하기");
            return resEmailConfirm;
        }

        if (ActiveEnum.Y.equals(emailConfirm.getSendYn())) {
            UserDTO.ResEmailConfirm resEmailConfirm = new UserDTO.ResEmailConfirm();
            resEmailConfirm.setTitle("이메일 인증에 실패하였습니다.");
            resEmailConfirm.setMsg("유효하지 않은 인증입니다. 다시한번 확인해주세요.");
            resEmailConfirm.setUrl("/regist");
            resEmailConfirm.setUrlTitle("가입하기");
            return resEmailConfirm;
        }
        emailConfirm.setSendYn(ActiveEnum.Y);

        User existUser = userRepository.findOneByEmail(emailConfirm.getEmail());
        if (existUser == null) {
            UserDTO.ResEmailConfirm resEmailConfirm = new UserDTO.ResEmailConfirm();
            resEmailConfirm.setTitle("이메일 인증에 실패하였습니다.");
            resEmailConfirm.setMsg("존재하지 않는 유저입니다. 다시한번 확인해주세요.");
            resEmailConfirm.setUrl("/regist");
            resEmailConfirm.setUrlTitle("가입하기");
            return resEmailConfirm;
        }

        if (ActiveEnum.Y.equals(existUser.getActive())) {
            UserDTO.ResEmailConfirm resEmailConfirm = new UserDTO.ResEmailConfirm();
            resEmailConfirm.setTitle("이메일 인증에 실패하였습니다.");
            resEmailConfirm.setMsg("이미 인증된 유저입니다. 다시한번 확인해주세요.");
            resEmailConfirm.setUrl("/login");
            resEmailConfirm.setUrlTitle("로그인하기");
            return resEmailConfirm;
        }

        existUser.setActive(ActiveEnum.Y);

        UserDTO.ResEmailConfirm resEmailConfirm = new UserDTO.ResEmailConfirm();
        resEmailConfirm.setTitle("이메일 인증에 성공하였습니다.");
        resEmailConfirm.setMsg("축하드립니다. 로그인하시면, Coinext를 사용하실수 있습니다.");
        resEmailConfirm.setUrl("/login");
        resEmailConfirm.setUrlTitle("로그인하기");

        //create wallet
        List<Coin> coins = coinRepository.findAll();
        for (Coin coin : coins) {
            walletService.precreateWallet(existUser.getId(), coin.getName());
        }
        return resEmailConfirm;
    }

    @SoftTransational
    public String doRegist(String email, String pwd, String fingerprintPayload) {

        User existUser = userRepository.findOneByEmail(email);
        if (existUser != null) {
            return "redirect:/regist?msg=already";
        }

        //user regist
        User user = new User();
        user.setEmail(email);
        user.setPwd(bCryptPasswordEncoder.encode(pwd));
        user.setActive(ActiveEnum.N);
        user.setDelDtm(null);
        user.setRegDtm(LocalDateTime.now());
        user.setLevel(LevelEnum.LEVEL1);
        user.setOtpHash(otpService.genSecretKey(user.getEmail() + "_" + user.getRegDtm() + "_" + user.getPwd()));
        userRepository.save(user);

        //finger print regist
        FingerPrint fingerPrint = new FingerPrint();
        fingerPrint.setUserId(user.getId());
        fingerPrint.setHashKey(bCryptPasswordEncoder.encode(fingerprintPayload));
        fingerPrint.setRegDtm(LocalDateTime.now());
        fingerPrint.setDelDtm(null);
        fingerPrint.setActive(ActiveEnum.Y);
        fingerPrintRepository.save(fingerPrint);

        //email confirm regist
        String code = KeyGenUtil.generateEmailConfirmNumericKey();
        String hashEmail = KeyGenUtil.generateHashEmail(user.getId(), user.getPwd(), user.getEmail());
        EmailConfirm existEmailConfirm = emailConfirmRepository.findOneByEmail(user.getEmail());
        EmailConfirm emailConfirm = new EmailConfirm();
        if (existEmailConfirm != null) {
            existEmailConfirm.setCode(code);
            existEmailConfirm.setRegDtm(LocalDateTime.now());
            existEmailConfirm.setSendYn(ActiveEnum.N);
            emailConfirm = existEmailConfirm;
        } else {
            emailConfirm.setHashEmail(hashEmail);
            emailConfirm.setCode(code);
            emailConfirm.setEmail(email);
            emailConfirm.setRegDtm(LocalDateTime.now());
            emailConfirm.setSendYn(ActiveEnum.N);
            emailConfirmRepository.save(emailConfirm);
        }

        //email send publishing.
        EmailConfirm bindEmailConfirm = new EmailConfirm();
        bindEmailConfirm.setHashEmail(emailConfirm.getHashEmail());
        bindEmailConfirm.setCode(emailConfirm.getCode());
        bindEmailConfirm.setEmail(emailConfirm.getEmail());
        mqPublisher.emailConfirmPublish(bindEmailConfirm);

        return "redirect:/regist?msg=succeed";
    }
}
