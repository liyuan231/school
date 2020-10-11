package com.school.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.school.component.security.UserServiceImpl;
import com.school.dao.LikesMapper;
import com.school.dto.SimpleLikesInfo;
import com.school.exception.*;
import com.school.model.Likes;
import com.school.model.LikesExample;
import com.school.model.User;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class LikeServiceImpl {
    @Resource
    private LikesMapper likesMapper;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserToRoleServiceImpl userToRoleService;


    public List<Likes> querySelective(Integer id,
                                      Integer likeUserId,
                                      Integer likedUserId,
                                      Integer page,
                                      Integer pageSize,
                                      String sort,
                                      String order) {
        LikesExample likeExample = new LikesExample();
        LikesExample.Criteria criteria = likeExample.createCriteria();
//        criteria.setP
        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(id);
        }
        if (!StringUtils.isEmpty(likedUserId)) {
            criteria.andLikeduseridEqualTo(likedUserId);
        }
        if (!StringUtils.isEmpty(likeUserId)) {
            criteria.andLikeuseridEqualTo(likeUserId);
        }
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            likeExample.setOrderByClause(sort + " " + order);
        }
        if (page != null || pageSize != null) {
            if (page == null) {
                PageHelper.startPage(1, pageSize);
            } else if (pageSize == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, pageSize);
            }
        }
        criteria.andDeletedEqualTo(false);
        List<Likes> likes = likesMapper.selectByExampleSelective(likeExample);
        PageInfo<Likes> pageInfo = new PageInfo<>(likes);
        return pageInfo.getList();
    }

    public void add(Likes like) {
        like.setAddTime(LocalDateTime.now());
        like.setUpdateTime(LocalDateTime.now());
        likesMapper.insertSelective(like);
    }

    public Likes findById(Integer id) {
        return likesMapper.selectByPrimaryKey(id);
    }

    public int update(Likes like) throws LikesNotFoundException, UserLikesNotCorrespondException {
        Likes byId = findById(like.getId());
        if (byId == null) {
            throw new LikesNotFoundException("该则意向不存在，请检查id");
        }
//        User user = userService.retrieveUserByToken();
//        Integer roleId = userToRoleService.retrieveUserToRoleByUser(user);
//        Integer likeuserid = byId.getLikeuserid();
//        if (roleId != RoleEnum.ADMINISTRATOR.value() && likeuserid != null && !user.getId().equals(likeuserid)) {
//            throw new UserLikesNotCorrespondException("当前用户与该则意向id不一致！");
//        }
        like.setUpdateTime(LocalDateTime.now());
        return likesMapper.updateByPrimaryKeySelective(like);
    }


    public void delete(Likes like) throws UserLikesNotCorrespondException, LikesNotFoundException {
        like.setDeleted(true);
        update(like);
    }

    //mybtais逆向工程无法实现多表查询，那我自己来
    public List<Likes> match() {
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        List<Likes> likes = likesMapper.selectByExample(likesExample);

        Map<Integer, Integer> map = new HashMap<>();
        List<Likes> result = new LinkedList<>();
        for (Likes like : likes) {
            //类比：我（喜欢）她-》    key->我：value->她
            Integer integer = map.get(like.getLikeuserid());
            if (integer != null) {
                result.add(like);
            } else {
                map.put(like.getLikeduserid(), like.getLikeuserid());
            }
        }
        return result;
    }

    public void like(Integer likeUserId, Integer likedUserId) throws UserNotFoundException, UserNotCorrectException, LikesAlreadyExistException {
        User byId = userService.findById(likedUserId);
        if (byId == null) {
            throw new UserNotFoundException("用户不存在！");
        }
        List<Likes> likes1 = find(likeUserId, likedUserId);
        if (likes1.size() >= 1) {
            throw new LikesAlreadyExistException("已经和该用户表明过意向了!");
        }
        if (likeUserId.equals(likedUserId)) {
            throw new UserNotCorrectException("不能自己对自己有意向！");
        }
        Likes likes = new Likes();
        likes.setLikeduserid(likedUserId);
        likes.setLikeuserid(likeUserId);
        add(likes);
    }

    private List<Likes> find(Integer likeUserId, Integer likedUserId) {
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andLikeduseridEqualTo(likedUserId);
        criteria.andLikeuseridEqualTo(likeUserId);
        criteria.andDeletedEqualTo(false);
        List<Likes> likes = likesMapper.selectByExample(likesExample);
        return likes;
    }

    public void update(Integer id, Integer likeUserId, Integer likedUserId) throws UserLikesNotCorrespondException, LikesNotFoundException {
        Likes likes = new Likes();
        likes.setId(id);
        likes.setLikeuserid(likeUserId);
        likes.setLikeduserid(likedUserId);
        update(likes);
    }

    public void deleteById(Integer id) throws UserLikesNotCorrespondException, LikesNotFoundException {
        Likes likes = new Likes();
        likes.setId(id);
        likes.setDeleted(true);
        update(likes);
    }

    public List<Likes> matchByLikedUserId() {
        User user = userService.retrieveUserByToken();
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andLikeduseridEqualTo(user.getId());
        return likesMapper.selectByExample(likesExample);
    }

    public List<Likes> matchByLikeUserId() {
        User user = userService.retrieveUserByToken();
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andLikeuseridEqualTo(user.getId());
        return likesMapper.selectByExample(likesExample);
    }

    public void like(Integer likedUserId) throws UserNotFoundException, UserNotCorrectException, LikesAlreadyExistException {
        User user = userService.retrieveUserByToken();
        like(user.getId(), likedUserId);
    }


    public List<Likes> matchByUserId() {
        User user = userService.retrieveUserByToken();
        Integer userId = user.getId();
        List<Likes> result = new LinkedList<>();
        List<Likes> matches = match();

        for (Likes next : matches) {
            if (next.getLikeuserid().equals(userId)) {
                result.add(next);
            }
        }
        return result;

    }

    public List<Likes> queryAll() {
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        return likesMapper.selectByExample(likesExample);
    }

    public List<Likes> querySelective(Integer page, Integer pageSize, String sort, String order) {
        return querySelective(null, null, null, page, pageSize, sort, order);
    }

    public Workbook exportLikesForm() {
        List<Likes> likes = querySelective(null, null, null, null, null, null, null);
        List<List<Likes>> lists = deduplicationLikes(likes);
        //此处每一个List代表一个用户及其所有的意向学校
        List<SimpleLikesInfo> likesInfos = new LinkedList<>();
        for (List<Likes> like : lists) {
            SimpleLikesInfo simpleLikesInfo = new SimpleLikesInfo();
            //该用户下所有意向的学校
            List<String> schoolNames = new LinkedList<>();
            for (Likes eachLike : like) {
                Integer likeuserid = eachLike.getLikeuserid();
                if (simpleLikesInfo.getSchoolId() == null) {
                    simpleLikesInfo.setSchoolId(likeuserid);
                }
                if (simpleLikesInfo.getSchoolName() == null) {
                    User user = userService.findById(likeuserid);
                    simpleLikesInfo.setSchoolName(user.getSchoolname());
                }
                User user = userService.findById(eachLike.getLikeduserid());
                schoolNames.add(user.getSchoolname());
            }
            simpleLikesInfo.setLikesSchoolName(schoolNames);
            likesInfos.add(simpleLikesInfo);
        }
        Workbook workbook = new HSSFWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("IMPACT");
        font.setBold(true);
        cellStyle.setFont(font);

        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("签约意向表");
        row = sheet.createRow(1);
        Cell cell = row.createCell(0);
        cell.setCellValue("学校名称");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("签约意向");
        cell.setCellStyle(cellStyle);
//        sheet.setColumnWidth(10,100*256);
//        sheet.setDefaultRowHeight(Short);
        for (int i = 0; i < likesInfos.size(); i++) {
            row = sheet.createRow(i + 2);
            cell = row.createCell(0);
            cell.setCellValue(likesInfos.get(i).getSchoolName());
            cell = row.createCell(1);
            StringBuilder s = new StringBuilder(Arrays.toString(likesInfos.get(i).getLikesSchoolName().toArray()));
            if (s.length() > 0) {
                s.deleteCharAt(s.length() - 1);
                s.deleteCharAt(0);
            }
            cell.setCellValue(s.toString());
        }
        return workbook;
    }

//    public List<Likes> querySelective(Integer page, Integer pageSize, String sort, String order, String distinctBy) {
//        return querySelective(null,null,null,page,pageSize,sort,order,distinctBy);
//    }

    private List<List<Likes>> deduplicationLikes(List<Likes> likes) {
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
}
