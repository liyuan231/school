package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.exception.UsernameAlreadyExistException;
import com.school.model.User;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"管理端用户"}, value = "管理端管理用户")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserServiceImpl userService;


    @GetMapping("/list")
    @ApiOperation(value = "查询用户(讨论)", notes = "主要用于管理端分页显示，也是为了支持管理端的搜索功能，搜索某一用户")
    public String listSchoolsInfo(@ApiParam(example = "1", value = "依据用户id查询该用户,就是点击该用户，就会获得该用户的详细信息") @RequestParam(value = "id", required = false) Integer userId,
                                  @ApiParam(example = "1@qq.com", value = "依据用户名，即邮箱号查询该用户") @RequestParam(value = "userName", required = false) String username,
                                  @ApiParam(example = "广东外语外贸", value = "依据学校名查询该学校") @RequestParam(value = "schoolName", required = false) String schoolName,
                                  @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(defaultValue = "1") Integer page,
                                  @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(defaultValue = "10") Integer limit,
                                  @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                                  @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<User> users = userService.querySelective(userId, username, schoolName, page, limit, sort, order);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取高校信息成功！", users);
    }

    @GetMapping("/all")
    @ApiOperation("获取所有用户")
    public String all() {
        List<User> users = userService.queryAll();
        return ResponseUtil.build(HttpStatus.OK.value(), "获取高校信息成功！", users);
    }


    @PostMapping("/add")
    @ApiOperation(value = "添加一个用户（讨论）", notes = "后台手动添加一个用户，添加一个用户至少要有userName,schoolName,password")
    public Object create(@ApiParam(example = "123@qq.com", value = "用户名即为邮箱号") @RequestParam("userName") String username,
                         @ApiParam(example = "123456", value = "前端加过密的密码") @RequestParam("password") String password,
                         @ApiParam(example = "GDUFS", value = "学校名") @RequestParam("schoolName") String schoolName,
                         @ApiParam(example = "2", value = "用户等级，1为管理员，2为普通用户，默认为2") @RequestParam(value = "level", defaultValue = "2") Integer level) throws UsernameAlreadyExistException {
        userService.add(username, password, schoolName, level);
        return ResponseUtil.build(HttpStatus.OK.value(), "添加一个用户成功!", null);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除用户", notes = "依据传入的id删除用户")
    public Object delete(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一个用户成功!", null);

    }

    @PostMapping("/update")
    @ApiOperation(value = "更新用户信息（讨论）", notes = "更新用户信息,用户名，即邮箱是否应该修改？")
    public Object update(@ApiParam(example = "1", value = "待修改的用户的id") @RequestParam("id") Integer id,
                         @ApiParam(example = "123456", value = "待修改的用户的密码") @RequestParam("password") String password,
                         @ApiParam(example = "广外", value = "待修改的用户的学校名") @RequestParam("schoolName") String schoolName,
                         @ApiParam(example = "2", value = "用户的等级，1-》管理员，2-》普通用户") @RequestParam(required = false, value = "level", defaultValue = "2") Integer level) {
        userService.update(id, password, schoolName, level);
        return ResponseUtil.build(HttpStatus.OK.value(), "修改一个用户成功!", null);
    }

}
