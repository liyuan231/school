package com.school.service.impl;

import com.github.pagehelper.PageHelper;
import com.school.dao.LikesMapper;
import com.school.dao.UserMapper;
import com.school.model.Likes;
import com.school.model.LikesExample;
import org.springframework.security.authentication.BadCredentialsException;
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
    private LikesMapper likeMapper;
    @Resource
    private UserMapper userMapper;

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
        return likeMapper.selectByExampleSelective(likeExample);
    }

    public void add(Likes like) {
        like.setAddTime(LocalDateTime.now());
        like.setUpdateTime(LocalDateTime.now());
        likeMapper.insertSelective(like);
    }

    public Likes findById(Integer id) {
        return likeMapper.selectByPrimaryKey(id);
    }

    public int update(Likes like) {
        like.setUpdateTime(LocalDateTime.now());
        return likeMapper.updateByPrimaryKeySelective(like);
    }

    public void delete(Likes like) {
        like.setDeleted(true);
        update(like);
    }

    //mybtais逆向工程无法实现多表查询，那我自己来
    public List<Likes> match() {
        LikesExample likesExample = new LikesExample();
        likesExample.createCriteria().andDeletedEqualTo(false);
        List<Likes> likes = likeMapper.selectByExample(likesExample);
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

    public void like(Integer likeUserId, Integer likedUserId) {
        Likes likes = new Likes();
        likes.setLikeduserid(likedUserId);
        likes.setLikeuserid(likeUserId);
        add(likes);
    }

    public void update(Integer id, Integer likeUserId, Integer likedUserId) {
        Likes likes = new Likes();
        likes.setId(id);
        likes.setLikeuserid(likeUserId);
        likes.setLikeduserid(likedUserId);
        update(likes);
    }

    public void deleteById(Integer id) {
        Likes likes = new Likes();
        likes.setId(id);
        likes.setDeleted(true);
        update(likes);
    }

    public void deleteAndCheck(Integer curUserId, Integer likeId) {
        Likes likesInDb = findById(likeId);
        if (!likesInDb.getLikeuserid().equals(curUserId)) {
            throw new BadCredentialsException("当前用户与意向记录不符!");
        }
        deleteById(likeId);

    }

    public List<Likes> matchByLikedUserId(Integer likedUserId) {
        LikesExample likesExample = new LikesExample();
        LikesExample.Criteria criteria = likesExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        criteria.andLikeduseridEqualTo(likedUserId);
        return likeMapper.selectByExample(likesExample);
    }
}
