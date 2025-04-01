package marques.vitor.persistence.dao;


import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final Long cardId, final String blockReason) throws SQLException {
        var sql = "INSERT INTO blocks(blocked_at,block_reason,card_id) values(?, ?, ?)";
        try (var statment = connection.prepareStatement(sql)) {
            statment.setTimestamp(1, Timestamp
                    .valueOf(OffsetDateTime.now().atZoneSameInstant(UTC).toLocalDateTime()));
            statment.setString(2, blockReason);
            statment.setLong(3, cardId);
            statment.executeUpdate();
        }
    }

    public void unblock(final long cardId, final String unblockReason) throws SQLException {
        var sql = "UPDATE blocks SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL";
        try (var statment = connection.prepareStatement(sql)) {
            statment.setTimestamp(1, Timestamp
                    .valueOf(OffsetDateTime.now().atZoneSameInstant(UTC).toLocalDateTime()));
            statment.setString(2, unblockReason);
            statment.setLong(3, cardId);
            statment.executeUpdate();
        }
    }
}
