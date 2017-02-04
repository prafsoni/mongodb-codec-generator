package org.pksprojects.mongodb.codec.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker Annotation for identifying field(s) not to be stored in MongoDB.
 * @author Prafull Kumar Soni
 * Created by PKS on 2/3/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
