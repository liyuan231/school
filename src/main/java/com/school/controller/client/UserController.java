package com.school.controller.client;

import com.school.component.security.UserServiceImpl;
import com.school.exception.*;
import com.school.model.Pics;
import com.school.model.User;
import com.school.service.impl.EmailServiceImpl;
import com.school.service.impl.PicsServiceImpl;
import com.school.utils.AssertUtil;
import com.school.utils.FileEnum;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(tags = {"客户端用户"}, value = "客户端")
@RestController
@RequestMapping("/api/client/user")
public class UserController {
    @Autowired
    private EmailServiceImpl emailService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${file.path}")
    private String filePath;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PicsServiceImpl picsService;
    @Value("${spring.file.path}")
    private String springFilePath;

    /**
     * 依据 retrieveVerificationCode发过去的验证码重新设置密码
     */
    @PostMapping("/resetPassword")
    @ApiOperation(value = "用户重置密码", notes = "需要username以及发给该账号邮箱的code（时限3分钟），以及newPassword")
    public String resetPassword(@ApiParam(example = "123@qq.com", value = "待重置密码的用户名，即邮箱号") @RequestParam("userName") String username,
                                @ApiParam(example = "123", value = "新密码，前端加过密的") @RequestParam("newPassword") String newPassword,
                                @ApiParam(example = "1234", value = "发给该用户邮箱的验证码") @RequestParam("code") String code,
                                HttpServletRequest request) throws EmailVerificationCodeNullPointerException, EmailWrongFormatException, UsernameNullPointerException, EmailVerificationCodeIllegalArgumentException {
        logger.info("[" + request.getRemoteAddr() + "] " + "is resetting his password!");
        AssertUtil.isValidMail(username, "用户名邮箱格式有误！");
        AssertUtil.emailVerificationCodeNotNull(code, "验证码不应为空！");
        Assert.notNull(newPassword, "新密码不应为空！");
        emailService.resetPassword(username, code, newPassword);
        return ResponseUtil.build(HttpStatus.OK.value(), "重设密码成功！", null);
    }

    @PostMapping("/forgetPassword")
    @ApiOperation(value = "用户忘记密码", notes = "传入一个邮箱账号username")
    public String retrieveVerificationCode(@ApiParam(example = "123@qq.com", value = "谁忘记了密码（用户名）") @RequestParam String username,
                                           HttpServletRequest request) throws EmailNotFoundException, UsernameNullPointerException, EmailWrongFormatException {
        logger.info("[" + request.getRemoteAddr() + "] is retrieving a verificationCode for his account!");
        AssertUtil.usernameNotNull(username, "邮箱号不应为空！");
        //TODO 这里可以进行校验邮箱是否合法
        AssertUtil.isValidMail(username, "邮箱格式错误!");
        emailService.sendVerificationCode("签约系统验证码", "签约系统验证码（3分钟内有效）", username, 180, TimeUnit.SECONDS);//忘记密码
        return ResponseUtil.build(HttpStatus.OK.value(), "获取邮箱验证码成功！", null);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @ApiOperation(value = "上传头像", notes = "用户上传头像")
    public Object upload(@ApiParam(example = "file", value = "该文件流,参数名应该为file") @RequestParam("file") MultipartFile file) throws IOException, FileFormattingException, UserNotFoundException {
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            AssertUtil.isPicture(format);
            //上传图片并插入数据库中
            upload(file, format, FileEnum.AVATAR_URL);
            return ResponseUtil.build(HttpStatus.OK.value(), "上传图片成功！", null);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "图片为空！", null);
    }

    private void upload(MultipartFile file, String format, FileEnum fileEnum) throws IOException, UserNotFoundException {
        User user = userService.retrieveUserByToken();
        picsService.add(user, file, fileEnum);
    }

    @PostMapping("/uploadLogoAndSignature")
    @ApiOperation(value = "上传学校logo以及校长签章", nickname = "用户上传学校logo以及校长签章")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object uploadLogoAndSignature(@RequestParam("files") List<MultipartFile> files) throws IOException, UserNotFoundException, FileFormattingException {
        if (files.size() != 0) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                AssertUtil.isPicture(format);
                User user = userService.retrieveUserByToken();
                //TODO
                List<Pics> picsToLogoAndSignature = picsService.findByUserId(user.getId(), FileEnum.LOGO);
                if (picsToLogoAndSignature.size() != 0) {
                    for (Pics pics : picsToLogoAndSignature) {
                        picsService.delete(pics);
                    }
                }
                //TODO
                upload(file, format, FileEnum.LOGO);
            }

            return ResponseUtil.build(HttpStatus.OK.value(), "上传图片成功！", null);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "上传文件为空！", null);

    }


    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @ApiOperation(value = "获取当前用户信息", notes = "获取用户信息")
    @GetMapping("/retrieveUserInfo")
    public Object retrieveUserInfo() {
        User userInDb = userService.retrieveUserByToken();
        userInDb.setPassword("[PASSWORD]");
        userInDb.setAvatarurl(springFilePath + userInDb.getAvatarurl());
//        List<Pics> avatarUrls = picsService.findByUserId(userInDb.getId(), FileEnum.AVATAR_URL);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取用户信息成功!", userInDb);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @ApiOperation(value = "④　参会学校展示", notes = "，获取所有的用户信息，")
    @GetMapping("/retrieveAllUserInfo")
    public Object retrieveAllUserInfo() {
        List<User> users = userService.queryAll();
        clearPassword(users);
        updateAvatarUrl(users);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取用户信息成功!", users);
    }

    private void updateAvatarUrl(List<User> users) {
        for (User user : users) {
            user.setAvatarurl(springFilePath + user.getAvatarurl());
        }
    }

    private void clearPassword(List<User> users) {
        for (User user : users) {
            user.setPassword("[PASSWORD]");
        }
    }


}
