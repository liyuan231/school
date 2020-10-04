package com.school.utils;

import com.school.exception.*;
import com.school.model.User;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertUtil {
    public static void emailVerificationCodeNotNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new EmailVerificationCodeNullPointerException(message);
        }
    }

    public static void emailVerificationCodeEquals(boolean expression, String message) {
        if (!expression) {
            throw new EmailVerificationCodeIllegalArgumentException(message);
        }
    }

    public static void usernameNotNull(@Nullable Object o, String message) {
        if (o == null || o.equals("")) {
            throw new UsernameNullPointerException(message);
        }
    }

    /**
     * 检查用户重设密码是邮箱账号是否合法
     * @param username
     * @param message
     */
    public static void isValidMail(String username, String message) {
        usernameNotNull(username, "用户名不应为空！");
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if(!matcher.matches()){
            throw new EmailWrongFormatException(message);
        }
    }

    public static void isExcel(String format) {
        if(format.equals(".xls")||format.equals(".xlsx")){

        }else {
            throw new FileFormattingException("文件格式错误！");
        }
    }

    public static void userNotNull(List<User> users) {
        if(users==null||users.size()==0){
            throw new UserNotFoundException("用户名不存在！");
        }
    }
}
