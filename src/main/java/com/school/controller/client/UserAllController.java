package com.school.controller.client;

import com.school.component.security.UserServiceImpl;
import com.school.model.Sign;
import com.school.model.User;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * description：
 * author：崔童
 * date：2020/10/11
 */
@Api(tags = {"客户端查询"})
@RestController
public class UserAllController {
    @Autowired
    private UserServiceImpl userService;
    @GetMapping("select")
    @ApiOperation(value = "查询所有学校", notes = "查询所有学校")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object list(@ApiParam(value = "分页，要第几页的数据", example = "1") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(value = "分页，该页数据要多少条", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<User> userList = userService.querySelectiveAllByPage(page, limit);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取列表成功！", userList);
    }

    @GetMapping("select/like")
    @ApiOperation(value = "学校信息模糊查询", notes = "模糊查询")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object list1(@ApiParam(value = "关键字", example = "1") @RequestParam(defaultValue = "1") String key,
                       @ApiParam(value = "分页，要第几页的数据", example = "1") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(value = "分页，该页数据要多少条", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<User> userList = userService.querySelectiveAllDim(key,page,limit);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取列表成功！", userList);
    }

    @GetMapping("select/like{schoolname}")
    @ApiOperation(value = "学校名称模糊查询", notes = "模糊查询")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object list2(@ApiParam(value = "关键字", example = "1") @RequestParam(defaultValue = "1") String key,
                       @ApiParam(value = "分页，要第几页的数据", example = "1") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(value = "分页，该页数据要多少条", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<User> userList = userService.querySelectiveBySchoolnameDim(key,page,limit);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取列表成功！", userList);
    }




}
