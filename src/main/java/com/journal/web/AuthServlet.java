package com.journal.web;

import com.google.gson.JsonObject;
import com.journal.exception.BusinessException;
import com.journal.model.SysMenu;
import com.journal.model.SysUser;
import com.journal.service.AuthService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 认证 Servlet，处理 /api/auth/* 路径的认证与权限相关请求。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   继承 BaseServlet，对接 /api/auth/* 路径，提供用户登录、
 *   获取当前登录用户信息、获取角色菜单列表、用户登出等
 *   RESTful 接口。登录成功后将用户信息存入 HttpSession
 *   （attribute 名 "currentUser"），后续请求通过 Session
 *   判断用户登录状态和权限角色。
 *
 *   路由说明：
 *     POST /api/auth/login   - 用户登录
 *     GET  /api/auth/me      - 获取当前登录用户信息
 *     GET  /api/auth/menus   - 获取当前用户角色的菜单列表
 *     POST /api/auth/logout  - 用户登出
 *
 * 依赖：
 *   com.journal.service.AuthService
 *   com.journal.util.JsonUtil
 *   com.google.gson.Gson
 */
public class AuthServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 认证业务服务 */
    private final AuthService authService = new AuthService();

    /**
     * 处理 GET 请求。
     * GET /api/auth/me     - 获取当前登录用户信息
     * GET /api/auth/menus  - 获取当前用户角色的菜单列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String action = getPathId(req);
            if ("me".equals(action)) {
                handleMe(req, resp);
            } else if ("menus".equals(action)) {
                handleMenus(req, resp);
            } else {
                writeJson(resp, JsonUtil.writeError(404, "未知的认证接口路径"));
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理 POST 请求。
     * POST /api/auth/login   - 用户登录
     * POST /api/auth/logout  - 用户登出
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String action = getPathId(req);
            if ("login".equals(action)) {
                handleLogin(req, resp);
            } else if ("logout".equals(action)) {
                handleLogout(req, resp);
            } else {
                writeJson(resp, JsonUtil.writeError(404, "未知的认证接口路径"));
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理用户登录。
     * 读取请求体 JSON {username, password}，调用 AuthService.login() 验证。
     * 验证成功后将用户信息存入 HttpSession（attribute 名 "currentUser"），
     * 返回用户信息（包含 roleCode、roleName、refId，不含密码）。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        JsonObject json = GSON.fromJson(body, JsonObject.class);
        if (json == null) {
            writeJson(resp, JsonUtil.writeError(400, "请求体不能为空"));
            return;
        }

        String username = getJsonString(json, "username");
        String password = getJsonString(json, "password");
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            writeJson(resp, JsonUtil.writeError(400, "用户名和密码不能为空"));
            return;
        }

        SysUser user = authService.login(username, password);

        // 将用户信息存入 Session
        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", user);

        writeJson(resp, JsonUtil.writeSuccess(user));
    }

    /**
     * 处理获取当前登录用户信息。
     * 从 Session 中获取 "currentUser" 属性，返回用户信息（不含密码）。
     * 如果 Session 中不存在用户信息，返回 401 未授权。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            writeJson(resp, JsonUtil.writeError(401, "未登录或会话已过期"));
            return;
        }
        SysUser user = (SysUser) session.getAttribute("currentUser");
        writeJson(resp, JsonUtil.writeSuccess(user));
    }

    /**
     * 处理获取当前用户角色的菜单列表。
     * 从 Session 获取 currentUser 的 roleId，调用 AuthService.getUserMenus()
     * 返回该角色的菜单列表。如果未登录则返回 401。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleMenus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            writeJson(resp, JsonUtil.writeError(401, "未登录或会话已过期"));
            return;
        }
        SysUser user = (SysUser) session.getAttribute("currentUser");
        List<SysMenu> menus = authService.getUserMenus(user.getRoleId());
        writeJson(resp, JsonUtil.writeSuccess(menus));
    }

    /**
     * 处理用户登出。
     * 调用 session.invalidate() 销毁当前会话，返回成功。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        writeJson(resp, JsonUtil.writeSuccess(null));
    }
}
