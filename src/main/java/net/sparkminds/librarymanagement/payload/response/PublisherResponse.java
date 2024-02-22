package net.sparkminds.librarymanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sparkminds.librarymanagement.entity.Publisher;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PublisherResponse {
    private Long id;

    private String name;

    private String address;

    public static PublisherResponse buildPublisherResponse(Publisher publisher) {
        return PublisherResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .address(publisher.getAddress())
                .build();
    }
}
