package marques.vitor.service;

import lombok.AllArgsConstructor;
import marques.vitor.dto.BoardColumInfoDTO;
import marques.vitor.dto.CardInfoDTO;
import marques.vitor.exception.CardBlockedException;
import marques.vitor.exception.CardFinishedException;
import marques.vitor.exception.EntityNotFoundException;
import marques.vitor.persistence.dao.BlockDAO;
import marques.vitor.persistence.dao.CardDao;
import marques.vitor.persistence.entity.BoardColumnTypeEnum;
import marques.vitor.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static marques.vitor.persistence.entity.BoardColumnTypeEnum.CANCEL;
import static marques.vitor.persistence.entity.BoardColumnTypeEnum.FINAL;

@AllArgsConstructor
public class CardService {
    private final Connection connection;


    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDao(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public Optional<CardInfoDTO> findById(final Long id) throws SQLException {
        var dao = new CardDao(connection);
        return dao.findById(id);
    }

    public void moveToNextColumns(final Long cardId, final List<BoardColumInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDao(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () ->
                            new EntityNotFoundException("O card de if %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.id().equals(dto.ColumnId())).
                    findFirst().
                    orElseThrow(() ->
                            new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.type().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado!");
            }
            var nextColumn = boardColumnsInfo.stream().filter(bc ->
                    bc.order() == currentColumn.order() + 1).findFirst().orElseThrow(() ->
                    new IllegalStateException("O card está cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDao(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () ->
                            new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.id().equals(dto.ColumnId())).
                    findFirst().
                    orElseThrow(() ->
                            new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.type().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado!");
            }
            var nextColumn = boardColumnsInfo.stream().filter(bc ->
                    bc.order() == currentColumn.order() + 1).findFirst().orElseThrow(() ->
                    new IllegalStateException("O card está cancelado"));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long cardId, final String blockReason, final List<BoardColumInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDao(connection);
            var blockDao = new BlockDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () ->
                            new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                throw new CardBlockedException("O card %s já está bloqueado".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.id().equals(dto.ColumnId())).
                    findFirst().
                    orElseThrow(() ->
                            new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.type().equals(FINAL) || currentColumn.type().equals(CANCEL)) {
                throw new IllegalStateException("O card não pode ser bloqueado pois já foi %s!"
                        .formatted(currentColumn.type()));
            }
            blockDao.block(cardId, blockReason);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long cardId, final String unblockReason) throws SQLException {
        try {
            var dao = new CardDao(connection);
            var blockDao = new BlockDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () ->
                            new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (!dto.blocked()) {
                throw new CardBlockedException("O card %s não está bloqueado".formatted(cardId));
            }
            blockDao.unblock(cardId, unblockReason);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

}

