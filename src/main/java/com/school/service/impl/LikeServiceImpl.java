package com.school.service.impl;

import com.github.pagehelper.PageHelper;
import com.school.component.security.UserServiceImpl;
import com.school.dao.LikesMapper;
import com.school.exception.*;
import com.school.model.Likes;
import com.school.model.LikesExample;
import com.school.model.User;
import com.school.utils.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                                      Integer limit,
                                      String sort,
                                      String order) {
        LikesExample likeExample = new LikesExample();
        LikesExample.Criteria criteria = likeExample.createCriteria();
        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(id);
        }
        if (!StringUtils.isEmpty(likedUserId)) {
            criteria.andLikeduseridEqualTo(likedUserId);
        }
        if (!StringUtils.isEmpty(likeUserId)) {
            criteria.andLikeduseridEqualTo(likeUserId);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            likeExample.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return likesMapper.selectByExampleSelective(likeExample);
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
        User user = userService.retrieveUserByToken();
        Integer roleId = userToRoleService.retrieveUserToRoleByUser(user);
        Integer likeuserid = byId.getLikeuserid();
        if (roleId != RoleEnum.ADMINISTRATOR.value() && likeuserid != null && !user.getId().equals(likeuserid)) {
            throw new UserLikesNotCorrespondException("当前用户与该则意向id不一致！");
        }
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
        List<Likes> likes1 = find(likeUserId, likedUserId);
        if (likes1.size() >= 1) {
            throw new LikesAlreadyExistException("已经和该用户表明过意向了!");
        }

        if (byId == null) {
            throw new UserNotFoundException("用户不存在！");
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
//查询对我有意向的用户
    public List<Likes> matchByLikedUserId() {
        User user = userService.retrieveUserByToken();
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andLikeduseridEqualTo(user.getId());
        return likesMapper.selectByExample(likesExample);
    }
//查询我有意向的用户
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

//查询互相有意向的用户
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
}
