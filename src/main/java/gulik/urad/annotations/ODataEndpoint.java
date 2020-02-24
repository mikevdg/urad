package gulik.urad.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEndpoint {
    String url() default "odata.svc";

    String namespace() default "";

    String container() default "container";
}
