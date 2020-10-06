package com.school.controller.client;

import com.school.component.security.UserServiceImpl;
import com.school.exception.*;
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

@Api(tags = {"客户端签约"})
@RestController
@RequestMapping("/api/client/sign")
public class SignController {
    @Autowired
    private SignServiceImpl signService;
    @Autowired
    private UserServiceImpl userService;

    /**
     * 传入被签约一方的userId
     *
     * @param signedUserId
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @ApiOperation(value = "签约", notes = "登录后才可调用，当前用户和某一用户进行签约")
    @GetMapping("/sign/{signedUserId}")
    public String sign(@ApiParam(value = "被签约方的id", example = "1") @PathVariable("signedUserId") Integer signedUserId) throws UserNotFoundException, SignAlreadyExistException, SignNotCorrectException {
        signService.sign(signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "签约成功！", null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询", notes = "查询签约")
    public Object list(@ApiParam(value = "待查询签约的id", example = "1") @RequestParam(value = "id",required = false) Integer id,
                       @ApiParam(value = "查询某一用户主动和谁签过约", example = "1") @RequestParam(value = "signUserId", required = false) Integer signUserId,
                       @ApiParam(value = "查询某一用户被动和谁签过约", example = "2") @RequestParam(required = false, value = "signedUserId") Integer signedUserId,
                       @ApiParam(value = "分页，要第几页的数据", example = "1") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(value = "分页，该页数据要多少条", example = "10") @RequestParam(defaultValue = "10") Integer limit,
                       @ApiParam(value = "排序，依照什么进行排序，add_time或update_time", example = "add_time") @RequestParam(defaultValue = "add_time") String sort,
                       @ApiParam(value = "排序，升序(asc)或降序(desc)", example = "desc") @RequestParam(defaultValue = "desc") String order) {
        List<Sign> signList = signService.querySelective(id, signUserId, signedUserId, page, limit, sort, order);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取签约列表成功！", signList);
    }

    @PostMapping("/update/{id}/{signUserId}/{signedUserId}")
    @ApiOperation(value = "更新", notes = "依据id更新一则签约")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object update(@ApiParam(example = "1", value = "该则签约的id") @PathVariable("id") Integer id,
                         @ApiParam(example = "1", value = "该则签约中主动签约的用户的id") @PathVariable("signUserId") Integer signUserId,
                         @ApiParam(example = "2", value = "该则签约中被签约的用户id") @PathVariable("signedUserId") Integer signedUserId) throws UserSignCorrespondException, SignNotFoundException, UserNotFoundException {
        signService.update(id, signUserId, signedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则签约成功!", null);
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除", notes = "依据id删除一则签约")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object delete(@PathVariable("id") Integer id) throws UserSignCorrespondException, SignNotFoundException, UserNotFoundException {
        signService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则签约成功!", null);
    }

    @GetMapping("/listBySignUserId")
    @ApiOperation(value = "查询我主动签约的用户", notes = "查询我主动签约的用户")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object queryBySignUserId() {
        List<Sign> signs = signService.findBySignUserId();
        List<User> users = new LinkedList<>();
        for (Sign sign : signs) {
            User user = userService.findById(sign.getSigneduserid());
            users.add(user);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "获取我主动签约记录成功！", users);
    }

    @GetMapping("/listBySignedUserId")
    @ApiOperation(value = "查询主动和我签约的用户", notes = "查询主动和我签约的用户（我是被动）")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object queryBySignedUserId() {
        List<Sign> signs = signService.findBySignedUserId();
        List<User> users = new LinkedList<>();
        for (Sign sign : signs) {
            User user = userService.findById(sign.getSignuserid());
            users.add(user);
        }
        return ResponseUtil.build(HttpStatus.OK.value(), "查询主动和我签约的用户成功！", users);
    }
}
