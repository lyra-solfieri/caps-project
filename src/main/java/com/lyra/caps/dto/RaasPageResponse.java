package com.lyra.caps.dto;

import java.util.List;

public record RaasPageResponse(
        List<RaasResponseDTO> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
}
