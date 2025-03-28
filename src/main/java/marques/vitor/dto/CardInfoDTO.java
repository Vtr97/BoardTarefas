package marques.vitor.dto;

import java.time.OffsetDateTime;

public record CardInfoDTO(Long id,
                          String title,
                          String description,
                          OffsetDateTime createdAt,
                          boolean blocked,
                          OffsetDateTime blockedAt,
                          String blockedReason,
                          Long ColumnId,
                          String ColumnName) {
}
