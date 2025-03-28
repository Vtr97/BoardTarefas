package marques.vitor.service;


import lombok.AllArgsConstructor;
import marques.vitor.dto.BoardInfoDto;
import marques.vitor.persistence.dao.BoardColumnDAO;
import marques.vitor.persistence.dao.BoardDAO;
import marques.vitor.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardService {
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumDao = new BoardColumnDAO(connection);
        try {
            BoardEntity insertedEntity = dao.insert(entity);
            var entityColums = insertedEntity.getBoardColumns().stream().peek(boardColumn -> boardColumn.setBoard(insertedEntity)).toList();
            for (var colum : entityColums) {
                boardColumDao.insert(colum);
            }
            connection.commit();
            return insertedEntity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if (!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumDao = new BoardColumnDAO(connection);
        try {
            Optional<BoardEntity> entity = dao.findById(id);
            if (entity.isPresent()) {
                var boardColumnEntities = boardColumDao.findByBoardId(entity.get().getId());
                boardColumnEntities.forEach(colum -> colum.setBoard(entity.get()));
                entity.get().setBoardColumns(boardColumnEntities);
                return entity;
            } else return Optional.empty();
        } catch (SQLException e) {
            throw e;
        }
    }

    public Optional<BoardInfoDto> showBoardInfo(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumDao = new BoardColumnDAO(connection);
        try {
            Optional<BoardEntity> entity = dao.findById(id);
            if (entity.isPresent()) {
                var boardColumnEntities = boardColumDao.findByBoardId(entity.get().getId());
                entity.get().setBoardColumns(boardColumnEntities);
                var boardInfo = new BoardInfoDto(
                        entity.get().getId(),
                        entity.get().getName(),
                        boardColumDao.getBoardColumnsDetails(entity.get().getId()));
                return Optional.of(boardInfo);
            } else return Optional.empty();
        } catch (SQLException e) {
            throw e;
        }
    }
}
