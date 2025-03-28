package marques.vitor.persistence.dao;


import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import marques.vitor.dto.BoardColumnDTO;
import marques.vitor.persistence.entity.BoardColumnEntity;
import marques.vitor.persistence.entity.BoardColumnTypeEnum;
import marques.vitor.persistence.entity.BoardEntity;
import marques.vitor.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@AllArgsConstructor
public class BoardColumnDAO {
    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO boards_columns (name,`order`, type, board_id) VALUES (?, ?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getOrder());
            statement.setString(3, entity.getType().toString());
            statement.setLong(4, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }


    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        var sql = "SELECT id,name,`order`,type FROM boards_columns WHERE board_id = ? ORDER BY `order`;";
        List<BoardColumnEntity> list = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setType(BoardColumnTypeEnum.valueOf(resultSet.getString("type")));
                list.add(entity);
            }
            return list;
        }
    }

    public List<BoardColumnDTO> getBoardColumnsDetails(Long id) throws SQLException {
        var sql =
                """
                                SELECT
                                     bc.id,
                                     bc.name,
                                     bc.type,
                                     (SELECT COUNT(c.id)
                                      FROM cards c
                                      WHERE c.board_column_id = bc.id) AS total_cards
                                FROM boards_columns bc
                                WHERE board_id = ?
                                ORDER BY `order`;
                        """;
        List<BoardColumnDTO> dtoList = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        BoardColumnTypeEnum.valueOf(resultSet.getString("type")),
                        resultSet.getInt("total_cards")
                );
                dtoList.add(dto);
            }
            return dtoList;
        }
    }

    public Optional<BoardColumnEntity> findById(Long id) throws SQLException {
        var sql = """
                    SELECT  bc.name,
                            bc.type ,
                            cards.title,
                            cards.id,
                            cards.created_at,cards.description
                    FROM boards_columns bc
                    LEFT JOIN cards
                        ON cards.board_column_id = bc.id
                    WHERE bc.id = ? 
                    ORDER BY `order`;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setType(BoardColumnTypeEnum.valueOf(resultSet.getString("bc.type")));
                do {
                    if (isNull(resultSet.getString("cards.title"))) {
                        break;
                    }
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("cards.id"));
                    card.setTitle(resultSet.getString("cards.title"));
                    card.setCreated_at(resultSet.getTimestamp("cards.created_at").toInstant().atOffset(ZoneOffset.UTC));
                    card.setDescription(resultSet.getString("cards.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }
}



