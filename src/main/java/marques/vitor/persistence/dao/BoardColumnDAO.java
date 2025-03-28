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
                                COUNT(
                                    SELECT c.id 
                                    FROM cards c
                                    WHERE c.board_column_id = bc.id) total_cards
                            FROM board_columns bc
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
                            bc.type 
                    FROM boards_columns bc
                    INNER JOIN cards
                        ON cards.board_column_id = boards_columns.id
                    WHERE id = ? 
                    ORDER BY `order`;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("boards_columns.name"));
                entity.setType(BoardColumnTypeEnum.valueOf(resultSet.getString("boards_columns.type")));
                do {
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("cards.id"));
                    card.setTitle(resultSet.getString("cards.title"));
                    card.setCreate_at(resultSet.getTimestamp("cards.created_at").toInstant().atOffset(ZoneOffset.of("UTC")));
                    card.setDescription(resultSet.getString("cards.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
            }
            return Optional.empty();
        }
    }
}



