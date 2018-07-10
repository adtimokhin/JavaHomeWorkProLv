package ru.timokhin.lesson2;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.Scanner;

public class Homework {
    private static final String DB_PATH = "D:\\GeekBrains\\JavaCoreProfessionalLevel\\HomeWork\\HWDB";
    //requests
    private static final String DEFAULT_SELECT_REQUEST = "SELECT * FROM product";
    private static final String DEFAULT_INSERT_INTO_REQUEST = "INSERT INTO product( id, prodid, title, cost) VALUES (?,?,?,?)";
    private static final String DEFAULT_DELETE_REQUEST = "DELETE FROM product";
    private static final String DEFAULT_UPDATE_COST_BY_TITLE_REQUEST = "UPDATE product SET cost = ? where (title = ?)";
    private static final String DEFAULT_SELECT_COST_RANGE_REQUEST = "SELECT * FROM  product WHERE(cost >= ? ) AND NOT (cost > ?)";
    // labels/names
    private static final String COST_COLUMN_LABEL = "cost";
    private static final String ID_COLUMN_LABEL = "id";
    private static final String PRODID_COLUMN_LABEL = "prodid";
    private static final String TITLE_COLUMN_LABEL = "title";
    private static final String TABLE_NAME = "product";

    private static Connection conn = null;
    // preparedStatements
    private static PreparedStatement add;
    private static PreparedStatement deleteAll;
    private static PreparedStatement change;
    private static PreparedStatement range;

    private static Scanner scr = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(JDBC.PREFIX + DB_PATH);
            conn.setAutoCommit(true);

            add = conn.prepareStatement(DEFAULT_INSERT_INTO_REQUEST);
            deleteAll = conn.prepareStatement(DEFAULT_DELETE_REQUEST);
            change = conn.prepareStatement(DEFAULT_UPDATE_COST_BY_TITLE_REQUEST);
            range = conn.prepareStatement(DEFAULT_SELECT_COST_RANGE_REQUEST);

            begin();
            printInfoByColumnLabel(COST_COLUMN_LABEL);

            Thread consoleWorks = new Thread(new Runnable() {
                @Override
                public void run() {

                    boolean shouldContinue = true;
                    while (shouldContinue) {
                        try {
                            shouldContinue = doWorkWithConsole();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }

            });
            consoleWorks.start();
            consoleWorks.join();


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    private static void add10000() throws SQLException {
        for (int i = 0; i < 10000; i++) {
            add(i);
        }
    }

    private static void addN(int n) throws SQLException {
        if (n <= 0) return;
        for (int i = 0; i < n; i++) {
            add(i);
        }
    }

    private static void add(int i) throws SQLException {
        add.setInt(1, (i + 1));
        add.setInt(2, (i + 1));
        add.setString(3, "Товар" + (i + 1));
        add.setInt(4, (i + 1) * 10);
        add.executeUpdate();
    }

    private static void deleteAll(PreparedStatement delete) throws SQLException {
        delete.executeUpdate();
    }

    private static void begin() throws SQLException {
        System.out.println("Таблица "+ TABLE_NAME+" начинает иницилизироваться. Подождите");
        deleteAll(deleteAll);
        // add10000(); - добавляет 10000 новых товаров(как и требуется в дз)
        addN(50); // - добавляет n новых товаров. Создан, чтобы не ждать долгой иницилизации таблицы, а показать "функционал" консольных комнанд побыстрее.
        System.out.println("Таблица готова к работе");
    }

    private static void printInfoByColumnLabel(String columnLabel) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(DEFAULT_SELECT_REQUEST);
        while (resultSet.next()) {
            int cost = resultSet.findColumn(columnLabel);
            String str = resultSet.getString(cost);
            System.out.println(columnLabel + " : " + str);
        }
    }

    // Console Commands
    private static void getPriceByNameCommand(String productName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(DEFAULT_SELECT_REQUEST);
        int count = 0;
        while (resultSet.next()) {
            // Getting cost
            int costColumn = resultSet.findColumn(COST_COLUMN_LABEL);
            String costStr = resultSet.getString(costColumn);
            // Getting name
            int nameColumn = resultSet.findColumn(TITLE_COLUMN_LABEL);
            String nameStr = resultSet.getString(nameColumn);
            // Compartment
            if (nameStr.equals(productName)) {
                System.out.println(nameStr + " : " + costStr);
                count++;
            }
        }
        if (count == 0) System.out.println("товара с наименованием : " + productName + " нет в таблице " + TABLE_NAME);
    }

    private static void changePriceByNameCommand(String productName, int newPrice) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(DEFAULT_SELECT_REQUEST);
        int count = 0;
        while (resultSet.next()) {
            // Getting name
            int nameColumn = resultSet.findColumn(TITLE_COLUMN_LABEL);
            String nameStr = resultSet.getString(nameColumn);
            // Compartment
            if (nameStr.equals(productName)) {
                change.setInt(1, newPrice);
                change.setString(2, productName);
                change.executeUpdate();
                getPriceByNameCommand(productName);
                count++;

            }
        }
        if (count == 0) System.out.println("товара с наименованием : " + productName + " нет в таблице " + TABLE_NAME);


    }

    private static void showAllProductsInPriceRangeCommand(int minVal, int maxVal) throws SQLException {
        int supportingVar;
        if (minVal > maxVal) {
            supportingVar = maxVal;
            maxVal = minVal;
            minVal = supportingVar;
        }
        range.setInt(1, minVal);
        range.setInt(2, maxVal);

        ResultSet resultSet = range.executeQuery();
        while (resultSet.next()) {
            // Getting id
            int idColumn = resultSet.findColumn(ID_COLUMN_LABEL);
            String idStr = resultSet.getString(idColumn);
            // Getting prodid
            int prodidColumn = resultSet.findColumn(PRODID_COLUMN_LABEL);
            String prodidStr = resultSet.getString(prodidColumn);
            // Getting title
            int titleColumn = resultSet.findColumn(TITLE_COLUMN_LABEL);
            String titleStr = resultSet.getString(titleColumn);
            // Getting cost
            int costColumn = resultSet.findColumn(COST_COLUMN_LABEL);
            String costStr = resultSet.getString(costColumn);
            System.out.println("ID : " + idStr + " Prodid : " + prodidStr + " Title : " + titleStr + " Cost : " + costStr);

        }

    }

    // Thread method
    private static boolean doWorkWithConsole() throws SQLException {
        String command = scr.nextLine();
        String[] partsOfCommand = command.split("\\s");
        switch (partsOfCommand[0]) {
            case "/цена":
                if (partsOfCommand.length != 2)
                    System.out.println("Некоректный ввод");
                else
                    getPriceByNameCommand(partsOfCommand[1]);
                return true;
            case "/сменитьцену":
                int newPrice;
                if (partsOfCommand.length == 3) {
                    try {
                        newPrice = Integer.parseInt(partsOfCommand[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("'" + partsOfCommand[2] + "'" + " не удалось переобразовать в число");
                        return true;
                    }

                    changePriceByNameCommand(partsOfCommand[1], newPrice);
                    return true;
                } else
                    System.out.println("Некоректный ввод");


            case "/товарыпоцене":
                int minVal;
                int maxVal;
                if (partsOfCommand.length == 3) {
                    try {
                        minVal = Integer.parseInt(partsOfCommand[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("'" + partsOfCommand[1] + "'" + " не удалось переобразовать в число");
                        return true;
                    }
                    try {
                        maxVal = Integer.parseInt(partsOfCommand[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("'" + partsOfCommand[2] + "'" + " не удалось переобразовать в число");
                        return true;
                    }

                    showAllProductsInPriceRangeCommand(minVal, maxVal);
                    return true;
                } else
                    System.out.println("Некоректный ввод");
            case "/конец":
                return false;
            default:
                System.out.println("Команда неопзнона");
                return true;

        }
    }
}


