package com.school.controller.client;

import com.school.component.security.UserServiceImpl;
import com.school.model.Likes;
import com.school.service.impl.LikeServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/like")
@Api(tags = {"客户端意向"})
public class LikeController {
    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private UserServiceImpl userService;

    /**
     * 传入当前用户喜欢的一方的userId
     *
     * @param likedUserId
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @PostMapping("/like")
    @ApiOperation(value = "表明意向（需登录）", notes = "用户表明意向,添加一则意向记录")
    public String like(@ApiParam(example = "1", value = "被表明意向的用户的id") @RequestParam("likedUserId") Integer likedUserId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.school.model.User userInDb = userService.findByUsername(user.getUsername());
        likeService.like(userInDb.getId(), likedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "表明意向成功！", null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询意向（需讨论！！！）", notes = "主要用于用户端分页显示，也是为了支持用户端的搜索功能，搜索某一用户喜欢谁或被谁意向")
    public Object list(@ApiParam(example = "1", value = "想要查询的该则意向的id") @RequestParam(value = "id", required = false) Integer id,
                       @ApiParam(example = "1", value = "用户的id，使用用户id查询她都对谁有过意向") @RequestParam(value = "likeUserId", required = false) Integer likeUserId,
                       @ApiParam(example = "1", value = "用户的id，使用用户id查询都有谁对他有意向") @RequestParam(value = "likedUserId", required = false) Integer likedUserId,
                       @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(defaultValue = "1") Integer page,
                       @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(defaultValue = "10") Integer limit,
                       @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time") String sort,
                       @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc") String order) {
        List<Likes> list = likeService.querySelective(id, likeUserId, likedUserId, page, limit, sort, order);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取意向列表成功！", list);
    }

//    @ApiOperation(value = "更新一则意向",notes = "用户不小心点错了意向的学校")
//    @PostMapping("/update")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
//    public Object update(@RequestBody Likes like) {
//        likeService.update(like);
//        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则意向成功！", null);
//    }

    @ApiOperation(value = "删除一则意向", notes = "用户删除一则意向")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    @DeleteMapping("/delete/{id}")
    public Object delete(@ApiParam(example = "1",value = "将被删除的那则意向的id") @PathVariable("id") Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.school.model.User userInDb = userService.findByUsername(user.getUsername());
        likeService.deleteAndCheck(userInDb.getId(), id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则意向成功！", null);
    }

    @ApiOperation(value = "匹配", notes = "看看谁对我有意向")
    @GetMapping("/match")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','USER')")
    public Object match() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.school.model.User userInDb = userService.findByUsername(user.getUsername());
        List<Likes> matchs = likeService.matchByLikedUserId(userInDb.getId());
        return ResponseUtil.build(HttpStatus.OK.value(), "匹配意向成功！(她们为互相有意向，只不过仅结果集取其中一条，相当于去重)", matchs);
    }

}
