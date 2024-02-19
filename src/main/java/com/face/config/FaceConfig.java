package com.face.config;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.face.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyongpeng
 * <p>des</p>
 **/
@Component
@Slf4j
public class FaceConfig implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String face_token = request.getHeader("face_token");
        Map<String,Object> infoMap = new HashMap<>();
        try{
            //token验证
            DecodedJWT tokenInfo = JwtUtils.getTokenInfo(face_token);
            //添加返回信息
            infoMap.put("msg","验证成功");
            return true;
        }catch (SignatureVerificationException e){
            e.printStackTrace();
            infoMap.put("msg","无效签名");
        }catch (TokenExpiredException e){
            infoMap.put("msg","token已过期");
        }catch (AlgorithmMismatchException e){
            infoMap.put("msg","算法不一致");
        }catch (Exception e){
            infoMap.put("msg","无效签名");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String mapInfoJson = objectMapper.writeValueAsString(infoMap);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(mapInfoJson);
        return false;
    }
}
