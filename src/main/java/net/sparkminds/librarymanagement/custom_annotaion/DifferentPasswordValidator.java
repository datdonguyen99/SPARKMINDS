package net.sparkminds.librarymanagement.custom_annotaion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;

public class DifferentPasswordValidator implements ConstraintValidator<DifferentPassword, Object> {
    @Override
    public void initialize(DifferentPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof ChangePassDto passDto) {
            return !passDto.getOldPassword().equals(passDto.getNewPassword());
        }
        return true;
    }
}
