package com.school.service.impl;

import com.school.dao.UsertoroleMapper;
import com.school.model.User;
import com.school.model.Usertorole;
import com.school.model.UsertoroleExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserToRoleServiceImpl {

    @Resource
    private UsertoroleMapper usertoroleMapper;

    public void updateByUserId(Integer userId, Integer level) {
        UsertoroleExample usertoroleExample = new UsertoroleExample();
        UsertoroleExample.Criteria criteria = usertoroleExample.createCriteria();
        criteria.andUseridEqualTo(userId);
        Usertorole usertorole = new Usertorole();
        usertorole.setRoleid(level);
        usertoroleMapper.updateByExampleSelective(usertorole, usertoroleExample);
    }

    public Integer retrieveUserToRoleByUser(User user) {
        UsertoroleExample usertoroleExample = new UsertoroleExample();
        UsertoroleExample.Criteria criteria = usertoroleExample.createCriteria();
        criteria.andUseridEqualTo(user.getId());
        Usertorole usertorole = usertoroleMapper.selectOneByExample(usertoroleExample);
        return usertorole.getRoleid();
    }
}
