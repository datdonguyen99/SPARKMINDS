package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sparkminds.librarymanagement.custom_annotaion.DifferentPassword;

@AllArgsConstructor
@Getter
@DifferentPassword
public class ChangePassDto {
    @NotEmpty(message = "Old password field is required")
    @Size(min = 6, max = 20, message = "Old password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=])(?=.*\\d).*$", message = "Invalid old password format")
    private String oldPassword;

    @NotEmpty(message = "New password field is required")
    @Size(min = 6, max = 20, message = "New password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=])(?=.*\\d).*$", message = "Invalid new password format")
    private String newPassword;
}
