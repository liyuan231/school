package com.school.controller.admin;

import com.school.component.security.UserServiceImpl;
import com.school.dto.NormalLikesInfo;
import com.school.dto.NormalLikesInfoList;
import com.school.dto.SimpleLikesInfo;
import com.school.dto.TrivialUserInfo;
import com.school.exception.*;
import com.school.model.Likes;
import com.school.model.User;
import com.school.service.impl.LikeServiceImpl;
import com.school.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController("adminLikeController")
@RequestMapping("/api/admin/like")
@Api(value = "签约意向管理", tags = "管理端意向")
public class AdminLikeController {
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
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @PostMapping("/add/{likeUserId}/{likedUserId}")
    @ApiOperation(value = "添加一则意向", notes = "管理端手动牵线")
    public String like(@ApiParam(example = "2", value = "被喜欢的用户", required = true) @PathVariable("likedUserId") Integer likedUserId,
                       @ApiParam(example = "1", value = "主动去喜欢其他用户的用户", required = true) @PathVariable("likeUserId") Integer likeUserId) throws UserNotFoundException, UserNotCorrectException, LikesAlreadyExistException {
        likeService.like(likeUserId, likedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "管理端手动牵线成功！", null);
    }

    /**
     * NEW
     *
     * @param page
     * @param pageSize
     * @param sort
     * @param order
     * @return
     */
    /**
     * 全局共享缓存
     */
    Map<Integer, String> idToSchoolNameCache = new HashMap<>();

    @GetMapping("/list")
    @ApiOperation(value = "签约意向管理->页面数据显示", notes = "主要用于管理端分页显示")
    public Object list(@ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(required = false) Integer page,
                       @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(required = false) Integer pageSize,
                       @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time", required = false) String sort,
                       @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc", required = false) String order) {
        List<Likes> likes = likeService.querySelective(null, null, sort, order);
        //由于mybatis无法实现去重，因此需要曲线救国，此处获取全部意向
        List<SimpleLikesInfo> results = getSimpleLikesInfos(page, pageSize, likes);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取意向列表成功！", results);
    }

    public List<SimpleLikesInfo> getSimpleLikesInfos(Integer page, Integer pageSize, List<Likes> likes) {
        List<List<Likes>> list = deduplicationLikes(likes);
        List<List<List<Likes>>> partition = ListUtils.partition(list, pageSize);
        if (partition.size() == 0) {
            //获取的数据为空，返回一个空列表
            return new LinkedList<>();
        }
        List<List<Likes>> lists = partition.get(page - 1);
        //以上实现了分页
        List<SimpleLikesInfo> results = new LinkedList<>();

        for (List<Likes> likesList : lists) {
            //每一个元素代表一个学校意向 的所有学校
            SimpleLikesInfo simpleLikesInfo = new SimpleLikesInfo();
            List<String> schoolNames = new LinkedList<>();
            for (Likes eachLike : likesList) {
                if (simpleLikesInfo.getSchoolId() == null) {
                    simpleLikesInfo.setSchoolId(eachLike.getLikeuserid());
                }
                if (simpleLikesInfo.getSchoolName() == null) {
                    Integer likeUserId = eachLike.getLikeuserid();
                    String schoolName = idToSchoolNameCache.get(likeUserId);
                    if (schoolName == null) {
                        User user = userService.findById(likeUserId);
                        schoolName = user.getSchoolname();
                        idToSchoolNameCache.put(likeUserId, schoolName);
                    }
                    simpleLikesInfo.setSchoolName(schoolName);
                }
                Integer likeduserid = eachLike.getLikeduserid();
                String likedSchoolName = idToSchoolNameCache.get(likeduserid);
                if (likedSchoolName == null) {
                    User user2 = userService.findById(likeduserid);
                    likedSchoolName = user2.getSchoolname();
                    idToSchoolNameCache.put(likeduserid, likedSchoolName);
                }
                schoolNames.add(likedSchoolName);
            }
            simpleLikesInfo.setLikesSchoolName(schoolNames);
            results.add(simpleLikesInfo);
        }
        return results;
    }

    public List<List<Likes>> deduplicationLikes(List<Likes> likes) {
        Map<Integer, List<Likes>> map = new HashMap<>();
        //获取所有用户的意向，且已经去重
        for (Likes like : likes) {
            Integer likeuserid = like.getLikeuserid();
            List<Likes> list = map.get(like.getLikeuserid());
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(like);
            map.put(likeuserid, list);
        }
        List<List<Likes>> list = new LinkedList<>(map.values());
        return list;
    }

    /**
     * NEW
     *
     * @param likeUserId
     * @return
     */
    @GetMapping("/list/{userId}")
    @ApiOperation(value = "签约意向管理->查看", notes = "每个高校的意向的查看")
    public Object list(@PathVariable("userId") Integer likeUserId) {
        List<Likes> likes = likeService.querySelective(null, likeUserId, null, null, null, null, null);
        SimpleLikesInfo simpleLikesInfo = new SimpleLikesInfo();
        String schoolName = idToSchoolNameCache.get(likeUserId);
        simpleLikesInfo.setSchoolId(likeUserId);
        if (schoolName == null) {
            User user = userService.findById(likeUserId);
            simpleLikesInfo.setSchoolName(user.getSchoolname());
            idToSchoolNameCache.put(likeUserId, user.getSchoolname());
        }
        simpleLikesInfo.setSchoolName(schoolName);
        List<String> schoolNames = new LinkedList<>();
        for (Likes like : likes) {
            String likedSchoolName = idToSchoolNameCache.get(like.getLikeduserid());
            if (likedSchoolName == null) {
                User user = userService.findById(like.getLikeduserid());
                idToSchoolNameCache.put(like.getLikeduserid(), user.getSchoolname());
                likedSchoolName = user.getSchoolname();
            }
            schoolNames.add(likedSchoolName);
        }
        simpleLikesInfo.setLikesSchoolName(schoolNames);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取该高校意向列表成功！", simpleLikesInfo);
    }

    /**
     * NEW
     *
     * @param likeUserId
     * @return
     */
    @GetMapping("/listModified/{userId}")
    @ApiOperation(value = "签约意向管理->修改签约意向->页面数据的获取", notes = "修改签约意向中 签约的高校的获取")
    public Object listModified(@PathVariable("userId") Integer likeUserId) {
        List<Likes> likes = likeService.querySelective(null, likeUserId, null, null, null, null, null);
        NormalLikesInfoList normalLikesInfoList = new NormalLikesInfoList();
        String schoolName = idToSchoolNameCache.get(likeUserId);
        normalLikesInfoList.setSchoolId(likeUserId);
        if (schoolName == null) {
            User user = userService.findById(likeUserId);
            normalLikesInfoList.setSchoolName(user.getSchoolname());
            idToSchoolNameCache.put(likeUserId, user.getSchoolname());
        }
        normalLikesInfoList.setSchoolName(schoolName);

        List<NormalLikesInfo> likesInfos = new LinkedList<>();
        for (Likes like : likes) {
            NormalLikesInfo normalLikesInfo = new NormalLikesInfo();
            normalLikesInfo.setLikeId(like.getId());
            String likedSchoolName = idToSchoolNameCache.get(like.getLikeduserid());
            if (likedSchoolName == null) {
                User user = userService.findById(like.getLikeduserid());
                likedSchoolName = user.getSchoolname();
                idToSchoolNameCache.put(like.getLikeduserid(), likedSchoolName);
            }
            normalLikesInfo.setSchoolName(likedSchoolName);
            likesInfos.add(normalLikesInfo);
        }
        normalLikesInfoList.setList(likesInfos);
        return ResponseUtil.build(HttpStatus.OK.value(), "获取当前高校意向学校成功！", normalLikesInfoList);
    }


    @GetMapping("/search")
    @ApiOperation(value = "签约意向管理->搜索", notes = "输入高校名进行搜索")
    public Object search(@RequestParam(value = "keyWord", required = false) String schoolName,
                         @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(required = false) Integer page,
                         @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(required = false) Integer pageSize,
                         @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time", required = false) String sort,
                         @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc", required = false) String order) {
        List<User> users = userService.querySelective(schoolName, null, null, sort, order);
        List<Likes> tmpLikes = new LinkedList<>();
        for (User user : users) {
            List<Likes> likes = likeService.querySelective(null, user.getId(), null, page, pageSize, sort, order);
            tmpLikes.addAll(likes);
        }
        List<SimpleLikesInfo> result = getSimpleLikesInfos(page, pageSize, tmpLikes);
        return ResponseUtil.build(HttpStatus.OK.value(), "搜索该高校意向列表成功！", result);

    }


//    @GetMapping("/listSchools")
//    @ApiOperation(value = "获取所有高校分页显示", notes = "添加意向高校中，获取所有高校信息分页显示的列表")
//    public Object listSchools(@ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(required = false) Integer page,
//                              @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(required = false) Integer pageSize,
//                              @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time", required = false) String sort,
//                              @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc", required = false) String order) {
//        List<User> users = userService.querySelective(page, pageSize, sort, order);
//        List<TrivialUserInfo> result = getTrivialUserInfos(users);
//        return ResponseUtil.build(HttpStatus.OK.value(), "获取该页用户列表成功！", result);
//    }

    @GetMapping("/listSchools")
    @ApiOperation(value = "签约意向管理->修改签约意向->搜索", notes = "签约意向管理->修改签约意向->搜索")
    public Object listSchools(@RequestParam(value = "keyWord", required = false) String schoolName,
                              @ApiParam(example = "1", value = "分页使用，要第几页的数据") @RequestParam(required = false) Integer page,
                              @ApiParam(example = "10", value = "分页使用，要该页的几条数据") @RequestParam(required = false) Integer pageSize,
                              @ApiParam(example = "1", value = "排序方式，从数据库中要的数据使用什么进行排序，如 add_time,update_time") @RequestParam(defaultValue = "add_time", required = false) String sort,
                              @ApiParam(example = "desc", value = "排序方式，升序asc还是降序desc") @RequestParam(defaultValue = "desc", required = false) String order) {
        List<User> users = userService.querySelective(schoolName, page, pageSize, sort, order);
        List<TrivialUserInfo> result = getTrivialUserInfos(users);
        return ResponseUtil.build(HttpStatus.OK.value(), "搜索用户列表成功！", result);
    }


    private List<TrivialUserInfo> getTrivialUserInfos(List<User> users) {
        List<TrivialUserInfo> result = new LinkedList<>();
        for (User user : users) {
            TrivialUserInfo trivialUserInfo = new TrivialUserInfo();
            trivialUserInfo.setId(user.getId());
            trivialUserInfo.setSchoolName(user.getSchoolname());
            result.add(trivialUserInfo);
        }
        return result;
    }

    @PostMapping("/update/{id}/{likeUserId}/{likedUserId}")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    @ApiOperation(value = "更新一则意向", notes = "管理端修改某一则意向，比如现在我喜欢女神，现在管理员强制修改成女神喜欢我")
    public Object update(@ApiParam(example = "1", value = "待修改的意向的id") @PathVariable("id") Integer id,
                         @ApiParam(example = "1", value = "主动去喜欢的用户id") @PathVariable("likeUserId") Integer likeUserId,
                         @ApiParam(example = "2", value = "被喜欢的用户的id") @PathVariable("likedUserId") Integer likedUserId) throws UserLikesNotCorrespondException, LikesNotFoundException {
        likeService.update(id, likeUserId, likedUserId);
        return ResponseUtil.build(HttpStatus.OK.value(), "更新一则意向成功！", null);
    }

    @ApiOperation(value = "签约意向管理->修改签约意向->删除", notes = "根据id删除一则意向")
    @DeleteMapping("/delete/{id}")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
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

//    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
//    @ApiOperation("获取所有意向")
//    public String all() {
//        List<Likes> users = likeService.queryAll();
//        return ResponseUtil.build(HttpStatus.OK.value(), "获取所有意向成功！", users);
//    }


    @ApiOperation(value = "签约意向管理->导出签约意向表", notes = "签约意向管理->导出签约意向表")
    @GetMapping("/exportLikesForm")
    public void exportLikesForm(HttpServletResponse response) throws IOException {
        Workbook workbook = likeService.exportLikesForm();
        String fileName = Likes.class.getSimpleName() + ".xls";
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

}
