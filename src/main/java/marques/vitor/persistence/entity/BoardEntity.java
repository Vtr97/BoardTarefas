package marques.vitor.persistence.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static marques.vitor.persistence.entity.BoardColumnTypeEnum.CANCEL;
import static marques.vitor.persistence.entity.BoardColumnTypeEnum.INITIAL;

@Data
public class BoardEntity {
    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity initialColumn() {
        return getFilteredColumn(bc -> bc.getType().equals(INITIAL));
    }

    public BoardColumnEntity getCancelColums() {
        return getFilteredColumn(bc -> bc.getType().equals(CANCEL));
    }

    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter) {
        return boardColumns.stream().filter(filter).findFirst().orElseThrow();
    }
}
