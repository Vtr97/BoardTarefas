package marques.vitor.persistence.dao;


import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import marques.vitor.persistence.entity.BoardColumnEntity;
import marques.vitor.persistence.entity.BoardColumnTypeEnum;
import marques.vitor.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnDAO {
    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        return null;
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
}
