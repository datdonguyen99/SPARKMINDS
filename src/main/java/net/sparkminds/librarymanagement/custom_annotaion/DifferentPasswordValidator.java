package net.sparkminds.librarymanagement.custom_annotaion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;

public class DifferentPasswordValidator implements ConstraintValidator<DifferentPassword, Object> {
    @Override
    public void initialize(DifferentPassword constraintAnnotation) {
        /*
         * initialize if needed
         */
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        ChangePassDto passDto = (ChangePassDto) value;
        return !passDto.getOldPassword().equals(passDto.getNewPassword());
    }
}
