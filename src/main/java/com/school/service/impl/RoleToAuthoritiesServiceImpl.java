package com.school.service.impl;

import com.school.dao.RoletoauthoritiesMapper;
import com.school.model.Roletoauthorities;
import com.school.model.RoletoauthoritiesExample;
import com.school.utils.RoleEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleToAuthoritiesServiceImpl {
    @Resource
    private RoletoauthoritiesMapper roletoauthoritiesMapper;

    public List<Roletoauthorities> findByRoleId(Integer id) {
        RoletoauthoritiesExample roletoauthoritiesExample = new RoletoauthoritiesExample();
        RoletoauthoritiesExample.Criteria criteria = roletoauthoritiesExample.createCriteria();
        criteria.andRoleidEqualTo(id);
        return roletoauthoritiesMapper.selectByExampleSelective(roletoauthoritiesExample);
    }

    private void add(int roleId, String authority) {
        Roletoauthorities roletoauthorities = new Roletoauthorities();
        roletoauthorities.setRoleid(roleId);
        roletoauthorities.setAuthority(authority);
        roletoauthoritiesMapper.insertSelective(roletoauthorities);
    }

    public List<Roletoauthorities> selectByRoleIdAndAuthority(int roleId, String authority) {
        RoletoauthoritiesExample roletoauthoritiesExample = new RoletoauthoritiesExample();
        RoletoauthoritiesExample.Criteria criteria = roletoauthoritiesExample.createCriteria();
        criteria.andRoleidEqualTo(roleId);
        criteria.andAuthorityEqualTo(authority);
        List<Roletoauthorities> roletoauthorities = roletoauthoritiesMapper.selectByExampleSelective(roletoauthoritiesExample);
        return roletoauthorities;
    }

    private void remove(int roleId, String authority) {
        RoletoauthoritiesExample roletoauthoritiesExample = new RoletoauthoritiesExample();
        RoletoauthoritiesExample.Criteria criteria = roletoauthoritiesExample.createCriteria();
        criteria.andRoleidEqualTo(roleId).andAuthorityEqualTo(authority);
        roletoauthoritiesMapper.deleteByExample(roletoauthoritiesExample);
    }


    public void addAuthority(RoleEnum roleEnum, String authority) {
        List<Roletoauthorities> roletoauthorities = selectByRoleIdAndAuthority(roleEnum.value(), authority);
        if (roletoauthorities.size() == 0) {
            add(roleEnum.value(), authority);
        }
    }

    public void removeAuthority(RoleEnum roleEnum, String authority) {
        remove(roleEnum.value(),authority);
    }
}
