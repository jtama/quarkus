package io.quarkus.it.rest.client.main;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MessageSerializer extends StdSerializer<HelloClient.Message> {
    public MessageSerializer(Class<HelloClient.Message> t) {
        super(t);
    }

    @Override
    public void serialize(HelloClient.Message value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("message", "test");
        gen.writeEndObject();
    }
}
