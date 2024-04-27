package com.face.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    //properties
    private static final String SIGN="adefaceeasyMI89AYISLAJspsl=da`23s56!@ds";

    public static String genereteToken(Map<String,String> mapinfo){
        Calendar instance = Calendar.getInstance();
        //有效期为24小时
        instance.add(Calendar.MINUTE,60*24);
        //map用户存储claim——声明的信息
        Map<String,Object> map=new HashMap<>();
        //生成token
        JWTCreator.Builder builder = JWT.create();
        //添加payload
        mapinfo.forEach(builder::withClaim);
        //指定令牌过期时间
        builder.withExpiresAt(instance.getTime());
        //指定算法-生成token
        return builder.sign(Algorithm.HMAC256(SIGN));
    }

    /**
     * 验证token的合法性
     * @param token token令牌值
     * throw 抛出异常-表示验证失败
     */
    public synchronized static void decodeToken(String token){
        JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }

    /**
     * 获取token信息的方法
     * @param token token令牌值
     * @return DecodedJWT对象
     * throw 抛出异常-表示验证失败
     */
    public synchronized static DecodedJWT getTokenInfo(String token){
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }

}
