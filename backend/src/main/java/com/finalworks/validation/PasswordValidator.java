package com.finalworks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordValidator.PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {
    String message() default "Password must be at least 8 characters long and contain at least one uppercase letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class PasswordConstraintValidator implements ConstraintValidator<PasswordValidator, String> {
        @Override
        public void initialize(PasswordValidator constraintAnnotation) {
        }

        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null || password.isEmpty()) {
                return false;
            }
            
            // At least 8 characters
            if (password.length() < 8) {
                return false;
            }
            
            // At least one uppercase letter
            boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
            if (!hasUpperCase) {
                return false;
            }
            
            return true;
        }
    }
}
