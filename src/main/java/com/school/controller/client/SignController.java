package com.school.controller.client;

import com.school.model.Sign;
import com.school.service.impl.SignServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"客户端签约"})
@RestController
@RequestMapping("/api/client/sign")
public class SignController {
    @Autowired
    SignServiceImpl signService;

    /**
     * 传入被签约一方的userId
     *
     * @param signedUserId
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','USER')")
    @ApiOperation("Authorization:Bearer token(header有这才可调用此方法，传入当前用户意向的用户的id)表明当前用户与该用户所选的用户进行签约")
    @GetMapping("/sign")
    public String sign(@RequestParam("signedUserId") Integer signedUserId) {
        signService.sign(signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "表明心意成功！", null);
    }

    @GetMapping("/list")
    @ApiOperation("强大的查询，全部字段可选，默认page=1，limit=10")
    public Object list(Integer id,
                       @RequestParam(value = "signUserId", required = false) Integer signUserId,
                       @RequestParam(required = false, value = "signedUserId") Integer signedUserId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order) {
        List<Sign> signList = signService.querySelective(id, signUserId, signedUserId, page, limit, sort, order);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取签约列表成功！", signList);
    }

    @PostMapping("/update")
    @ApiOperation("更新，仅id字段必传")
    public Object update(@RequestBody Sign sign) {
        signService.update(sign);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则签约成功!", null);
    }

    @PostMapping("/create")
    @ApiOperation("往数据库插入一则签约记录")
    public Object create(@RequestBody Sign sign) {
        signService.add(sign);
        return ResponseUtil.build(HttpStatus.OK.value(), "增加一则签约成功!", null);
    }

    @PostMapping("/delete")
    @ApiOperation("删除一则签约")
    public Object delete(@RequestBody Sign sign) {
        signService.delete(sign);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则签约成功!", null);
    }
}
