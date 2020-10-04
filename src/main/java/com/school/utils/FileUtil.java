package com.school.utils;

import com.school.dao.UserMapper;
import com.school.dao.UsertoroleMapper;
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
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
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

@Component
@Lazy
@Transactional
public class FileUtil {
    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailServiceImpl emailService;

    @Resource
    private UsertoroleMapper usertoroleMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;


    /**
     * @param file   该文件
     * @param format 该文件后缀，以区分该文件是xls或xlsx
     */
    public void importExcelToDb(MultipartFile file, String format) throws Exception {
//        PasswordEncoder md5PasswordEncoder = new MessageDigestPasswordEncoder("MD5");
        Workbook workbook = null;
        if (format.equals(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (format.equals(".xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else {
            throw new IllegalStateException("文件格式不支持，仅支持.xls以及.xlsx");
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
                String defaultPassword = generateDefaultPassword();
                user.setPassword(defaultPassword);
                System.out.println(user.toString());
                emailService.sendDefaultPassword(user);
                user.setAddTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                //在此处先进行一次MD5加密
//                user.setPassword(md5PasswordEncoder.encode(user.getPassword()));
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                userMapper.insertSelective(user);//此处之后还要插入 userToRole
                UserExample userExample = new UserExample();
                userExample.createCriteria().andUsernameEqualTo(user.getUsername());
                User userInDb = userMapper.selectOneByExampleSelective(userExample);
                Usertorole usertorole = new Usertorole();
                usertorole.setRoleid(1);
                usertorole.setUserid(userInDb.getId());
                usertoroleMapper.insert(usertorole);//插入用户-角色对应表
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
