package org.jamm.message;


import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.sf.json.JSONObject;
import org.jamm.pipeline.JsonMessageDecoder;
import org.jamm.pipeline.JsonMessageEncoder;
import org.jamm.session.Session;

public class JsonMessage extends AbstractMessage {
    private JSONObject body;

    public JsonMessage() {
        body = new JSONObject();
    }

    public JsonMessage(String jsonStr) {
        body = JSONObject.fromObject(jsonStr);
    }

    public JSONObject getBody() {
        return body;
    }

//    @Override
    public String buildPacketMessage() {
        return body.toString();
    }

    @Override
    public MessageToMessageDecoder getDecoder() {
        return new JsonMessageDecoder();
    }

    @Override
    public MessageToMessageEncoder getEncoder() {
        return new JsonMessageEncoder();
    }
}
