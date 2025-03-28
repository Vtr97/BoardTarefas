package marques.vitor;

import marques.vitor.persistence.migration.MigrationStrategy;
import marques.vitor.ui.MainMenu;

import java.sql.SQLException;

import static marques.vitor.persistence.config.ConnectionConfig.getConnection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
        var mainMenu = new MainMenu();
        mainMenu.execute();
    }
}