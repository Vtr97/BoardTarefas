package marques.vitor.persistence.dao;

import lombok.AllArgsConstructor;
import marques.vitor.dto.CardInfoDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.Optional;

@AllArgsConstructor
public class CardDao {
    private Connection connection;

    public Optional<CardInfoDTO> findById(final Long id) throws SQLException {
        var sql = """
                    SELECT  c.id,
                            c.title,
                            c.description, 
                            c.created_at,
                            c.board_column_id
                            b.blocked_at,
                            b.block_reason,
                            bc.name
                    FROM cards c
                    LEFT JOIN blocks b
                        ON c.id = b.card_id
                        AND b.unblocked_at IS NULL
                    INNER JOIN boards_columns bc
                        on bc.id = c.board_column_id
                    WHERE id = ?;
                """;
        try (var statment = connection.prepareStatement(sql)) {
            statment.setLong(1, id);
            statment.executeQuery();
            var resultset = statment.getResultSet();
            if (resultset.next()) {
                var cardDto = new CardInfoDTO(
                        resultset.getLong("c.id"),
                        resultset.getString("c.title"),
                        resultset.getString("c.description"),
                        resultset.getTimestamp("c.created_at").toInstant().atOffset(ZoneOffset.of("UTC")),
                        !resultset.getString("block_reason").isEmpty(),
                        resultset.getTimestamp("b.blocked_at").toInstant().atOffset(ZoneOffset.of("UTC")),
                        resultset.getString("b.block_reason"),
                        resultset.getLong("c.board_column_id"),
                        resultset.getString("bc.name")
                );
                return Optional.of(cardDto);
            }
        }
        return Optional.empty();
    }

    public void findByBoardId(final Long id) {

    }


}
