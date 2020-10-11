package com.school.service.impl;

import com.school.component.security.UserServiceImpl;
import com.school.dao.PicsMapper;
import com.school.exception.UserNotFoundException;
import com.school.model.Pics;
import com.school.model.PicsExample;
import com.school.model.User;
import com.school.utils.FileEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PicsServiceImpl {
    @Value("${file.path}")
    private String filePath;
    @Resource
    private PicsMapper picsMapper;
    @Autowired
    private UserServiceImpl userService;

    public void insert(Integer userId, String fileName, int type) {
        Pics pics = new Pics();
        pics.setUserid(userId);
        pics.setLocation(fileName);
        pics.setType(type);
        pics.setAddTime(LocalDateTime.now());
        pics.setUpdateTime(LocalDateTime.now());
        picsMapper.insertSelective(pics);
    }

    public List<Pics> findByUserId(Integer userId, FileEnum fileEnum) {
        PicsExample picsExample = new PicsExample();
        PicsExample.Criteria criteria = picsExample.createCriteria();
        criteria.andUseridEqualTo(userId);
        criteria.andTypeEqualTo(fileEnum.value());
        criteria.andDeletedEqualTo(false);
        List<Pics> pics = picsMapper.selectByExampleSelective(picsExample);
        return pics;
    }


    public void add(User user, MultipartFile file, FileEnum fileEnum) throws IOException, UserNotFoundException {
        String fileName = file.getOriginalFilename();
        String location = getRandomFileName(fileName);
//        insert(user.getId(), location, fileEnum.value());
        if (fileEnum.equals(FileEnum.AVATAR_URL)) {
            user.setAvatarurl(location);
            userService.update(user);
        } else {
            insert(user.getId(), fileName, fileEnum.value());
        }
        transferTo(file, location);
    }

    private void transferTo(MultipartFile file, String location) throws IOException {
        File fileInServer = new File(filePath + location);
        file.transferTo(fileInServer);
    }

    private String getRandomFileName(String fileName) {
        String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        UUID uuid = UUID.randomUUID();
        return uuid + "." + format;
    }

    public void deleteById(Integer id) {
        Pics pics = picsMapper.selectByPrimaryKey(id);
        delete(pics);
    }

    public void delete(Pics pics) {
        pics.setDeleted(true);
        pics.setUpdateTime(LocalDateTime.now());
        picsMapper.updateByPrimaryKeySelective(pics);
    }

    public Pics upload(Integer userId, FileEnum fileEnum, MultipartFile file) throws IOException {
        List<Pics> logos = findByUserId(userId, fileEnum);
        Pics pics = new Pics();
        String randomFileName = getRandomFileName(file.getOriginalFilename());
        pics.setLocation(randomFileName);
        pics.setUpdateTime(LocalDateTime.now());
        pics.setType(fileEnum.value());
        pics.setUserid(userId);
        transferTo(file, pics.getLocation());
        if (logos.size() != 0) {
            PicsExample picsExample = new PicsExample();
            PicsExample.Criteria criteria = picsExample.createCriteria();
            criteria.andUseridEqualTo(userId);
            criteria.andTypeEqualTo(fileEnum.value());
            picsMapper.updateByExampleSelective(pics, picsExample);
        } else {
            pics.setAddTime(LocalDateTime.now());
            picsMapper.insertSelective(pics);
        }
        return pics;
    }
}
