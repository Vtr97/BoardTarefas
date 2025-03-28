package marques.vitor.dto;

import java.util.List;

public record BoardInfoDto(Long id, String name, List<BoardColumnDTO> columns) {

}
