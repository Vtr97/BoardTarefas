package marques.vitor.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import marques.vitor.dto.CardInfoDTO;
import marques.vitor.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDao {
    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO cards(title,description,created_at,board_column_id) VALUES(?,?,?,?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setTimestamp(3, Timestamp.from(entity.getCreated_at().toInstant()));
            statement.setLong(4, entity.getBoardColumn().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    public Optional<CardInfoDTO> findById(final Long id) throws SQLException {
        var sql = """
                    SELECT  c.id,
                            c.title,
                            c.description, 
                            c.created_at,
                            c.board_column_id,
                            b.blocked_at,
                            b.block_reason,
                            bc.name
                    FROM cards c
                    LEFT JOIN blocks b
                        ON c.id = b.card_id
                        AND b.unblocked_at IS NULL
                    INNER JOIN boards_columns bc
                        on bc.id = c.board_column_id
                    WHERE c.id = ?;
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
                        resultset.getTimestamp("c.created_at").toInstant().atOffset(ZoneOffset.UTC),
                        nonNull(resultset.getString("block_reason")),
                        resultset.getTimestamp("b.blocked_at") != null ?
                                resultset.getTimestamp("b.blocked_at").toInstant().atOffset(ZoneOffset.UTC) : null,
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

    public void moveToColumn(final Long columId, final Long cardId) throws SQLException {
        var sql = """
                UPDATE cards 
                SET board_column_id = ?
                WHERE id = ? ;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, columId);
            statement.setLong(2, cardId);
            statement.executeUpdate();
        }
    }


}
