package com.school.service.impl;

import com.school.dao.UserMapper;
import com.school.exception.EmailNotFoundException;
import com.school.model.User;
import com.school.model.UserExample;
import com.school.utils.AssertUtil;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class EmailServiceImpl {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ExpiringMap<String, String> usernameToCodeMap = ExpiringMap.builder().variableExpiration().build();

    @Autowired
    private JavaMailSenderImpl javaMailSenderImpl;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 系统邮箱
     *
     * @param username
     * @return
     */
//    @Value("${spring.mail.username}")
//    private String systemEmail;

//    public String getSystemEmail() {
//        return systemEmail;
//    }
//
//    public void setSystemEmail(String systemEmail) {
//        this.systemEmail = systemEmail;
//    }

    public void sendVerificationCode(String username) throws EmailNotFoundException {
        //生成随机的验证码
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append((int) (Math.random() * 10));
        }
        usernameToCodeMap.put(username, code.toString(), 180, TimeUnit.SECONDS);
        StringBuilder context = new StringBuilder();
        context.append("XXX验证码: " + code).append("\n").append("三分钟内有效");

        send(username,"XXX验证码",context.toString());
    }


    public void resetPassword(String username,
                              String code,
                              String newPassword) {
        String codeInCache = usernameToCodeMap.get(username);
        AssertUtil.emailVerificationCodeNotNull(codeInCache, "验证码不存在！");
        AssertUtil.emailVerificationCodeEquals(code.trim().equals(codeInCache), "验证码错误！");

        //TODO 在这里修改数据库中密码
        User user = new User();
        user.setPassword(passwordEncoder.encode(newPassword));
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        userMapper.updateByExampleSelective(user, userExample);
    }


    public void modifySystemEmail(String username,
                                  String mailAuthorizationCode) {
        mailProperties.setUsername(username);
        mailProperties.setPassword(mailAuthorizationCode);
        javaMailSenderImpl = new JavaMailSenderImpl();
        applyProperties(mailProperties, javaMailSenderImpl);
    }

    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }
        if (!properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(this.asProperties(properties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }



    public void sendDefaultPassword(User user) throws Exception {
        send(user.getUsername(),"XXX临时默认密码",user.getPassword());
    }


    public void send(String to,String subject,String context) throws EmailNotFoundException{
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getUsername());//系统邮箱
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(context);
        simpleMailMessage.setTo(to);
        try {
            javaMailSenderImpl.send(simpleMailMessage);
        }catch (EmailNotFoundException e){
            throw e;
        }
    }
}
