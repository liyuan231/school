package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//@Api(tags = {"下载文件"}, value = "文件下载统一管理")
//@Controller
//@ResponseBody
//@RequestMapping("/api/admin")
public class AdminDownloadController {
//    @Autowired
    UserServiceImpl userService;


}
