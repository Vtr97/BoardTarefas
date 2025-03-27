package marques.vitor.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {

    private Long id;
    private String block_reason;
    private OffsetDateTime blocked_at;
    private String unblock_reason;
    private OffsetDateTime unblocked_at;

}
