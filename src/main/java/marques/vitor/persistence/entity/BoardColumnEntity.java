package marques.vitor.persistence.entity;


import lombok.Data;
import lombok.ToString;

@Data
public class BoardColumnEntity {
    private Long id;
    private String name;
    private int order;
    private BoardColumnTypeEnum type;
    private BoardEntity board = new BoardEntity();

}
