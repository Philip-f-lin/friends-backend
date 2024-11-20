package com.philip.friendsbackend.utils;

import com.philip.friendsbackend.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.philip.friendsbackend.service.impl.UserServiceImpl.USER_LOGIN_STATE;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 獲取使用者
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 判斷是使用者是否存在
        if (user == null){
            // 沒有，需要攔截，設置狀態碼
            response.setStatus(401);
            // 攔截
            return false;
        }
        UserHolder.saveUser((User) user);
        // 有使用者，放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除使用者
        UserHolder.removeUser();
    }
}
