package net.sparkminds.librarymanagement.custom_annotaion;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = DifferentPasswordValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentPassword {
    String message() default "Old password and new password must be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
