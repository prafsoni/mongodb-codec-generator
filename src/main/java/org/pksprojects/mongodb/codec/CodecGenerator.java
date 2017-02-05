package org.pksprojects.mongodb.codec;

import org.bson.codecs.Codec;
import org.pksprojects.mongodb.codec.annotations.Document;
import org.pksprojects.mongodb.codec.codecs.CodecImpl;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates MongoDB Codec(s) for class(es) annotated as @Document.
 * @author Prafull Kumar Soni
 * Created by PKS on 2/3/2017.
 */
public class CodecGenerator {
    private Set<Class<?>> classSet;

    private CodecGenerator(){}

    /**
     * Creates instance of CodecGenerator.
     * @param path String path to project root directory.
     */
    public CodecGenerator(String path){
        classSet = new Reflections(path).getTypesAnnotatedWith(Document.class);
    }

    /**
     * Get's CodecRegistry containing Codec for all the class annotated with @Document.
     * @return CodecRegistry.
     */
    public CodecRegistry getCodecRegistry(){
        return CodecRegistries.fromCodecs(getCodec().stream().collect(Collectors.toList()));
    }

    private Set<Codec<?>> getCodec(){
        return classSet.stream()
                .map(this::getCodecForClass)
                .collect(Collectors.toSet());
    }

    private Codec<?> getCodecForClass(Class<?> c){
        return new CodecImpl<>(c);
    }
}
