package com.school.service.impl;

import com.school.dao.RoleMapper;
import com.school.model.Role;
import com.school.model.RoleExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RoleServiceImpl {

    @Resource
    RoleMapper roleMapper;

    public Role findByName(String roleName) {
        RoleExample roleExample = new RoleExample();
        RoleExample.Criteria criteria = roleExample.createCriteria();
        criteria.andNameEqualTo(roleName);
        return roleMapper.selectOneByExampleSelective(roleExample);
    }



}
