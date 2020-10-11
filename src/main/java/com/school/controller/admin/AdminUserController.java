package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.dto.SimpleUserInfo;
import com.school.dto.SimpleUserInfoPlus;
import com.school.exception.*;
import com.school.model.Pics;
import com.school.model.User;
import com.school.service.impl.PicsServiceImpl;
import com.school.utils.FileEnum;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 至此，管理端用户部分应是写完了
 */
@Api(tags = {"高校信息管理，管理端用户"}, value = "管理端管理用户")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PicsServiceImpl picsService;

    @Value("${spring.file.path}")
    private String springFilePath;

    //
//    @GetMapping("/list")
//    @ApiOperation(value = "查询用户(讨论)", notes = "主要用于管理端分页显示，也是为了支持管理端的搜索功能，搜索某一用户")
//    public String listSchoolsInfo(@ApiParam(example = "1", value = "依据用户id查询该用户,就是点击该用户，就会获得该用户的详细信息") @RequestParam(value = "id", required = false) Integer userId,
//                                  @ApiParam(example = "1@qq.com", value = "依据用户名，即邮箱号查询该用户") @RequestParam(value = "userName", required = false) String username,
//                                  @ApiParam(example = "广东外语外贸", value = "依据学校名查询该学校") @RequestParam(value = "schoolName", required = false) String schoolName,
//                                  @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(required = false) Integer page,
//                                  @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(required = false) Integer limit,
//                                  @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
//                                  @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
//        List<User> users = userService.querySelective(userId, username, schoolName, page, limit, sort, order);
//        userService.clearPassword(users);
//        return ResponseUtil.build(HttpStatus.OK.value(), "获取高校信息成功！", users);
//    }
    @GetMapping("/list")
    @ApiOperation(value = "查询列表（分页）", notes = "管理端分页显示所有用户")
    public String list(@ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(value = "page", required = false) Integer page,
                       @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(value = "pageSize", required = false) Integer limit,
                       @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                       @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<User> userList = userService.querySelective(page, limit, sort, order);
        List<SimpleUserInfo> list = new LinkedList<>();
        for (User user : userList) {
            SimpleUserInfo simpleUserInfo = new SimpleUserInfo();
            complete(user, simpleUserInfo);
            list.add(simpleUserInfo);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "获取第" + page + "页列表信息成功!", list);
    }

    private void complete(User user, SimpleUserInfo simpleUserInfo) {
        simpleUserInfo.setId(user.getId());
        simpleUserInfo.setAddress(user.getAddress());
        simpleUserInfo.setContact(user.getContact());
        simpleUserInfo.setEmail(user.getEmail());
        simpleUserInfo.setTelephone(user.getTelephone());
        simpleUserInfo.setUsername(user.getUsername());
        simpleUserInfo.setSchoolName(user.getSchoolname());
    }

    @GetMapping("/show/{userId}")
    @ApiOperation(value = "通过id查询某一用户", notes = "通过id查询某一用户")
    public String show(@PathVariable("userId") Integer id) {
        User user = userService.findById(id);
        List<Pics> logos = picsService.findByUserId(user.getId(), FileEnum.LOGO);
        String logo = logos.size() == 0 ? null : springFilePath + logos.get(0).getLocation();
        List<Pics> signatures = picsService.findByUserId(user.getId(), FileEnum.SIGNATURE);
        String signature = signatures.size() == 0 ? null : springFilePath + signatures.get(0).getLocation();
        SimpleUserInfoPlus simpleUserInfoPlus = new SimpleUserInfoPlus();
        complete(user, simpleUserInfoPlus);
        simpleUserInfoPlus.setSchoolLogo(logo);
        simpleUserInfoPlus.setSchoolSignature(signature);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取用户信息成功!", simpleUserInfoPlus);
    }

    @PostMapping("/add")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation(value = "导入单个高校信息", notes = "后台手动添加一个用户")
    public Object create(@ApiParam(example = "123@qq.com", value = "用户名即为邮箱号") @RequestParam("username") String username,
                         @ApiParam(example = "123456", value = "前端加过密的密码") @RequestParam(value = "password", required = false) String password,
                         @ApiParam(example = "GDUFS", value = "学校名") @RequestParam(value = "schoolName", required = false) String schoolName,
                         @ApiParam(example = "人名", value = "联系人") @RequestParam(value = "contact", required = false) String contact,
                         @ApiParam(example = "地址", value = "学校详细地址") @RequestParam(value = "address", required = false) String address,
                         @ApiParam(example = "111", value = "电话号码") @RequestParam(value = "telephone", required = false) String telephone,
                         @ApiParam(example = "111", value = "学校代码") @RequestParam(value = "schoolCode", required = false) String schoolCode,
                         @ApiParam(example = "11@qq.com", value = "学校邮箱") @RequestParam(value = "email", required = false) String email,
                         @ApiParam(example = "教授",value = "职务")@RequestParam(value = "profession",required = false)String profession) throws UsernameAlreadyExistException {
        userService.add(username, password, schoolName, contact, address, telephone, schoolCode, email,profession);
        return ResponseUtil.build(HttpStatus.OK.value(), "添加一个用户成功!", null);
    }


    @DeleteMapping("/delete/{id}")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation(value = "删除用户", notes = "依据传入的id删除用户")
    public Object delete(@PathVariable("id") Integer id) throws UserNotFoundException {
        userService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一个用户成功!", null);
    }

    @PostMapping("/update/{userId}")
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息,用户名，即邮箱是否应该修改？")
    public Object update(@ApiParam(example = "1", value = "待修改的用户的id") @PathVariable("userId") Integer id,
                         @ApiParam(example = "广外", value = "待修改的用户的学校名") @RequestParam(value = "schoolName", required = false) String schoolName,
                         @ApiParam(example = "联系人", value = "联系人") @RequestParam(value = "contact", required = false) String contact,
                         @ApiParam(example = "详细地址", value = "详细地址") @RequestParam(value = "address", required = false) String address,
                         @ApiParam(example = "电话", value = "电话") @RequestParam(value = "telephone", required = false) String telephone,
                         @ApiParam(example = "邮箱", value = "邮箱") @RequestParam(value = "email", required = false) String email) throws UserNotFoundException {
        User update = userService.update(id, schoolName, contact, address, telephone, email);
        return ResponseUtil.build(HttpStatus.OK.value(), "修改一个用户成功!", update);
    }


    @PostMapping("/upload/logo/{userId}")
    @ApiOperation(value = "上传logo", notes = "管理端上传某一学校logo")
    public String uploadLogo(@RequestParam("logo") MultipartFile file,
                             @ApiParam(example = "1", value = "被上传图片的用户的id") @PathVariable("userId") Integer userId) throws IOException {
        if (file.isEmpty()) {
            return ResponseUtil.build(HttpStatus.BAD_REQUEST.value(), "上传文件不能为空！", null);
        }
        Pics upload = picsService.upload(userId, FileEnum.LOGO, file);
        return ResponseUtil.build(HttpStatus.OK.value(), "上传学校logo成功！", springFilePath + upload.getLocation());
    }

    @PostMapping("/upload/signature/{userId}")
    @ApiOperation(value = "上传校长签章", notes = "管理端上传校长签章")
    public String uploadSignature(@RequestParam("signature") MultipartFile file,
                                  @ApiParam(example = "1", value = "被上传图片的用户的id") @PathVariable("userId") Integer userId) throws IOException {
        if (file.isEmpty()) {
            return ResponseUtil.build(HttpStatus.BAD_REQUEST.value(), "上传文件不能为空！", null);
        }
        Pics upload = picsService.upload(userId, FileEnum.SIGNATURE, file);
        return ResponseUtil.build(HttpStatus.OK.value(), "上传校长签章成功！", springFilePath + upload.getLocation());
    }


    @PostMapping("/openLogin")
    @ApiOperation(value = "开放登录", notes = "开放用户可以登录")
    public String openLogin() {
        userService.openLogin();
        return ResponseUtil.build(HttpStatus.OK.value(), "开放所有用户登录", null);
    }


    @PostMapping("/importRegistrationForm")
    @ApiOperation(value = "导入报名表", notes = "但对excel的字段名有严格要求，仅支持.xls以及.xlsx，请直接和我讨论这一块")
    public String uploadFile(@ApiParam(value = "导入的excel文件", example = "test.xlsx") @RequestParam("registrationForm") MultipartFile file,
                             HttpServletRequest request) throws Exception, FileFormattingException, EmailNotFoundException, ExcelDataException, UsernameAlreadyExistException {

        if (file.isEmpty()) {
            return ResponseUtil.build(HttpStatus.BAD_REQUEST.value(), "文件不能为空！", null);
        }
        userService.importRegistrationForm(file);
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "录入excel数据成功！");
    }

    @ApiOperation(value = "导出报名表", notes = "导出报名表(swagger-bootstarp无法下载，会直接显示内容，因此要测试可以直接浏览器访问该地址)")
    @GetMapping("/exportRegistrationForm")
    public void exportRegistrationForm(HttpServletResponse response) throws IOException {
        Workbook workbook = userService.exportRegistrationForm();
        String fileName = User.class.getSimpleName() + ".xls";
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }


    @GetMapping("/user/search")
    @ApiOperation(value = "搜索", notes = "输入搞高校名进行搜索")
    public String search(@RequestParam(required = false) String schoolName,
                         @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(value = "page", required = false) Integer page,
                         @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(value = "pageSize", required = false) Integer limit,
                         @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                         @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<User> users = userService.querySelective(schoolName, page, limit, sort, order);
        userService.clearPassword(users);
        List<SimpleUserInfo> userInfos = new LinkedList<>();
        for (User user : users) {
            SimpleUserInfo simpleUserInfo = new SimpleUserInfo();
            complete(user, simpleUserInfo);
            userInfos.add(simpleUserInfo);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "搜索成功！", userInfos);
    }


}
