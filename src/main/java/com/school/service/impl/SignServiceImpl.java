package com.school.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.school.component.security.UserServiceImpl;
import com.school.dao.SignMapper;
import com.school.exception.*;
import com.school.model.Sign;
import com.school.model.SignExample;
import com.school.model.User;
import com.school.utils.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserToRoleServiceImpl userToRoleService;


    public void sign(Integer signedUserId) throws UserNotFoundException, SignAlreadyExistException, SignNotCorrectException {
        User signedUser = userService.findById(signedUserId);
        User signUserId = userService.retrieveUserByToken();
        if (signedUser == null) {
            throw new UserNotFoundException("被签约的用户不存在！");
        }
        Sign sign = new Sign();
        sign.setSignuserid(signUserId.getId());
        sign.setSigneduserid(signedUserId);
        if (sign.getSigneduserid().equals(sign.getSignuserid())) {
            throw new SignNotCorrectException("不能和自己签约！");
        }
        List<Sign> signs = find(sign);
        if (signs.size() >= 1) {
            throw new SignAlreadyExistException("已经和该用户签约过了！！");
        }
        add(sign);
    }

    private List<Sign> find(Sign sign) {
        SignExample signExample = new SignExample();
        SignExample.Criteria criteria = signExample.createCriteria();
        criteria.andSignuseridEqualTo(sign.getSignuserid());
        criteria.andSigneduseridEqualTo(sign.getSigneduserid());
        criteria.andDeletedEqualTo(false);
        List<Sign> signs = signMapper.selectByExampleSelective(signExample);
        return signs;
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
        if (page != null || limit != null) {
            if (page == null) {
                PageHelper.startPage(1, limit);
            } else if (limit == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, limit);
            }
        }
        List<Sign> signs = signMapper.selectByExampleSelective(signExample);
        PageInfo<Sign> signPageInfo = new PageInfo<>(signs);
        return signPageInfo.getList();

    }

    public void add(Sign sign) {
        sign.setAddTime(LocalDateTime.now());
        sign.setUpdateTime(LocalDateTime.now());
        signMapper.insertSelective(sign);
    }

    public Sign findById(Integer id) {
        return signMapper.selectByPrimaryKey(id);
    }

    public int update(Sign sign) throws SignNotFoundException, UserSignCorrespondException, UserNotFoundException {
        Integer id = sign.getId();
        Sign byId = findById(id);
        if (byId == null) {
            throw new SignNotFoundException("该签约不存在，请检查id");
        }
        Integer signUserId = sign.getSignuserid();
        Integer signedUserId = sign.getSigneduserid();
        com.school.model.User user = userService.retrieveUserByToken();
        Integer roleId = userToRoleService.retrieveUserToRoleByUser(user);
        if (roleId != RoleEnum.ADMINISTRATOR.value() && signUserId != null && !signUserId.equals(user.getId())) {
            throw new UserSignCorrespondException("签约用户与当前sign不一致！");
        }
        if (roleId != RoleEnum.ADMINISTRATOR.value() && signedUserId != null) {
            User byId1 = userService.findById(signedUserId);
            if (byId1 == null) {
                throw new UserNotFoundException("被签约用户不存在！");
            }
        }
        sign.setUpdateTime(LocalDateTime.now());
        return signMapper.updateByPrimaryKeySelective(sign);
    }


    public void sign(Integer signUserId, Integer signedUserId) {
        Sign sign = new Sign();
        sign.setSignuserid(signUserId);
        sign.setSigneduserid(signedUserId);
        add(sign);
    }

    public void update(Integer id, Integer signUserId, Integer signedUserId) throws SignNotFoundException, UserSignCorrespondException, UserNotFoundException {
        Sign sign = new Sign();
        sign.setId(id);
        sign.setSignuserid(signUserId);
        sign.setSigneduserid(signedUserId);
        update(sign);
    }

    public void deleteById(Integer id) throws SignNotFoundException, UserSignCorrespondException, UserNotFoundException {
        Sign sign = new Sign();
        sign.setId(id);
        delete(sign);
    }

    private void delete(Sign byId) throws UserSignCorrespondException, SignNotFoundException, UserNotFoundException {
        byId.setDeleted(true);
        update(byId);
    }

    public List<Sign> findBySignUserId() {
        SignExample signExample = new SignExample();
        SignExample.Criteria criteria = signExample.createCriteria();
        User user = userService.retrieveUserByToken();
        criteria.andSignuseridEqualTo(user.getId());
        criteria.andDeletedEqualTo(false);
        return signMapper.selectByExampleSelective(signExample);
    }

    public List<Sign> findBySignedUserId() {
        SignExample signExample = new SignExample();
        SignExample.Criteria criteria = signExample.createCriteria();
        User user = userService.retrieveUserByToken();
        criteria.andSigneduseridEqualTo(user.getId());
        criteria.andDeletedEqualTo(false);
        return signMapper.selectByExampleSelective(signExample);
    }

    public List<Sign> queryAll() {
        SignExample signExample = new SignExample();
        SignExample.Criteria criteria = signExample.createCriteria();
        criteria.andDeletedEqualTo(false);
        return signMapper.selectByExampleSelective(signExample);

    }
}
