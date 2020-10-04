package com.school.service.impl;

import com.github.pagehelper.PageHelper;
import com.school.dao.SignMapper;
import com.school.dao.UserMapper;
import com.school.model.Sign;
import com.school.model.SignExample;
import com.school.model.UserExample;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SignServiceImpl {
    @Resource
    private SignMapper signMapper;
    @Resource
    private UserMapper userMapper;


    public void sign(Integer signedUserId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(user.getUsername());
        com.school.model.User signUser = userMapper.selectOneByExampleSelective(userExample);
        Sign sign = new Sign();
        sign.setAddTime(LocalDateTime.now());
        sign.setUpdateTime(LocalDateTime.now());
        sign.setSignuserid(signUser.getId());
        sign.setSigneduserid(signedUserId);
        signMapper.insertSelective(sign);
    }


    public List<Sign> querySelective(Integer id,
                                     Integer signUserId,
                                     Integer signedUserId,
                                     Integer page,
                                     Integer limit,
                                     String sort,
                                     String order) {
        SignExample signExample = new SignExample();
        SignExample.Criteria criteria = signExample.createCriteria();
        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(id);
        }
        if (!StringUtils.isEmpty(signedUserId)) {
            criteria.andSigneduseridEqualTo(signedUserId);
        }
        if (!StringUtils.isEmpty(signUserId)) {
            criteria.andSignuseridEqualTo(signUserId);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            signExample.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return signMapper.selectByExampleSelective(signExample);
    }

    public void add(Sign sign) {
        sign.setAddTime(LocalDateTime.now());
        sign.setUpdateTime(LocalDateTime.now());
        signMapper.insertSelective(sign);
    }

    public Sign findById(Integer id) {
        return signMapper.selectByPrimaryKey(id);
    }

    public int update(Sign sign) {
        sign.setUpdateTime(LocalDateTime.now());
        return signMapper.updateByPrimaryKeySelective(sign);
    }

    public void delete(Sign sign) {
        sign.setUpdateTime(LocalDateTime.now());
        sign.setDeleted(true);
        signMapper.updateByPrimaryKeySelective(sign);
    }


    public void sign(Integer signUserId, Integer signedUserId) {
        Sign sign = new Sign();
        sign.setSignuserid(signUserId);
        sign.setSigneduserid(signedUserId);
        add(sign);
    }

    public void update(Integer id, Integer signUserId, Integer signedUserId) {
        Sign sign = new Sign();
        sign.setId(id);
        sign.setSignuserid(signUserId);
        sign.setSigneduserid(signedUserId);
        update(sign);
    }

    public void deleteById(Integer id) {
        Sign sign = new Sign();
        sign.setId(id);
        delete(sign);
    }
}
