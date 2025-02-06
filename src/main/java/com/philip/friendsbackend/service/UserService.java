package com.philip.friendsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author philip
 */
public interface UserService extends IService<User> {

    /**
     * 使用者註冊
     * @param userAccount 使用者名稱
     * @param userAccount 使用者帳號
     * @param userPassword 使用者密碼
     * @param checkPassword 檢驗密碼
     * @return 使用者 id
     */
    long userRegister(String username, String userAccount, String userPassword, String checkPassword);

    /**
     * 使用者登入
     *
     * @param userAccount  使用者帳號
     * @param userPassword 使用者密碼
     * @param request
     * @return 去除敏感訊息後使用者訊息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 去除使用者敏感資訊
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 使用者登出
     * @param request
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根據標籤搜索使用者
     *
     * @param tagNameList 使用者擁有的標籤
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新使用者資訊
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 獲取目前登入使用者資訊
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    Page<User> getRecommendUsers(long pageSize, long pageNum, HttpServletRequest request);

    /**
     * 尋找相似的使用者
     * @param num
     * @param loginUser
     * @return
     */
    List<UserVO> getMatchUsers(long num, User loginUser);
}
