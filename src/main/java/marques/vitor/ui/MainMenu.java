package marques.vitor.ui;

import marques.vitor.persistence.entity.BoardColumnEntity;
import marques.vitor.persistence.entity.BoardColumnTypeEnum;
import marques.vitor.persistence.entity.BoardEntity;
import marques.vitor.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static marques.vitor.persistence.config.ConnectionConfig.getConnection;
import static marques.vitor.persistence.entity.BoardColumnTypeEnum.*;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Gerenciador de boards, selecione os comandos com a tecla associada: ");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Selecionar board");
            System.out.println("3 - Excluir boar");
            System.out.println(("4 - sair"));
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    createBoard();
                    break;
                case 2:
                    selectBoard();
                    break;
                case 3:
                    deleteBoard();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção invalida");
            }
        }
    }

    private void createBoard() throws SQLException {
        var boardEntity = new BoardEntity();
        System.out.println("Digite o nome do board: ");
        var boardName = scanner.next();
        boardEntity.setName(boardName);

        System.out.println(("Seu board terá quantas colunas adicionais? O padrão inicial são 3. Caso queira um número adicional digite esse número, caso contrário digite 0: "));
        var columns = scanner.nextInt();

        List<BoardColumnEntity> boardColumnEntities = new ArrayList<>();

        System.out.println("Informe o nome da primeira coluna: ");
        var firstColumName = scanner.next();
        var initialColumn = createBoardColum(firstColumName, INITIAL, 0);
        boardColumnEntities.add(initialColumn);

        for (int i = 0; i < columns; i++) {
            System.out.println("Informe o nome da coluna de tarefas");
            var newColumName = scanner.next();
            var newColum = createBoardColum(newColumName, PENDING, i + 1);
            boardColumnEntities.add(newColum);
        }

        System.out.println("Informe o nome da ultima coluna: ");
        var lastColumName = scanner.next();
        var lastColumn = createBoardColum(lastColumName, INITIAL, columns + 1);
        boardColumnEntities.add(lastColumn);

        System.out.println("Informe o nome da coluna de cancelamento: ");
        var cancelColumnName = scanner.next();
        var cancelColumn = createBoardColum(cancelColumnName, CANCEL, columns + 2);
        boardColumnEntities.add((cancelColumn));

        boardEntity.setBoardColumns(boardColumnEntities);
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(boardEntity);

        }
    }

    private void selectBoard(final int id) throws SQLException {
        System.out.println("Informe o id do board para selecionar: ");
        var boardId = scanner.nextLong();
        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);
            var selectedBoard = boardService.findById(boardId);
            if (selectedBoard.isPresent()) {
                var boardMenu = new BoardMenu(selectedBoard.get());
                boardMenu.runMenu();
            } else {
                System.out.printf("O board com o id %s não foi encontrado, informar um id válido! /n", id);
            }
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do print que sera deletado");
        var boardId = scanner.nextLong();
        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);
            var operationResult = boardService.delete(boardId);
            if (operationResult) {
                System.out.printf("Board %s deletado com sucesso!\n", boardId);
            } else {
                System.out.printf("Board com id: %s não encontrado, informe um id válido\n", boardId);
            }
        }
    }

    private BoardColumnEntity createBoardColum(final String name, final BoardColumnTypeEnum type, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setType(type);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
