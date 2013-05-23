package net.pusuo.cms.test;


import net.pusuo.cms.web.service.ChannelService;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-18
 * Time: 上午12:15
 * To change this template use File | Settings | File Templates.
 */
public class TestJSon {
    public static void main(String... args) {
        ChannelService channelService = new ChannelService();
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        try {
            JsonGenerator g = jsonFactory.createJsonGenerator(stringWriter);
            g.writeStartArray();
            for (int i = 0; i < 2; i++) {
                g.writeStartObject();
                g.writeNumberField("channelId", i);
                g.writeStringField("name", "a " + i);
                g.writeStringField("dir", "b " + i);

                g.writeObjectFieldStart("meta");
                g.writeStringField("a", "=======");
                g.writeStringField("b", "bbbbbbb");
                g.writeEndObject();

                g.writeEndObject();
            }
            g.writeEndArray();

            g.close(); // important: will force flushing of output, close underlying output stream

            String ret = g.getOutputTarget().toString();
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyyMMddhh");
        System.out.println(format.format(new Date()));
    }
}
