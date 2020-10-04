package com.school.controller.admin;

import com.school.model.Sign;
import com.school.service.impl.SignServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "管理端管理签约",tags = {"管理端签约"})
@RestController("adminSignController")
@RequestMapping("/api/admin/sign")
public class AdminSignController {
    @Autowired
    SignServiceImpl signService;

    @ApiOperation(value = "添加一则签约", notes = "管理员手动添加签约")
    @PostMapping("/add/{signUserId}/{signedUserId}")
    public String sign(@ApiParam(example = "1", value = "主动签约方") @PathVariable("signUserId") Integer signUserId,
                       @ApiParam(example = "2", value = "被签约方") @PathVariable("signedUserId") Integer signedUserId) {
        signService.sign(signUserId, signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "管理员添加签约成功", null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询签约", notes = "主要用于管理端分页显示，也是为了支持管理端的搜索功能，搜索某一用户喜欢谁或被谁意向")
    public Object list(@ApiParam(example = "1", value = "查询该则签约的id") Integer id,
                       @ApiParam(example = "1", value = "查询该用户主动和谁签过约") @RequestParam(value = "signUserId", required = false) Integer signUserId,
                       @ApiParam(example = "2", value = "查询该用户被动被谁签约过") @RequestParam(required = false, value = "signedUserId") Integer signedUserId,
                       @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(defaultValue = "10") Integer limit,
                       @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                       @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<Sign> signList = signService.querySelective(id, signUserId, signedUserId, page, limit, sort, order);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取签约列表成功！", signList);
    }

    @PostMapping("/update/{id}/{signUserId}/{signedUserId}")
    @ApiOperation(value = "更新一则签约", notes = "管理员可强制修改签约的内容，比如原本是我主动和女神签约，现在被改成了女神和我主动签约")
    public Object update(@ApiParam(example = "1",value = "要修改一则签约，肯定要有该签约的唯一标识，即他的id")@PathVariable("id") Integer id,
                         @ApiParam(example = "1",value = "那么对于该则记录你想把主动签约方改成谁")@PathVariable("signUserId") Integer signUserId,
                         @ApiParam(example = "1",value = "那么对于该则记录，被签约方想改成谁")@PathVariable("signedUserId") Integer signedUserId) {
        signService.update(id,signUserId,signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则签约成功!", null);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除一则签约",notes = "依据id删除一则签约")
    public Object delete(@PathVariable("id") Integer id) {
        signService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则签约成功!", null);
    }
}
