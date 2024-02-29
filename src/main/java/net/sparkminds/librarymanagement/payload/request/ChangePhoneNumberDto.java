package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePhoneNumberDto {
    @NotEmpty(message = "Phone number field is required")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9\\d)\\d{7}$", message = "Invalid phone number")
    private String phoneNumber;
}
