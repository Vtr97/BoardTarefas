package marques.vitor.dto;

import marques.vitor.persistence.entity.BoardColumnTypeEnum;

public record BoardColumInfoDTO(Long id, int order, BoardColumnTypeEnum type) {
}
