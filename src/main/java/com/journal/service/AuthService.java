package com.journal.service;

import com.journal.dao.SysMenuDAO;
import com.journal.dao.SysUserDAO;
import com.journal.exception.BusinessException;
import com.journal.model.SysMenu;
import com.journal.model.SysUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 认证与权限业务服务类，封装用户登录认证与菜单权限查询业务。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 SysUserDAO 和 SysMenuDAO，提供用户登录认证和角色菜单查询
 *   两大核心业务能力。登录时通过用户名查询用户信息，将明文密码
 *   进行 SHA-256 哈希后与数据库存储的哈希值比对，验证通过后
 *   将密码字段置为 null 并返回用户对象（包含角色编码、角色名称、
 *   数据范围等权限控制所需信息）。菜单查询通过角色ID获取该角色
 *   所拥有的菜单列表，用于前端动态渲染导航菜单。
 *
 * 依赖：
 *   com.journal.dao.SysUserDAO
 *   com.journal.dao.SysMenuDAO
 */
public class AuthService {

    /** 系统用户数据访问对象 */
    private final SysUserDAO sysUserDAO = new SysUserDAO();

    /** 系统菜单数据访问对象 */
    private final SysMenuDAO sysMenuDAO = new SysMenuDAO();

    /**
     * 用户登录认证。
     * 根据用户名查询用户信息，将传入的明文密码进行 SHA-256 哈希后
     * 与数据库中存储的密码哈希值比对。验证通过后将密码字段置为 null
     * 并返回用户对象（含角色编码、角色名称、数据范围等权限信息）。
     * 验证失败（用户不存在、密码不匹配）时抛出 BusinessException。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 认证通过的用户对象（密码字段已置 null）
     * @throws BusinessException 如果用户不存在或密码不匹配
     */
    public SysUser login(String username, String password) {
        SysUser user = sysUserDAO.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        String hashedPassword = sha256(password);
        if (!hashedPassword.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 验证通过后清除密码字段，避免泄露
        user.setPassword(null);
        return user;
    }

    /**
     * 获取指定角色的菜单列表。
     * 通过 SysMenuDAO 查询该角色所拥有的全部菜单（按 sort_order 排序）。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param roleId 角色ID
     * @return 该角色的菜单列表
     */
    public List<SysMenu> getUserMenus(int roleId) {
        return sysMenuDAO.findMenusByRoleId(roleId);
    }

    /**
     * 对输入字符串进行 SHA-256 哈希计算，返回64位十六进制小写字符串。
     * 使用 java.security.MessageDigest 实现。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param input 待哈希的明文字符串
     * @return SHA-256 哈希值的十六进制字符串
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("SHA-256 哈希算法不可用", e);
        }
    }
}
