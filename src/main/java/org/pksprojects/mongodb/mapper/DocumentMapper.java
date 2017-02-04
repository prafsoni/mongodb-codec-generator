package org.pksprojects.mongodb.mapper;

import org.pksprojects.mongodb.annotations.Id;
import org.pksprojects.mongodb.annotations.Ignore;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for mapping Class T to-from Document class.
 * @author Prafull Kumar Soni
 * Created by PKS on 2/3/2017.
 */
public class DocumentMapper {
    public static <T> Document getDocument(T t){

        List<Field> idFields = Arrays.stream(t.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(DocumentMapper::makeAccessible)
                .collect(Collectors.toList());

        Document document = initializeDocument(idFields, t);

        Arrays.stream(t.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) == null)
                .filter(field -> field.getAnnotation(Ignore.class) == null)
                .map(DocumentMapper::makeAccessible)
                .forEach(field -> {
                    try {
                        document.append(field.getName(), field.get(t));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw  new IllegalAccessError("Unhandled Error");
                    }
                });

        return document;
    }

    private static Field makeAccessible(Field field){
        field.setAccessible(true);
        return field;
    }

    private static <T> Document initializeDocument(List<Field> idFields, T t){
        switch (idFields.size()){
            case 0 :
                return new Document();
            case 1 :
                try {
                    if (idFields.get(0).get(t) != null & idFields.get(0).getType().equals(ObjectId.class)){
                        return new Document("_id", new ObjectId(idFields.get(0).get(t).toString()));
                    }else if(idFields.get(0).get(t) == null){
                        return new Document("_id", new ObjectId());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Only one @Id field allowed per @Document class");
        }
        return null;
    }

    public static <T> T getNewInstanceFrom(Document document, Class<T> tClass){
        try {
            T t = tClass.newInstance();
            Arrays.stream(tClass.getDeclaredFields())
                    .filter(field -> field.getAnnotation(Id.class) == null)
                    .filter(field -> field.getAnnotation(Ignore.class) == null)
                    .map(DocumentMapper::makeAccessible)
                    .forEach(field -> {
                        try {
                            field.set(t, document.get(field.getName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });

            List<Field> idFields = Arrays.stream(tClass.getDeclaredFields())
                    .filter(field -> field.getAnnotation(Id.class) != null)
                    .map(DocumentMapper::makeAccessible)
                    .collect(Collectors.toList());

            switch (idFields.size()){
                case 0:
                    break;
                case 1:
                    if (idFields.get(0).getAnnotation(Id.class) != null & document.get("_id") != null) {
                        if (idFields.get(0).getType().equals(ObjectId.class))
                            idFields.get(0).set(t, document.getObjectId("_id"));
                        else
                            idFields.get(0).set(t, document.getObjectId("_id").toString());
                    }
                    break;
                default:
                    throw new IllegalStateException("Only one @Id field allowed per @Document class");
            }

            return t;
        } catch (InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }
}
