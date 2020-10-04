package com.school.controller.client;

import com.school.exception.EmailNotFoundException;
import com.school.service.impl.EmailServiceImpl;
import com.school.utils.AssertUtil;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags={"客户端用户"},value="客户端")
@RestController
@RequestMapping("/api/client/user")
public class UserController {
    @Autowired
    private EmailServiceImpl emailService;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 依据 retrieveVerificationCode发过去的验证码重新设置密码
     */
    @PostMapping("/resetPassword")
    @ApiOperation(value = "用户重置密码",notes = "需要username以及发给该账号邮箱的code（时限3分钟），以及newPassword")
    public String resetPassword(@ApiParam(example = "123@qq.com",value = "待重置密码的用户名，即邮箱号") @RequestParam("userName")String username,
                                @ApiParam(example = "123",value = "新密码，前端加过密的")@RequestParam("newPassword")String newPassword,
                                @ApiParam(example = "1234",value = "发给该用户邮箱的验证码")@RequestParam("code")String code,
                                HttpServletRequest request) {
        logger.info("[" + request.getRemoteAddr() + "] " + "is resetting his password!");
        AssertUtil.isValidMail(username, "用户名邮箱格式有误！");
        AssertUtil.emailVerificationCodeNotNull(code, "验证码不应为空！");
        Assert.notNull(newPassword, "新密码不应为空！");
        emailService.resetPassword(username, code, newPassword);
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.OK.value());
    }

    @PostMapping("/forgetPassword")
    @ApiOperation(value = "用户忘记密码",notes = "传入一个邮箱账号username")
    public String retrieveVerificationCode(@ApiParam(example = "123@qq.com",value = "谁忘记了密码（用户名）")@RequestParam String username,
                                           HttpServletRequest request) throws EmailNotFoundException {
        logger.info("[" + request.getRemoteAddr() + "] is retrieving a verificationCode for his account!");
        AssertUtil.usernameNotNull(username, "邮箱号不应为空！");
        //TODO 这里可以进行校验邮箱是否合法
        AssertUtil.isValidMail(username, "邮箱格式错误!");
        emailService.sendVerificationCode(username);
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.OK.value());
    }
}
