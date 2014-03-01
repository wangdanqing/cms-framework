package net.pusuo.cms.web.web;

import net.pusuo.cms.web.util.CaptchaUtil;
import org.patchca.service.Captcha;
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

	@RequestMapping("login")
	public ModelAndView login() {
		ModelAndView m = new ModelAndView("user/login");
		return m;
	}

	@RequestMapping("loginin")
	public ModelAndView loginin(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("username");
		String passwd = request.getParameter("password");
		String captcha = request.getParameter("captcha");

		String verify_captcha = (String) session.getAttribute("key_capt_word");
		ModelAndView m = null;
		//todo
//        if (!captcha.equals(verify_captcha)) {
//            m = new ModelAndView("/user/login");
//            m.addObject("login_error", "验证码错误，请重新登陆");
//            return m;
//        }

		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}

		session.setAttribute("user", "->" + name);

		m = new ModelAndView("index");
		return m;
	}


	@RequestMapping("getCaptcha")
	@ResponseBody
	public void getCaptcha(HttpSession session, HttpServletResponse response) {
		Captcha captcha = CaptchaUtil.getCaptcha();
		session.setAttribute("key_capt_word", captcha.getChallenge());

		try {
			ImageIO.write(captcha.getImage(), "jpg", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
