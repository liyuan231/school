import com.google.common.net.HttpHeaders;
import com.school.Application;
import com.school.component.jwt.JwtTokenGenerator;
import com.school.component.security.UserServiceImpl;
import com.school.exception.UsernameAlreadyExistException;
import com.school.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class ApplicationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;


    //    @Test
    public void login() throws Exception {
        String body = "{\"username\":\"1987151116@qq.com\",\"password\":\"011588\"}";
        MockHttpServletRequestBuilder header = MockMvcRequestBuilders.request(HttpMethod.POST, "/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(body);
        ResultActions perform = mockMvc
                .perform(header);
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }

    //    @Test
    public void testAdministrator() throws Exception {
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWRpZW5jZSI6IjE5ODcxNTExMTZAcXEuY29tIiwic3Vic2NyaWJlciI6ImFsbCIsInJvbGVzIjoiW1wiQURNSU5JU1RSQVRPUlwiXSIsImV4cGlyYXRpb24iOiIyMDIwLTEwLTEyIDE4OjQxOjQ2IiwiaXNzdWVBdCI6IjIwMjAtMTAtMDIgMTg6NDE6NDYiLCJpc3N1ZXIiOiJMaXl1YW4ifQ.38kHW45dr38V7aZD5PVNv-4VvDFPVsf90AXtwsX6nNStJEobx7q9ogKeZX2Md1D7e72iQi7JWexnkvMHmWIOUPWld-ugaH8Pbe_6o2yXpgrcF_OrCFf3psh9XA5CpcWomUxZbvTVc4hiXSZp7nNB4IY0kxZtivJEmYTa3TQKlSc";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.GET, "/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        ResultActions perform = mockMvc.perform(requestBuilder);
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }

    //    @Test
    public void testUsersList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.GET, "/user/list?page=1&limit=10&sort=add_time");
        ResultActions perform = mockMvc.perform(requestBuilder);
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }

    //    @Test
    public void retrieveAllUsers() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(HttpMethod.GET, "/admin/file/retrieveAllUsers");
        ResultActions perform = mockMvc.perform(request);
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }


    //    @Test
    public void sign() throws Exception {
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWRpZW5jZSI6IjE5ODcxNTExMTZAcXEuY29tIiwic3Vic2NyaWJlciI6ImFsbCIsInJvbGVzIjoiW1wiQURNSU5JU1RSQVRPUlwiXSIsImV4cGlyYXRpb24iOiIyMDIwLTEwLTEyIDE4OjQxOjQ2IiwiaXNzdWVBdCI6IjIwMjAtMTAtMDIgMTg6NDE6NDYiLCJpc3N1ZXIiOiJMaXl1YW4ifQ.38kHW45dr38V7aZD5PVNv-4VvDFPVsf90AXtwsX6nNStJEobx7q9ogKeZX2Md1D7e72iQi7JWexnkvMHmWIOUPWld-ugaH8Pbe_6o2yXpgrcF_OrCFf3psh9XA5CpcWomUxZbvTVc4hiXSZp7nNB4IY0kxZtivJEmYTa3TQKlSc";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(HttpMethod.GET, "/user/sign?signedUserId=24"); //  .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        ResultActions perform = mockMvc.perform(requestBuilder);
        System.out.println(perform.andReturn().getResponse().getContentAsString());
    }

    @Autowired
    UserServiceImpl userService;

    //    @Test
    public void insertUsersForTest() throws UsernameAlreadyExistException {
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setUsername("school-" + i);
            user.setAvatarurl("school_avatar-" + i);
            user.setSchoolname("这是学校名字-" + i);
            userService.add(user);
        }
    }

    @Test
    public void testLogin() throws Exception {
        MessageDigestPasswordEncoder messageDigestPasswordEncoder = new MessageDigestPasswordEncoder("MD5");
        String username = "1987151116@qq.com";
        String password = messageDigestPasswordEncoder.encode("164304");
        System.out.println(password);
        System.out.println(messageDigestPasswordEncoder.encode("164304"));
//        String body = "{\"username\":\"1987151116@qq.com\",\"password\":\"" +
//                password +
//                "\"}";
//        MockHttpServletRequestBuilder header = MockMvcRequestBuilders.request(HttpMethod.POST, "/login")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .content(body);
//        ResultActions perform = mockMvc
//                .perform(header);
//        System.out.println(perform.andReturn().getResponse().getContentAsString());
//        System.out.println();
    }


}
