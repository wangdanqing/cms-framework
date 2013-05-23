package net.pusuo.cms.web.web;

import net.pusuo.cms.core.bean.Channel;
import net.pusuo.cms.web.service.ChannelService;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-17
 * Time: 下午11:53
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    private final ChannelService channelService = new ChannelService();
    JsonFactory jsonFactory = new JsonFactory();

    @RequestMapping("getall")
    public String getAll(HttpServletRequest request) {
        List<Channel> list = channelService.query(0);

        StringWriter stringWriter = new StringWriter();
        try {
            JsonGenerator g = jsonFactory.createJsonGenerator(stringWriter);
            g.writeStartArray();
            for (Channel ch : list) {
                g.writeStartObject();
                g.writeNumberField("channelId", ch.getId());
                g.writeStringField("name", ch.getName());
                g.writeStringField("dir", ch.getDir());
                g.writeEndObject();
            }
            g.writeEndArray();
            g.close(); // important: will force flushing of output, close underlying output stream

            String ret = g.getOutputTarget().toString();
            System.out.println("menu: " + ret);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping("getbyChannelId")
    public String getByChannelId(HttpServletRequest request) {

        return "";
    }

}
