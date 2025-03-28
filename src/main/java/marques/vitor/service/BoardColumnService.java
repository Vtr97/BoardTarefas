package marques.vitor.service;

import lombok.AllArgsConstructor;
import marques.vitor.dto.BoardInfoDto;
import marques.vitor.persistence.dao.BoardColumnDAO;
import marques.vitor.persistence.dao.BoardDAO;
import marques.vitor.persistence.entity.BoardColumnEntity;
import marques.vitor.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnService {
    private final Connection connection;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        var boardColumDao = new BoardColumnDAO(connection);
        try {
            Optional<BoardColumnEntity> entity = boardColumDao.findById(id);
            if (entity.isPresent()) {
                return entity;
            } else return Optional.empty();
        } catch (SQLException e) {
            throw e;
        }
    }
}
