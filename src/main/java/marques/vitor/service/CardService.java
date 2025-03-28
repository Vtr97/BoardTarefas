package marques.vitor.service;

import lombok.AllArgsConstructor;
import marques.vitor.dto.CardInfoDTO;
import marques.vitor.persistence.dao.CardDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public Optional<CardInfoDTO> findById(final Long id) throws SQLException {
        var dao = new CardDao(connection);
        return dao.findById(id);
    }

}
