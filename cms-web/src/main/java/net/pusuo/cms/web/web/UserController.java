package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.auth.User;
import net.pusuo.cms.web.service.UserService;
import net.pusuo.cms.web.util.CaptchaUtil;
import org.patchca.service.Captcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-3-27
 * Time: 上午12:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("user")
public class UserController {
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	private final static String captKey = "key_capt_word";

	private UserService userService = new UserService();

	@RequestMapping("login")
	public ModelAndView login() {
		return new ModelAndView("user/login");
	}

	@RequestMapping("loginin")
	public ModelAndView loginin(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("username");
		String passwd = request.getParameter("password");
		String captcha = request.getParameter("captcha");

		String verify_captcha = (String) session.getAttribute(captKey);

		ModelAndView view = new ModelAndView("/user/login");
		if (verify_captcha != null && !captcha.equals(verify_captcha)) {
			view.addObject("login_error", "验证码错误，请重新输入验证码");
			return view;
		}

		//	清空验证码
		session.removeAttribute(verify_captcha);

		User user = userService.getByUserNamePasswd(name, passwd);
		if (user == null) {
			view.addObject("login_error", "用户名或密码不匹配，请重新输入");
			return view;
		} else {
			session.setAttribute("user", user.getName());
			session.setAttribute("userId", user.getId());
			boolean ret = userService.setLoginToken(user.getId(), session);
			if (!ret) {
				view.addObject("login_error", "登录失败，请重新登录");
				return view;
			}
		}

		return new ModelAndView("forward:/");
	}


	@RequestMapping("getCaptcha")
	@ResponseBody
	public void getCaptcha(HttpSession session, HttpServletResponse response) {
		Captcha captcha = CaptchaUtil.getCaptcha();
		session.setAttribute(captKey, captcha.getChallenge());

		try {
			ImageIO.write(captcha.getImage(), "png", response.getOutputStream());
		} catch (IOException e) {
			logger.error("gen captcha err", e);
		}
	}

}
