package com.school.component.security;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.school.dao.RoleMapper;
import com.school.dao.RoletoauthoritiesMapper;
import com.school.dao.UserMapper;
import com.school.dao.UsertoroleMapper;
import com.school.exception.*;
import com.school.model.*;
import com.school.service.impl.EmailServiceImpl;
import com.school.service.impl.PicsServiceImpl;
import com.school.service.impl.UserToRoleServiceImpl;
import com.school.utils.AccountStatus;
import com.school.utils.AssertUtil;
import com.school.utils.RoleEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private PicsServiceImpl picsService;
    @Autowired
    private UserToRoleServiceImpl userToRoleService;
    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
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

    public User findByUsername(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        User user = userMapper.selectOneByExampleSelective(userExample);
        return user;
    }


    public void add(User user) throws UsernameAlreadyExistException {
        User byUsername = findByUsername(user.getUsername());
        if (byUsername != null) {
            throw new UsernameAlreadyExistException("用户名重复！");
        }
        user.setAvatarurl("default.png");
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.insertSelective(user);
    }

    public void delete(User user) throws UserNotFoundException {
        user.setDeleted(true);
        update(user);
    }

    public User update(User user) throws UserNotFoundException {
//        User byId = findById(user.getId());
//        if (byId == null) {
//            throw new UserNotFoundException("用户不存在！");
//        }
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
        userMapper.updateByExampleSelective(user, userExample);
        return user;
    }

    public User findById(Integer id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(id);
        User user = userMapper.selectOneByExampleSelective(userExample);
        return user;
    }

    public List<User> queryAll() {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andDeletedEqualTo(false);
        List<User> users = userMapper.selectByExample(userExample);
        return users;
    }

    public void add(User user, Integer level) throws UsernameAlreadyExistException {
        add(user);
        User byUsername = findByUsername(user.getUsername());
        Usertorole usertorole = new Usertorole();
        usertorole.setRoleid(level);
        usertorole.setUserid(byUsername.getId());
        usertoroleMapper.insertSelective(usertorole);
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
        if (page != null || limit != null) {
            if (page == null) {
                PageHelper.startPage(1, limit);
            } else if (limit == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, limit);
            }
        }
        List<User> users = userMapper.selectByExampleSelective(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return pageInfo.getList();
    }

    public List<User> querySelective(Integer page, Integer limit, String sort, String order) {
        return querySelective(null, null, null, page, limit, sort, order);
    }

    public void add(String username, String password, String schoolName, Integer level) throws UsernameAlreadyExistException {
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setSchoolname(schoolName);
        user.setAvatarurl("default.png");
        add(user, level);
//        picsService.insert(byUsername.getId(), "default.png", FileEnum.AVATAR_URL.value());
    }

    public void deleteById(Integer id) throws UserNotFoundException {
        User user = new User();
        user.setId(id);
        delete(user);
    }

    public void update(Integer id, String password, String schoolName, Integer level) throws UserNotFoundException {
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setSchoolname(schoolName);
        update(user);
        userToRoleService.updateByUserId(user.getId(), level);
    }

//    public void add(String username, String password, String schoolName, Integer level, MultipartFile file) throws UsernameAlreadyExistException, IOException, UserNotFoundException {
//        add(username, password, schoolName, level);
//        User user = findByUsername(username);
//    }

    public User retrieveUserByToken() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = findByUsername(principal.getUsername());
        return user;
    }

    public void clearPassword(List<User> users) {
        for (User user : users) {
            user.setPassword("[PROTECTED]");
        }
    }

    public User update(Integer id, String schoolName, String contact, String address, String telephone, String email) throws UserNotFoundException {
        User user = findById(id);
        if (user == null) {
            throw new UserNotFoundException("用户id不存在！");
        }
        if (StringUtils.hasText(schoolName)) {
            user.setSchoolname(schoolName);
        }
        if (StringUtils.hasText(contact)) {
            user.setContact(contact);
        }
        if (StringUtils.hasText(address)) {
            user.setAddress(address);
        }
        if (StringUtils.hasText(telephone)) {
            user.setTelephone(telephone);
        }
        if (StringUtils.hasText(email)) {
            user.setEmail(email);
        }
        return update(user);

    }

    public void openLogin() {
        List<User> users = querySelective(null, null, null, null);
        for (User user : users) {
            user.setAccountstatus(AccountStatus.ALLOW_LOGIN.value());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void add(String username,
                    String password,
                    String schoolName,
                    String contact,
                    String address,
                    String telephone,
                    String schoolCode,
                    String email) throws UsernameAlreadyExistException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setSchoolname(schoolName);
        user.setContact(contact);
        user.setAddress(address);
        user.setTelephone(telephone);
        user.setSchoolcode(schoolCode);
        user.setEmail(email);
        add(user, RoleEnum.USER.value());
    }

    public void importRegistrationForm(MultipartFile file) throws FileFormattingException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ExcelDataException, EmailNotFoundException, UsernameAlreadyExistException {
        String originalFilename = file.getOriginalFilename();//原本文件的名字
        String format = originalFilename.substring(originalFilename.lastIndexOf("."));
        AssertUtil.isExcel(format);
//        PasswordEncoder md5PasswordEncoder = new MessageDigestPasswordEncoder("MD5");
        Workbook workbook = null;
        if (format.equals(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (format.equals(".xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else {
            throw new FileFormattingException("文件格式不支持，仅支持.xls以及.xlsx");
        }
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            Iterator<Row> rowIterator = sheetAt.iterator();
            if (!rowIterator.hasNext()) {
                continue;
            }
            Row preRow = rowIterator.next();
            Map<Integer, String> info = preConstruct(preRow);
            //第一行用于读取信息，字段信息，（字段名：索引） 相映射，但其值需要与数据库相映射
            while (rowIterator.hasNext()) {
                Row aRow = rowIterator.next();
                Constructor<User> constructor = User.class.getConstructor();
                User user = constructor.newInstance();
                Iterator<Cell> cellIterator = aRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    CellType cellType = cell.getCellType();
                    String fieldValue = null;
                    //需要强转
                    if (cellType == CellType.NUMERIC) {
                        fieldValue = String.valueOf(cell.getNumericCellValue()).trim();
                    } else if (cellType == CellType.STRING) {
                        fieldValue = cell.getStringCellValue().trim();
                    }
                    int columnIndex = cell.getColumnIndex();//该单元格在第几列
                    String fieldName = info.get(columnIndex);//该单元格对应的名字
                    try {
                        invokeValue(user, fieldName, fieldValue);
                    } catch (NoSuchMethodException e) {
                        //没有该set方法说明第一行的字段错了，因此直接抛出错误
                        throw new ExcelDataException("Excel表中第一行字段与数据中的字段不对应！");
                    }
                }
                if (user.getUsername() == null || user.getUsername().trim().equals("")) {
                    //若username出现空缺，跳过该行！
                    continue;
                }
                User byUsername = findByUsername(user.getUsername());
                if (byUsername != null) {
                    System.out.println("该用户名已经被注册过了！");
                    continue;
                }
                String defaultPassword = generateDefaultPassword();
                user.setPassword(defaultPassword);
                emailService.sendVerificationCode("签约系统临时授权码", "签约系统临时授权码(3天内有效，请尽快重设您的密码)", user.getUsername(), 3, TimeUnit.DAYS);
                add(user, RoleEnum.USER.value());
                System.out.println(user.toString());
            }
        }
    }

    private String generateDefaultPassword() {
        String s = String.valueOf(System.currentTimeMillis());
        return s.substring(s.length() - 6);
    }

    private void invokeValue(User user, String fieldName, String fieldValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = User.class.getMethod("set" + fieldName, String.class);
        method.invoke(user, fieldValue);
    }

    private Map<Integer, String> preConstruct(Row aRow) {
        Map<Integer, String> map = new HashMap<>();
        Iterator<Cell> cellIterator = aRow.iterator();
        int index = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            map.put(index++, cell.getStringCellValue());
        }
        return map;
    }


    public Workbook exportRegistrationForm() throws IOException {
        List<User> users = querySelective(null, null, null, null);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);//第0行为字段名
        Map<String, Integer> map = new HashMap<>();
        Field[] declaredFields = User.class.getDeclaredFields();
        int index = 0;
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (fieldName.startsWith("IS_") || fieldName.startsWith("NOT_")) {
                continue;
            }
            map.put(fieldName, index);
            Cell cell = row.createCell(index);
            index++;
            cell.setCellValue(fieldName);
        }
        for (int i = 1; i <= users.size(); i++) {
            Row eachUserRow = sheet.createRow(i);
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Cell cell = eachUserRow.createCell(entry.getValue());
                try {
                    Object value = valueInvoke(users.get(i - 1), entry.getKey());
                    cell.setCellValue(String.valueOf(value));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
//        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\Users\\Administrator\\Desktop\\test.xls")))) {
//            workbook.write(dataOutputStream);
//        }
        return workbook;
    }

    private Object valueInvoke(User user, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = User.class.getMethod("get" + fieldName);
        User user1 = new User();
        return method.invoke(user);
    }
//分页查询
    public List<User> querySelectiveAllByPage(Integer page,
                                         Integer limit){
        UserExample userExample = new UserExample();
        userExample.createCriteria().andDeletedEqualTo(false);
        if (page != null || limit != null) {
            if (page == null) {
                PageHelper.startPage(1, limit);
            } else if (limit == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, limit);
            }
        }
        List<User> users = userMapper.selectByExampleSelective(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return pageInfo.getList();
    }
//    学校信息模糊查询
    public List<User> querySelectiveAllDim(String name,Integer page, Integer limit){
        UserExample userExample1 = new UserExample();
        UserExample userExample2 = new UserExample();
        UserExample userExample3 = new UserExample();
        UserExample userExample4 = new UserExample();
        UserExample userExample5 = new UserExample();
        userExample1.createCriteria().andTelephoneLike("%" + name + "%");
        userExample2.createCriteria().andSchoolnameLike("%" + name + "%");
        userExample3.createCriteria().andContactLike("%" + name + "%");
//        userExample4.createCriteria().andEmailLike("%" + name + "%");
        userExample5.createCriteria().andAddressLike("%" + name + "%");
//        UserExample.Criteria criteria = userExample.createCriteria();
//        criteria.andTelephoneLike("%" + name + "%");
//        userExample.or(criteria.andSchoolnameLike("%" + name + "%"));
//        userExample.or(criteria.andContactLike("%" + name + "%"));
//        userExample.or(criteria.andAddressLike("%" + name + "%"));
//        userExample.or(criteria.andEmailLike("%" + name + "%"));
//        UserExample.Criteria criteria = userExample.createCriteria();
//        criteria.andSchoolnameLike("%" + name + "%");
        List<User> user1 = userMapper.selectByExampleSelective(userExample1);
        List<User> user2 = userMapper.selectByExampleSelective(userExample2);
        List<User> user3 = userMapper.selectByExampleSelective(userExample3);
//        List<User> user4 = userMapper.selectByExampleSelective(userExample4);
        List<User> user5 = userMapper.selectByExampleSelective(userExample5);
        Set set = new HashSet();
        set.addAll(user1);
        set.addAll(user2);
        set.addAll(user3);
//        set.addAll(user4);
        set.addAll(user5);
        List<User> user = new ArrayList<>();
        user.addAll(set);
        if (page != null || limit != null) {
            if (page == null) {
                PageHelper.startPage(1, limit);
            } else if (limit == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, limit);
            }
        }
        PageInfo<User> pageInfo = new PageInfo<>(user);
        return pageInfo.getList();
    }
    public List<User> querySelectiveBySchoolnameDim(String schoolname,Integer page,
                                                    Integer limit){
        UserExample userExample = new UserExample();
        userExample.createCriteria().andSchoolnameLike("%" + schoolname + "%");
        if (page != null || limit != null) {
            if (page == null) {
                PageHelper.startPage(1, limit);
            } else if (limit == null) {
                PageHelper.startPage(page, 10);
            } else {
                PageHelper.startPage(page, limit);
            }
        }
        List<User> users = userMapper.selectByExampleSelective(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return pageInfo.getList();
    }


}
