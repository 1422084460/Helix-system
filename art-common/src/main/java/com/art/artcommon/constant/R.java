package com.art.artcommon.constant;

/**
 *  定义常量
 */
public class R {
    /**
     * 结果相关
     */
    //成功
    public static final String CODE_SUCCESS = "0000";
    //失败
    public static final String CODE_FAIL = "9999";
    //未登陆
    public static final String CODE_NOT_LOGIN = "1000";
    //异常
    public static final String CODE_EXCEPTION = "9000";
    //成功信息
    public static final String CODE_MSG_SUCCESS = "SUCCESS";

    /**
     * token相关
     */
    //token过期
    public static final String CODE_TOKEN_EXPIRE = "9101";
    //token签名无效
    public static final String CODE_TOKEN_SIGNATURE_INVALID = "9102";
    //token无效
    public static final String CODE_TOKEN_INVALID = "9103";
    //token算法不一致
    public static final String CODE_TOKEN_ALGORITHM_MISMATCH = "9104";
    //token过期
    public static final String MSG_TOKEN_EXPIRE = "token过期";
    //token签名无效
    public static final String MSG_TOKEN_SIGNATURE_INVALID = "token签名无效";
    //token无效
    public static final String MSG_TOKEN_INVALID = "token无效";
    //token算法不一致
    public static final String MSG_TOKEN_ALGORITHM_MISMATCH = "token算法不一致";

    /**
     * 操作相关
     */
    public static final String USER_LOGIN = "用户登录";
    public static final String USER_LOGOUT = "用户退出";
    public static final String USER_CHANGE_PWD = "用户修改密码";
    public static final String USER_CANCEL = "用户注销";
    public static final String USER_CALL = "用户访问接口";

    /**
     * 请求相关
     */
    public static final String CODE_BAD_REQUEST = "9200";
    public static final String MSG_BAD_REQUEST = "请求过于频繁，稍后再试";
    public static final String CODE_BAD_REQUEST_AGAIN = "9201";
    public static final String MSG_BAD_REQUEST_AGAIN = "请求过于频繁，请于";
    public static final String CODE_ACCESS_DENIED = "9202";
    public static final String MSG_ACCESS_DENIED = "拒绝访问";

    /**
     * 文件相关
     */
    public static final String SAVE_DATA_PATH = "/usr/local/simulator/saveData.txt";

    /**
     * 验证相关
     */
    public static final String CODE_VERIFY_SUCCESS = "0010";
    public static final String CODE_VERIFY_FAIL = "0011";
    public static final String CODE_VERIFY_EXPIRE = "0012";

    /**
     * 用户相关
     */
    public static final String LOGIN_INVALID = "用户登录失效";
    public static final String CODE_LOGIN_INVALID = "0103";
    public static final String REGISTER_EMAIL_REPEAT = "用户当前邮箱已被注册";
    public static final String CODE_REGISTER_EMAIL_REPEAT = "0104";
    //以密码进行登录
    public static final String CODE_LOGIN_WITH_PWD = "0200";
    //以验证码进行登录
    public static final String CODE_LOGIN_WITH_CODE = "0201";
}
