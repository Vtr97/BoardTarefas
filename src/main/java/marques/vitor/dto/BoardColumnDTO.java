package marques.vitor.dto;

import marques.vitor.persistence.entity.BoardColumnTypeEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnTypeEnum type, int cardsAmount) {

}
