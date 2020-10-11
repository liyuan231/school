package com.school.utils;

import com.school.component.security.UserServiceImpl;
import com.school.dao.UserMapper;
import com.school.dao.UsertoroleMapper;
import com.school.exception.EmailNotFoundException;
import com.school.exception.ExcelDataException;
import com.school.model.User;
import com.school.model.UserExample;
import com.school.model.Usertorole;
import com.school.service.impl.EmailServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Component
//@Lazy
//@Transactional
public class FileUtil {
    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailServiceImpl emailService;

    @Resource
    private UsertoroleMapper usertoroleMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserServiceImpl userService;


    public void exportDbToExcel(HttpServletRequest request, HttpServletResponse response, String fileName) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);//第一行比较特殊
        Field[] declaredFields = User.class.getDeclaredFields();
        Map<String, Integer> map = new HashMap<>();
        int c = 0;
        for (Field declaredField : declaredFields) {
            String filedName = declaredField.getName();
            map.put(filedName, c++);
            Cell cell = row.createCell(c);
            cell.setCellValue(filedName);
        }
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo("*");
        List<User> users = userMapper.selectByExample(userExample);
        int rowIndex = 1;
        for (User user : users) {
            Row row1 = sheet.createRow(rowIndex++);
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Cell cell = row1.createCell(entry.getValue());
                try {
                    cell.setCellValue(String.valueOf(valueInvoke(user, entry.getKey())));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("application/x-download");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1") + ".xls");
        workbook.write(outputStream);
        outputStream.flush();
    }

    private Object valueInvoke(User user, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = User.class.getMethod("get" + fieldName, String.class);
        return method.invoke(user);
    }
}
