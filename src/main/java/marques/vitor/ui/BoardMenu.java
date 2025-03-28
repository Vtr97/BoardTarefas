package marques.vitor.ui;

import lombok.AllArgsConstructor;
import marques.vitor.persistence.entity.BoardColumnEntity;
import marques.vitor.persistence.entity.BoardColumnTypeEnum;
import marques.vitor.persistence.entity.BoardEntity;
import marques.vitor.persistence.entity.CardEntity;
import marques.vitor.service.BoardColumnService;
import marques.vitor.service.BoardService;
import marques.vitor.service.CardService;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Scanner;

import static marques.vitor.persistence.config.ConnectionConfig.getConnection;
import static marques.vitor.persistence.entity.BoardColumnTypeEnum.INITIAL;


@AllArgsConstructor
public class BoardMenu {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void runMenu() throws SQLException {
        System.out.printf("Este é o menu da board %s, escolha sua opção abaixo:  \n", entity.getName());
        var option = -1;
        var run = true;
        while (run) {
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Ver card");
            System.out.println("7 - Ver colunas");
            System.out.println("8 - Ver coluna com cards");
            System.out.println("9 - Voltar para o menu de boards");

            System.out.println(("10 - Sair da aplicação"));

            option = scanner.nextInt();
            switch (option) {
                case 1:
                    createCard();
                    break;
                case 2:
                    moveCard();
                    break;
                case 3:
                    blockCard();
                    break;
                case 4:
                    unblockCard();
                    break;
                case 5:
                    cancelCard();
                    break;
                case 6:
                    showCard();
                    break;
                case 7:
                    showColumns();
                    break;
                case 8:
                    showColumn();
                    break;
                case 9:
                    run = false;
                    break;
                default:
                    System.out.println("Opção invalida");
            }
        }
    }

    private void showColumn() throws SQLException {
        System.out.printf("Escolha uma coluna do board %s \n", entity.getName());
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            entity.getBoardColumns().forEach(c ->
                    System.out.printf("%s - %s: %s\n", c.getId(), c.getName(), c.getType())
            );
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnService(connection).findById(selectedColumn);
            if (column.isPresent()) {
                System.out.printf("Coluna: %s tipo: %s \n",
                        column.get().getName(),
                        column.get().getType());
                column.get().getCards().forEach(
                        card -> System.out.printf("Card %s - %s\nCriado em: %s \n Descrição: %s\n",
                                card.getId(), card.getTitle(), card.getCreated_at(), card.getDescription())
                );
            }
        }
    }

    private void showColumns() throws SQLException {
        try (var connection = getConnection()) {
            BoardService service = new BoardService(connection);
            var boardDetails = service.showBoardInfo(entity.getId());
            if (boardDetails.isPresent()) {
                var details = boardDetails.get();
                System.out.printf("Board id: %s  nome: %s \n", details.id(), details.name());
                details.columns().forEach(c ->
                        System.out.printf("Coluna id: %s nome: %s tipo: %s tem %s cards\n ", c.id(), c.name(), c.type().toString(), c.cardsAmount())
                );
            }
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            var cardService = new CardService(connection);
            var selectedCard = cardService.findById(selectedCardId);
            if (selectedCard.isPresent()) {
                System.out.printf("card: %s  %s\n", selectedCard.get().id(), selectedCard.get().title());
                System.out.printf("Descrição: %s \n", selectedCard.get().description());
                System.out.printf("Criado em: %s \n", selectedCard.get().createdAt());
                System.out.println(selectedCard.get().blocked() ? "Está bloqueado. Motivo: " + selectedCard.get().blockedReason() : "");
                System.out.printf("Está na coluna %s %s \n", selectedCard.get().ColumnId(), selectedCard.get().ColumnName());
            } else {
                System.out.printf("Não foi possível achar a card de id %s, informe um id válido \n", selectedCardId);
            }
        }
    }

    private void cancelCard() {
    }

    private void unblockCard() {
    }

    private void blockCard() {
    }

    private void moveCard() {
    }

    private void createCard() throws SQLException {
        CardEntity cardEntity = new CardEntity();
        cardEntity.setCreated_at(OffsetDateTime.now());
        System.out.println("Digite o titulo do card: ");
        var cardTitle = scanner.next();
        cardEntity.setTitle(cardTitle);
        System.out.println("Digite a descrição do card: ");
        var cardDesc = scanner.next();
        cardEntity.setDescription(cardDesc);
        var creationColumn = entity.initialColumn();
        cardEntity.setBoardColumn(creationColumn);
        try (var connection = getConnection()) {
            new CardService(connection).insert(cardEntity);
        }
    }
}
