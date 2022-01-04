package com.art.artcommon.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * 统一返回对象
 * 前端请求后端接口，后端去数据库查数据，并不知道业务是否成功
 */

@Data
@NoArgsConstructor
public class IResult extends HashMap<String,Object> {

    private static final String CODE_SUCCESS = "0000";
    private static final String CODE_FAIL = "9999";
    private static final String CODE_NOT_LOGIN = "1000";
    private static final String CODE_EXCEPTION = "9000";
    private static final String CODE_MSG_SUCCESS = "SUCCESS";
    private static final String TOKEN_EXPIRE = "9101";

    private String code;
    private String msg;
    private JSONObject data = new JSONObject();

    private IResult(String code, String msg, JSONObject data){
        this.code=code;
        this.msg=msg;
        this.data=data;
        this.put("code",this.code);
        this.put("msg",this.msg);
        this.put("data",this.data);
    }

    public static IResultBuilder create(){
        return new IResultBuilder();
    }

    public static class IResultBuilder{

        private String code;
        private String msg;
        private JSONObject data = new JSONObject();

        IResultBuilder(){}

        public IResultBuilder success(){
            this.code = CODE_SUCCESS;
            this.msg = CODE_MSG_SUCCESS;
            return this;
        }

        public IResultBuilder fail(){
            this.code = CODE_FAIL;
            return this;
        }

        public IResultBuilder code(String code){
            this.code = code;
            return this;
        }

        public IResultBuilder msg(String msg){
            this.msg = msg;
            return this;
        }

        public IResultBuilder data(JSONObject data){
            this.data = data;
            return this;
        }

        public IResultBuilder add(String k,Object v){
            this.data.put(k,v);
            return this;
        }

        public IResult build(){
            return new IResult(code,msg,data);
        }
    }

    /**
     * 请求接口成功
     * @param data
     * @return
     */
    public static IResult success(JSONObject data){
        return IResult.create().success().data(data).build();
    }

    /**
     * 请求接口失败，默认返回9999
     * @param data
     * @param msg
     * @return
     */
    public static IResult fail(JSONObject data,String msg){
        return IResult.create().fail().msg(msg).data(data).build();
    }

    /**
     * 请求接口失败，返回自定义错误信息
     * @param data
     * @param msg
     * @return
     */
    public static IResult fail(JSONObject data,String msg,String code){
        return IResult.create().fail().code(code).msg(msg).data(data).build();
    }

    /**
     * 判断请求是否为成功
     * @return
     */
    public boolean isSuccess(){
        if (code == null){
            return CODE_SUCCESS.equals(get("code"));
        }
        return CODE_SUCCESS.equals(code);
    }

    /**
     * 判断请求是否为失败
     * @return
     */
    public boolean isFail(){
        return !isSuccess();
    }
}
