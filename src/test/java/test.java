import com.school.Application;
import com.school.component.security.UserServiceImpl;
import com.school.exception.EmailNotFoundException;
import com.school.exception.ExcelDataException;
import com.school.exception.UsernameAlreadyExistException;
import com.school.model.User;
import com.school.service.impl.EmailServiceImpl;
import com.school.utils.FileUtil;
import com.school.utils.IpUtil;
import com.school.utils.RoleEnum;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class test {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> map = new HashMap<>();
        map.put("1", "one");
        map.put("1", "two");
        System.out.println(map.get("1"));


        ExpiringMap<String, String> expiringMap = ExpiringMap.builder().variableExpiration().build();
//        expiringMap.
        expiringMap.put("1", "2", 2, TimeUnit.SECONDS);
        System.out.println(expiringMap.get("1"));
        Thread.sleep(5000);
        System.out.println(expiringMap.get("1"));
    }

    //    @Test
    public void test1() {
        ExpiringMap<String, String> expiringMap = ExpiringMap.builder().variableExpiration().build();
        expiringMap.put("1", "11");
        expiringMap.put("2", "22");

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                expiringMap.put("i" + finalI, "i" + finalI);
            }).start();
        }

        System.out.println(expiringMap);
    }

    //    @Autowired
    private EmailServiceImpl emailService;

    //    @Test
    public void test2() throws Exception, EmailNotFoundException {
//        emailService.modifySystemEmail("2812329425@qq.com","ilkanyshlsqcdefc");
//        emailService.sendVerificationCode("1987151116@qq.com");
    }


    //    @Test
    public void test3() throws Exception, EmailNotFoundException, ExcelDataException {
        InputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("C:\\Users\\Administrator\\Desktop\\test.xlsx")));
        MultipartFile multipartFile = new MockMultipartFile("name", inputStream);
        FileUtil fileUtil = new FileUtil();
//        fileUtil.importExcelToDb(multipartFile, ".xls");
    }


    //    @Test
    public void test5() {
        MessageDigestPasswordEncoder md5PasswordEncoder = new MessageDigestPasswordEncoder("MD5");
        System.out.println(md5PasswordEncoder.encode("504148"));
//        System.out.println(md5PasswordEncoder.matches("123456", "e10adc3949ba59abbe56e057f20f883e"));
    }

    //    @Test
    public void test6() {
        String name = "1.png";
        int i = name.lastIndexOf(".");
        System.out.println(name.substring(i + 1));
    }

    @Test
    public void test7() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.matches("123456",
                "$2a$10$zQamlik./STBRPvTwzrpd.j5LX935j1mMLLvR/CDZCyjYN4kP/lQi"));
//        $2a$10$bPCsW2KzDii0rEosBGYDve79lmV6uF20wipJYz8R0vdkFEnuGbJEa
    }

    @Test
    public void test8() {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(LocalDateTime.now());
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void test9() {
        String s = IpUtil.retrieveCity("175.24.4.196");
    }


    @Autowired
    UserServiceImpl userService;

    @Test
    public void insertTmpUsers() throws UsernameAlreadyExistException {
        for (int i = 0; i < 77; i++) {
            User user = new User();
            user.setUsername(i + "@qq.com");
            user.setSchoolname("学校" + i);
            user.setAddress("学校详细地址" + i);
            user.setContact("联系人" + i);
            user.setEmail("学校邮箱" + i);
            user.setTelephone("学校电话" + i);
            user.setPassword("123");
            userService.add(user, RoleEnum.USER.value());
        }

    }
}
