package com.school.component.security;

import com.github.pagehelper.PageHelper;
import com.school.dao.RoleMapper;
import com.school.dao.RoletoauthoritiesMapper;
import com.school.dao.UserMapper;
import com.school.dao.UsertoroleMapper;
import com.school.exception.UsernameAlreadyExistException;
import com.school.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
//@Transactional 加上后代理会冲突
public class UserServiceImpl implements UserDetailsService {

    @Value("${password.encoded.prefix:}")
    private String encodedPasswordPrefix;
    @Resource
    private UserMapper userMapper;
    @Resource
    UsertoroleMapper usertoroleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoletoauthoritiesMapper roletoauthoritiesMapper;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        User user = userMapper.selectOneByExampleSelective(userExample);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        UsertoroleExample usertoroleExample = new UsertoroleExample();
        usertoroleExample.createCriteria().andUseridEqualTo(user.getId());
        List<Usertorole> usertoroles = usertoroleMapper.selectByExample(usertoroleExample);
        //一个用户可能会有多个角色
        List<Role> roles = new ArrayList<>();
        for (Usertorole usertorole : usertoroles) {
            RoleExample roleExample = new RoleExample();
            roleExample.createCriteria().andIdEqualTo(usertorole.getRoleid());
            Role role = roleMapper.selectOneByExample(roleExample);
            roles.add(role);
        }
        // RoletoauthoritiesExample roletoauthoritiesExample = new RoletoauthoritiesExample();
        //roletoauthoritiesExample.createCriteria().andRoleidEqualTo(usertorole.getRoleid());

        //List<Roletoauthorities> roletoauthorities = roletoauthoritiesMapper.selectByExample(roletoauthoritiesExample);
        Collection<GrantedAuthority> roles_ = new HashSet<>();
        for (Role role : roles) {
            roles_.add(new SimpleGrantedAuthority(role.getName()));
        }
//        for (Roletoauthorities roletoauthority : roletoauthorities) {
//            roles.add(new SimpleGrantedAuthority(roletoauthority.getAuthority()));
//        }
        return new org.springframework.security.core.userdetails.User(username, encodedPasswordPrefix + user.getPassword(), roles_);
    }


    public List<User> querySelective(Integer userId,
                                     Integer page,
                                     Integer limit,
                                     String sort,
                                     String order) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!StringUtils.isEmpty(userId)) {
            criteria.andIdEqualTo(userId);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            userExample.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return userMapper.selectByExampleSelective(userExample);
    }

    public User findByUsername(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        return userMapper.selectOneByExampleSelective(userExample);
    }


    public void add(User user) throws UsernameAlreadyExistException {
        User byUsername = findByUsername(user.getUsername());
        if (byUsername != null) {
            throw new UsernameAlreadyExistException("用户名重复！");
        }
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.insertSelective(user);
    }

    public void delete(User user) {
        user.setDeleted(true);
        update(user);
    }

    public int update(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!StringUtils.isEmpty(user.getUsername())) {
            criteria.andUsernameEqualTo(user.getUsername());
        }
        if (!StringUtils.isEmpty(user.getId())) {
            criteria.andIdEqualTo(user.getId());
        }
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByExampleSelective(user, userExample);
    }


    public List<User> queryAll() {
        UserExample userExample = new UserExample();
        return userMapper.selectByExample(userExample);
    }

    public void add(User user, Integer level) throws UsernameAlreadyExistException {
        add(user);
        User byUsername = findByUsername(user.getUsername());
        Usertorole usertorole = new Usertorole();
        usertorole.setRoleid(level);
        usertorole.setUserid(byUsername.getId());
        usertoroleMapper.insertSelective(usertorole);
    }

    public void update(User user, Integer level) {
        update(user);
        User byUsername = findByUsername(user.getUsername());
        UsertoroleExample usertoroleExample = new UsertoroleExample();
        usertoroleExample.createCriteria().andUseridEqualTo(byUsername.getId());
        Usertorole usertorole = usertoroleMapper.selectOneByExample(usertoroleExample);
        usertorole.setRoleid(level);
        usertoroleMapper.updateByPrimaryKey(usertorole);
    }

    public List<User> querySelective(Integer userId,
                                     String username,
                                     String schoolName,
                                     Integer page,
                                     Integer limit,
                                     String sort,
                                     String order) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!StringUtils.isEmpty(userId)) {
            criteria.andIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameEqualTo(username);
        }
        if (!StringUtils.isEmpty(schoolName)) {
            criteria.andSchoolnameLike("%" + schoolName + "%");
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            userExample.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return userMapper.selectByExampleSelective(userExample);
    }

    public void add(String username, String password, String schoolName, Integer level) throws UsernameAlreadyExistException {
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setSchoolname(schoolName);
        add(user, level);
    }

    public void deleteById(Integer id) {
        User user = new User();
        user.setId(id);
        delete(user);
    }

    public void update(Integer id, String password, String schoolName, Integer level) {
        User user = new User();
        user.setId(id);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setSchoolname(schoolName);
        update(user, level);
    }
}
