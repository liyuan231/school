package com.school.utils;

import com.school.exception.*;
import com.school.model.User;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertUtil {
    public static void emailVerificationCodeNotNull(@Nullable Object object, String message) throws EmailVerificationCodeNullPointerException {
        if (object == null) {
            throw new EmailVerificationCodeNullPointerException(message);
        }
    }

    public static void emailVerificationCodeEquals(boolean expression, String message) throws EmailVerificationCodeIllegalArgumentException {
        if (!expression) {
            throw new EmailVerificationCodeIllegalArgumentException(message);
        }
    }

    public static void usernameNotNull(@Nullable Object o, String message) throws UsernameNullPointerException {
        if (o == null || o.equals("")) {
            throw new UsernameNullPointerException(message);
        }
    }

    /**
     * 检查用户重设密码是邮箱账号是否合法
     *
     * @param username
     * @param message
     */
    public static void isValidMail(String username, String message) throws EmailWrongFormatException, UsernameNullPointerException {
        usernameNotNull(username, "用户名不应为空！");
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            throw new EmailWrongFormatException(message);
        }
    }

    public static void isExcel(String format) throws FileFormattingException {
        if (format.equals(".xls") || format.equals(".xlsx")) {

        } else {
            throw new FileFormattingException("文件格式错误！");
        }
    }

    public static void userNotNull(List<User> users) throws UserNotFoundException {
        if (users == null || users.size() == 0) {
            throw new UserNotFoundException("用户名不存在！");
        }
    }

    private static HashSet<String> picSet = new HashSet<>();
    static {
        picSet.add("bmp");
        picSet.add("jpg");
        picSet.add("png");
        picSet.add("tif");
        picSet.add("gif");
        picSet.add("pcx");
        picSet.add("tga");
        picSet.add("exif");
        picSet.add("fpx");
        picSet.add("svg");
        picSet.add("psd");
        picSet.add("cdr");
        picSet.add("pcd");
        picSet.add("dxf");
        picSet.add("ufo");
        picSet.add("eps");
        picSet.add("ai");
        picSet.add("raw");
        picSet.add("wmf");
        picSet.add("webp");
        picSet.add("avif");
    }

    public static void isPicture(String format) throws FileFormattingException {
        if(!picSet.contains(format)){
            throw new FileFormattingException("文件格式错误");
        }
    }
}
