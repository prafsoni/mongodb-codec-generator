package org.pksprojects.mongodb.codecs;

import org.pksprojects.mongodb.mapper.DocumentMapper;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;

/**
 * Generic class implementation of Codec<T> interface.
 * @author Prafull Kumar Soni
 * Created by PKS on 2/3/2017.
 */
public class ClassCodec<T> implements Codec<T> {

    private Class<T> tClass;

    private static final DocumentCodec codec = new DocumentCodec();

    public ClassCodec(Class<T> tClass){
        this.tClass = tClass;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codec.decode(reader, decoderContext);
        return DocumentMapper.getNewInstanceFrom(document,tClass);
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        Document document = DocumentMapper.getDocument(value);
        codec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<T> getEncoderClass() {
        return tClass;
    }
}
