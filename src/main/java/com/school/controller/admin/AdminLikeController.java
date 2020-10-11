package com.school.controller.admin;

import com.school.exception.*;
import com.school.model.Likes;
import com.school.model.Sign;
import com.school.service.impl.LikeServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminLikeController")
@RequestMapping("/api/admin/like")
@Api(value = "管理端管理意向", tags = "管理端意向")
public class AdminLikeController {
    @Autowired
    private LikeServiceImpl likeService;

    /**
     * 传入当前用户喜欢的一方的userId
     *
     * @param likedUserId
     * @return
     */
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @PostMapping("/add/{likeUserId}/{likedUserId}")
    @ApiOperation(value = "添加一则意向", notes = "管理端手动牵线")
    public String like(@ApiParam(example = "2", value = "被喜欢的用户", required = true) @PathVariable("likedUserId") Integer likedUserId,
                       @ApiParam(example = "1", value = "主动去喜欢其他用户的用户", required = true) @PathVariable("likeUserId") Integer likeUserId) throws UserNotFoundException, UserNotCorrectException, LikesAlreadyExistException {
        likeService.like(likeUserId, likedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "管理端手动牵线成功！", null);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询意向（需讨论）", notes = "主要用于管理端分页显示，也是为了支持管理端的搜索功能，搜索某一用户喜欢谁或被谁意向")
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


    @PostMapping("/update/{id}/{likeUserId}/{likedUserId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation(value = "更新一则意向", notes = "管理端修改某一则意向，比如现在我喜欢女神，现在管理员强制修改成女神喜欢我")
    public Object update(@ApiParam(example = "1", value = "待修改的意向的id") @PathVariable("id") Integer id,
                         @ApiParam(example = "1", value = "主动去喜欢的用户id")@PathVariable("likeUserId") Integer likeUserId,
                         @ApiParam(example = "2", value = "被喜欢的用户的id")@PathVariable("likedUserId") Integer likedUserId) throws UserLikesNotCorrespondException, LikesNotFoundException {
        likeService.update(id, likeUserId, likedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则意向成功！", null);
    }

    @ApiOperation(value = "删除一则意向", notes = "根据id删除一则意向")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Object delete(@ApiParam(example = "1", value = "该则意向的id") @PathVariable("id") Integer id) throws UserLikesNotCorrespondException, LikesNotFoundException {
        likeService.deleteById(id);
        return ResponseUtil.build(HttpStatus.OK.value(), "删除一则意向成功！", null);
    }

    @ApiOperation(value = "匹配", notes = "双向匹配，我对他有意向，他对我也有意向，就进行匹配")
    @GetMapping("/match")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Object match() {
        List<Likes> matchs = likeService.match();
        return ResponseUtil.build(HttpStatus.OK.value(), "匹配意向成功！", matchs);
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation("获取所有意向")
    public String all() {
        List<Likes> users = likeService.queryAll();
        return ResponseUtil.build(HttpStatus.OK.value(), "获取所有意向成功！", users);
    }

}
