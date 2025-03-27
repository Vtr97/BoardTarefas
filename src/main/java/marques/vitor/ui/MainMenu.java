package marques.vitor.ui;

import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() {
        System.out.println("Gerenciador de boards, selecione os comandos com a tecla associada: ");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Selecionar board");
            System.out.println("3 - Excluir boar");
            System.out.println(("4 - sair"));
        }
    }
}
