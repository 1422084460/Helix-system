package com.art.artweb.interceptor;

import com.art.artcommon.constant.CustomException;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IPManager;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artcommon.mapper.IPManagerMapper;
import com.art.artcommon.utils.JWTUtils;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.SpringContextHolder;
import com.art.artcommon.utils.Tools;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MasterInterceptor extends HandlerInterceptorAdapter {
    //拦截器于过滤器后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        String ip = Tools.getIpAddr(request);
        if (!ifInBlackList(ip)) {
            if (!RedisUtil.hasKey(ip+"_access")){
                boolean ips = ifRealOne(ip);
                if (!ips) {
                    addToIPM(ip);
                    throw new CustomException(R.CODE_BAD_REQUEST, R.MSG_BAD_REQUEST);
                }
            }else {
                throw new CustomException(R.CODE_BAD_REQUEST_AGAIN, R.MSG_BAD_REQUEST_AGAIN+RedisUtil.getExpire(ip+"_access")+"秒后再试");
            }
        }else {
            throw new CustomException(R.CODE_ACCESS_DENIED, R.MSG_ACCESS_DENIED);
        }

        String token = request.getHeader("token");
        if (token == null){
            throw new CustomException(R.CODE_FAIL, "用户未登录");
        }
        IResult res;
        String name = Thread.currentThread().getName();
        try {
            JWTUtils.verify(token);
            res = IResult.success(null);
            Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", res));
            return true;
        }catch (TokenExpiredException e){
            log.error("用户"+name+"访问："+request.getRequestURI()+"接口异常===>>"+e.getMessage());
            res = IResult.fail(null,R.MSG_TOKEN_EXPIRE,R.CODE_TOKEN_EXPIRE);
            if (request.getRequestURI().equals("/api/user/login")){
                Store.getInstance().put(name,Store.getInstance().MainDataPut("token验证", res));
                return true;
            }
            throw new TokenExpiredException(R.MSG_TOKEN_EXPIRE);
        }
    }

    private boolean ifRealOne(String ip){
        if (!RedisUtil.hasKey(ip)){
            RedisUtil.set(ip,"0",10, TimeUnit.SECONDS);
        }
        Long nums = RedisUtil.inc(ip,false);
        RedisUtil.setExpire(ip,10, TimeUnit.SECONDS);
        if (nums > 10){
            RedisUtil.set(ip+"_access","access_denied",60,TimeUnit.MINUTES);
            return false;
        }
        return true;
    }

    private IPManagerMapper ipManagerMapper = SpringContextHolder.getBean(IPManagerMapper.class);

    private void addToIPM(String ip){
        QueryWrapper<IPManager> wrapper = new QueryWrapper<>();
        wrapper.eq("ip",ip);
        IPManager one = ipManagerMapper.selectOne(wrapper);
        if (one == null){
            IPManager ipManager = new IPManager()
                    .setIp(ip)
                    .setCount(1)
                    .setBlacklist(false);
            ipManagerMapper.insert(ipManager);
        }else if (one.getCount()<3 && !one.isBlacklist()){
            int count = one.getCount();
            int newCount = count+1;
            UpdateWrapper<IPManager> wrapper1 = new UpdateWrapper<>();
            wrapper1.set("count",newCount).eq("ip",ip);
            ipManagerMapper.update(null,wrapper1);
        }else if (one.getCount()==3 && !one.isBlacklist()){
            UpdateWrapper<IPManager> wrapper1 = new UpdateWrapper<>();
            wrapper1.set("blacklist",true).eq("ip",ip);
            ipManagerMapper.update(null,wrapper1);
            RedisUtil.setHash("blacklist",ip,"true",0,TimeUnit.SECONDS);
        }
    }

    private boolean ifInBlackList(String ip){
        return RedisUtil.hasHashKey("blacklist", ip);
    }
}
