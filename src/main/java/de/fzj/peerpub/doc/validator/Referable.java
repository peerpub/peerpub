package de.fzj.peerpub.doc.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ReferableValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Referable {
    String message() default "{referable.notmatching}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String allowedRegex() default "^[a-zA-Z0-9\\-_]+$";
}
