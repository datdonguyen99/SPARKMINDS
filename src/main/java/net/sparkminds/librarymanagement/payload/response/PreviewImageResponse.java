package net.sparkminds.librarymanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PreviewImageResponse {
    private String imgPath;
}
