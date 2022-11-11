package com.xiwang.csmall.passport.filter;

import com.alibaba.fastjson2.JSON;
import com.xiwang.csmall.passport.secrity.LoginPrincipal;
import com.xiwang.csmall.passport.web.JsonResult;
import com.xiwang.csmall.passport.web.ServiceCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final int JWT_MIN_LENGTH = 100;
    @Value("${csmall.jwt.secret-key}")
    String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //清楚SecurityContext中原有的数据(认证信息)
        SecurityContextHolder.clearContext();

        // 尝试获取客户端提交请求时可能携带的JWT
        String jwt = request.getHeader("Authorization");
        log.debug("接收到JWT数据：{}", jwt);

        // 判断是否获取到有效的JWT
        if (!StringUtils.hasText(jwt) || jwt.length() < JWT_MIN_LENGTH) {
            // 直接放行
            log.debug("未获取到有效的JWT数据，将直接放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 设置响应的文档类型
        response.setContentType("application/json; charset=utf-8");
        // 尝试解析JWT，从中获取用户的相关数据，例如id、username等
        log.debug("将尝试解析JWT……");
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        } catch (SignatureException e) {
            String message = "非法访问！";
            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_SIGNATURE, message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();
            writer.println(jsonResultString);
            writer.close();
            return;
        } catch (MalformedJwtException e) {
            String message = "非法访问！";
            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_MALFORMED, message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();
            writer.println(jsonResultString);
            writer.close();
            return;
        } catch (ExpiredJwtException e) {
            String message = "登录已过期，请重新登录！";
            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_EXPIRED, message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();
            writer.println(jsonResultString);
            writer.close();
            return;
        } catch (Throwable e) {
            e.printStackTrace(); // 重要
            String message = "服务器忙，请稍后再次尝试！";
            JsonResult jsonResult = JsonResult.fail(ServiceCode.ERR_UNKNOWN, message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();
            writer.println(jsonResultString);
            writer.close();
            return;
        }

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);
        String authoritiesJsonString = claims.get("authorities", String.class);
        log.debug("从JWT中解析得到数据：id={}", id);
        log.debug("从JWT中解析得到数据：username={}", username);
        log.debug("从JWT中解析得到数据：authoritiesJsonString={}", authoritiesJsonString);

        // 准备用于创建认证信息的权限数据
        List<SimpleGrantedAuthority> authorities
                = JSON.parseArray(authoritiesJsonString, SimpleGrantedAuthority.class);

        // 准备用于创建认证信息的当事人数据
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setId(id);
        loginPrincipal.setUsername(username);

        // 创建认证信息
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginPrincipal, null, authorities);
        // 将认证信息存储到SecurityContext中
        log.debug("即将处理将认证信息存储到SecurityContext中");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 放行
        filterChain.doFilter(request, response);
    }

}
