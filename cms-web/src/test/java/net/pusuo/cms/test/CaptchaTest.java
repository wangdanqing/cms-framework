package net.pusuo.cms.test;

import net.pusuo.cms.web.util.CaptchaUtil;
import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.*;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sjk on 20/7/14.
 */
public class CaptchaTest {
	public static void main(String[] args) throws IOException {
		Captcha captcha = CaptchaUtil.getCaptcha();
		File f = new File("/tmp/a.png");
		if (f.exists()) {
			f.delete();
		}

		ImageIO.write(captcha.getImage(), "png", f);
	}

	private static void test() throws IOException {
		for (int counter = 0; counter < 5; counter++) {
			ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
			cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));

			SingleColorBackgroundFactory bgc = new SingleColorBackgroundFactory();
			bgc.setColorFactory(new SingleColorFactory(new Color(170, 170, 170)));
			cs.setBackgroundFactory(bgc);
			switch (counter % 5) {
				case 0:
					cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
					break;
				case 1:
					cs.setFilterFactory(new MarbleRippleFilterFactory());
					break;
				case 2:
					cs.setFilterFactory(new DoubleRippleFilterFactory());
					break;
				case 3:
					cs.setFilterFactory(new WobbleRippleFilterFactory());
					break;
				case 4:
					cs.setFilterFactory(new DiffuseRippleFilterFactory());
					break;
			}

			FileOutputStream fos = new FileOutputStream("patcha_demo" + counter + ".png");
			EncoderHelper.getChallangeAndWriteImage(cs, "png", fos);
			fos.close();
		}
	}
}
