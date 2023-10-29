package com.damon.demo.adapter.config;

import cn.hutool.core.util.StrUtil;
import com.damon.demo.common.CommonConstant;
import com.damon.demo.common.TransmittableContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截用户信息用于设置到系统上下中
 *
 * @author xianpinglu
 */
@Component
@Slf4j
public class ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String trafficFlag = request.getHeader(CommonConstant.TRAFFIC_FLAG);
        if (StrUtil.isNotEmpty(trafficFlag)) {
            TransmittableContext.put(CommonConstant.TRAFFIC_FLAG, trafficFlag);
        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof UserContext) {
//                UserContext userContext = (UserContext) principal;
//                TransmittableContext.put(CommonConstant.USER_NAME, userContext.getUsername());
//                TransmittableContext.put(CommonConstant.USER_NUM, userContext.getUcUserName());
//            }
//        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TransmittableContext.clear();
        }
    }
}
