package com.school.controller.admin;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员相关的邮件的接口，与用户的EmailController分开
 */
@RestController
@RequestMapping("/api/admin/email")
@Api(tags = {"修改系统邮箱"})
public class AdminEmailController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    EmailServiceImpl emailService;

    @PostMapping("/modifySystemEmail")
    @ApiOperation("修改系统邮箱")
    public String modifySystemEmail(@ApiParam(example = "123@qq.com", value = "系统邮箱账号") @RequestParam("userName") String username,
                                    @ApiParam(example = "123456", value = "开启POP3后获得的授权码") @RequestParam("code") String code,
                                    HttpServletRequest request) {
        logger.info("[" + request.getRemoteAddr() + "] 管理员修改默认系统邮箱！");
        Assert.notNull(code, "管理端设置系统的邮箱的授权码不应为空！");
        AssertUtil.isValidMail(username, "设置的系统邮箱不能为空！");
        emailService.modifySystemEmail(username, code);
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.OK.value());
    }
}
