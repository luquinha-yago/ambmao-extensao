package web.ambmao.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HttpUrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpUrl {
    String message() default "Informe um link HTTP ou HTTPS válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
