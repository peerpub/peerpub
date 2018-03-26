package de.fzj.peerpub.doc.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Custom annotation to validate referable names. Restricting to /^[a-zA-Z0-9\-_]+$/
 * leaves readable names within MongoDB and allows easy references (DBRef not usable everywhere)
 */
@Documented
@Constraint(validatedBy = ReferableValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Referable {
    String message() default "{Referable.notmatching}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String allowedRegex() default "^[a-zA-Z0-9\\-_]+$";
}
