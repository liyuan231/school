package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.dto.SimpleSignInfo;
import com.school.exception.SignNotFoundException;
import com.school.exception.UserNotFoundException;
import com.school.exception.UserSignCorrespondException;
import com.school.model.Sign;
import com.school.model.User;
import com.school.service.impl.SignServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Api(value = "高校信息管理，管理端管理签约", tags = {"管理端签约"})
@RestController("adminSignController")
@RequestMapping("/api/admin/sign")
public class AdminSignController {
    @Autowired
    SignServiceImpl signService;
    @Autowired
    UserServiceImpl userService;

    @ApiOperation(value = "添加一则签约", notes = "管理员手动添加签约")
    @PostMapping("/add/{signUserId}/{signedUserId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public String sign(@ApiParam(example = "1", value = "主动签约方") @PathVariable("signUserId") Integer signUserId,
                       @ApiParam(example = "2", value = "被签约方") @PathVariable("signedUserId") Integer signedUserId) {
        signService.sign(signUserId, signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "管理员添加签约成功", null);
    }




    @GetMapping("/list")
    @ApiOperation(value = "查询签约", notes = "主要用于管理端分页显示，也是为了支持管理端的搜索功能，搜索某一用户喜欢谁或被谁意向")
    public Object list(@ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(value = "page", required = false) Integer page,
                       @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(value = "pageSize", required = false) Integer pageSize,
                       @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                       @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<Sign> signList = signService.querySelective(page, pageSize, sort, order);
        List<SimpleSignInfo> simpleSignInfos = new LinkedList<>();
        for (Sign sign : signList) {
            SimpleSignInfo simpleSignInfo = new SimpleSignInfo();
            complete(sign, simpleSignInfo);
            simpleSignInfos.add(simpleSignInfo);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "获取签约列表成功！", simpleSignInfos);
    }

    @GetMapping("/search")
    @ApiOperation(value = "搜索", notes = "搜索签约名单")
    public String search(@ApiParam(example = "关键字", value = "关键字") @RequestParam("schoolName") String schoolName,
                         @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(value = "page", required = false) Integer page,
                         @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(value = "pageSize", required = false) Integer pageSize,
                         @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                         @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<Sign> signs = signService.querySelective(page, pageSize, sort, order);
        List<SimpleSignInfo> simpleSignInfos = chooseSign(signs, schoolName);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取该关键字学校的签约结果成功！",simpleSignInfos);
    }

    /**
     * 通过学校名获取该学校的签约
     *
     * @param signs
     * @param schoolName
     * @return
     */
    private List<SimpleSignInfo> chooseSign(List<Sign> signs, String schoolName) {
        List<SimpleSignInfo> chosenSigns = new LinkedList<>();
        for (Sign sign : signs) {
            User user1 = userService.findById(sign.getSignuserid());
            if (user1.getSchoolname().contains(schoolName)) {
                User user2 = userService.findById(sign.getSigneduserid());
                SimpleSignInfo simpleSignInfo = new SimpleSignInfo();
                simpleSignInfo.setSignId(sign.getId());
                simpleSignInfo.setSignTime(sign.getAddTime());
                simpleSignInfo.setSchoolName(user1.getSchoolname());
                simpleSignInfo.setSignedSchoolName(user2.getSchoolname());
                chosenSigns.add(simpleSignInfo);
            }
        }
        return chosenSigns;
    }

    private void complete(Sign sign, SimpleSignInfo simpleSignInfo) {
        simpleSignInfo.setSignId(sign.getId());
        User user1 = userService.findById(sign.getSignuserid());//主动签约的高校
        simpleSignInfo.setSchoolName(user1.getSchoolname());
        User user2 = userService.findById(sign.getSigneduserid());//被签约的高校的名称
        simpleSignInfo.setSignedSchoolName(user2.getSchoolname());
        simpleSignInfo.setSignTime(sign.getAddTime());
    }

    @PostMapping("/update/{id}/{signUserId}/{signedUserId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation(value = "更新一则签约", notes = "管理员可强制修改签约的内容，比如原本是我主动和女神签约，现在被改成了女神和我主动签约")
    public Object update(@ApiParam(example = "1", value = "要修改一则签约，肯定要有该签约的唯一标识，即他的id") @PathVariable("id") Integer
                                 id,
                         @ApiParam(example = "1", value = "那么对于该则记录你想把主动签约方改成谁") @PathVariable("signUserId") Integer signUserId,
                         @ApiParam(example = "1", value = "那么对于该则记录，被签约方想改成谁") @PathVariable("signedUserId") Integer signedUserId) throws
            UserSignCorrespondException, SignNotFoundException, UserNotFoundException {
        signService.update(id, signUserId, signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则签约成功!", null);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除一则签约", notes = "依据id删除一则签约")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Object delete(@PathVariable("id") Integer id) throws
            UserSignCorrespondException, SignNotFoundException, UserNotFoundException {
        signService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则签约成功!", null);
    }
//    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
//    @ApiOperation("获取所有签约")
//    public String all() {
//        List<Sign> users = signService.queryAll();
//        return ResponseUtil.build(HttpStatus.OK.value(), "获取所有签约信息成功！", users);
//    }
}
