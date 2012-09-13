package net.pusuo.cms.impress.sync.img;

import net.pusuo.cms.impress.sync.SpringContainer;
import net.pusuo.cms.impress.sync.task.TaskRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImgCompressMain {
    private static final Log log = LogFactory.getLog(ImgCompressMain.class);

    private static final String IMG_COMPRESS = "imgCompress";

    /**
     * @param args
     */
    public static void main(String[] args) {
        String appConfig = null;
        if (args != null && args.length > 0) {
            appConfig = args[0];
        }

        SpringContainer sc = null;
        if (appConfig != null) {
            sc = SpringContainer.getInstance(appConfig);
        } else {
            sc = SpringContainer.getInstance();
        }
        TaskRunner icp = (TaskRunner) sc.getApplicationContext().getBean(
                IMG_COMPRESS);
        if (icp == null) {
            log.warn("Can't find ImgCompress with name " + IMG_COMPRESS
                    + ",exit");
            System.exit(1);
        }
        System.out.println("ImgCompress run.....");
        log.info("ImgCompress run.....");
        icp.run();
        log.info("ImgCompress exit.");
    }

}
