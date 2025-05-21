package com.gorkem.livebettingapi.domain.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableEventDTO {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private Long totalElements;
    private List<EventDTO> content;
}
