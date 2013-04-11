package net.pusuo.cms.web.util;

import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-9
 * Time: 下午9:10
 * To change this template use File | Settings | File Templates.
 */
public class CaptchaUtil {
    private final static ConfigurableCaptchaService cs = new ConfigurableCaptchaService();

    static {
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setBackgroundFactory(new SingleColorBackgroundFactory());
//        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
        cs.setFilterFactory(new WobbleRippleFilterFactory());
    }

    public static Captcha getCaptcha() {
        return cs.getCaptcha();
    }
}
