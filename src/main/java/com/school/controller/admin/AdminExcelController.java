package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.model.User;
import com.school.utils.AssertUtil;
import com.school.utils.FileUtil;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/file")
@Api(tags = {"管理端导入导出用户信息"}, value = "管理端导入导出用户的信息，导入仅支持xls以及xlsx，且excel字段需符合要求，请直接和我沟通，导出目前仅支持json")
public class AdminExcelController {
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入用户信息", notes = "，但对excel的字段名有严格要求，仅支持.xls以及.xlsx，请直接和我讨论这一块，支持多文件传入，就是一个表单有多个文件的input框")
    public String uploadFile(@ApiParam(value = "导入的excel文件",example = "test.xlsx") @RequestParam("file") List<MultipartFile> files,
                             HttpServletRequest request) throws Exception {
        int count = 0;
        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isEmpty()) {
                count++;
                String originalFilename = files.get(i).getOriginalFilename();//原本文件的名字
                String format = originalFilename.substring(originalFilename.lastIndexOf("."));
                AssertUtil.isExcel(format);
                fileUtil.importExcelToDb(files.get(i), format);
            }
        }
        if (count == 0) {
            return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "upload file should not be null!");
        }
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "录入excel数据成功！");
    }

    @GetMapping("/retrieveAllUsers")
    @ApiOperation(value = "导出用户信息（未实现excel）", notes = "此处应该生成excel并返回该文件，然而对此块有些不熟，就先直接返回所有的用户的json数据，和 api/admin/user/all 作用一样")
    public String retrieveAllUsers() throws IOException {
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("utf-8");
        List<User> users = userService.queryAll();
        return ResponseUtil.build(HttpStatus.OK.value(), "获取所有高校用户信息成功！", users);
    }
}
