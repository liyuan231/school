import com.school.service.impl.EmailServiceImpl;
import com.school.utils.FileUtil;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@SpringBootTest(classes = {Application.class})
//@RunWith(SpringJUnit4ClassRunner.class)
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
    public void test2() throws Exception {
//        emailService.modifySystemEmail("2812329425@qq.com","ilkanyshlsqcdefc");
        emailService.sendVerificationCode("1987151116@qq.com");
    }


//    @Test
    public void test3() throws Exception {
        InputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("C:\\Users\\Administrator\\Desktop\\test.xlsx")));
        MultipartFile multipartFile = new MockMultipartFile("name", inputStream);
        FileUtil fileUtil = new FileUtil();
        fileUtil.importExcelToDb(multipartFile, ".xls");
    }


    @Test
    public void test5(){
        MessageDigestPasswordEncoder md5PasswordEncoder = new MessageDigestPasswordEncoder("MD5");
        System.out.println(md5PasswordEncoder.encode("504148"));
//        System.out.println(md5PasswordEncoder.matches("123456", "e10adc3949ba59abbe56e057f20f883e"));
    }



}
