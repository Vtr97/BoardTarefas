package marques.vitor.service;

import lombok.AllArgsConstructor;
import marques.vitor.dto.CardInfoDTO;
import marques.vitor.persistence.dao.CardDao;
import marques.vitor.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

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

}
