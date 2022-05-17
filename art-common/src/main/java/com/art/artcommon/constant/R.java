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
    public static final String SIMULATOR_REQUEST = "/api/simulator/{path}";

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
    public static final String CODE_LOGIN_INVALID = "0013";
}
