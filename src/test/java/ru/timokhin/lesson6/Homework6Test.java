package ru.timokhin.lesson6;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@Slf4j
public class Homework6Test {
    public static final String DB_PATH = "D:\\GeekBrains\\JavaCoreProfessionalLevel\\HomeWork\\StudentsDB";
    public static final String DEFAULT_SELECT_REQUEST = "SELECT * FROM students";
    public static final String DEFAULT_INSERT_INTO_REQUEST = "INSERT INTO students ( id, surname, score) VALUES (?,?,?)";
    public static final String DEFAULT_DELETE_REQUEST = "DELETE FROM students";
    public static final String SOME_DELETE_REQUEST = "DELETE FROM students WHERE (id>50)";
    public static final String DELETE_MORE_THAN_60_REQUEST = "DELETE FROM students WHERE (id>60)";
    public static final String DEFAULT_UPDATE_COST_BY_TITLE_REQUEST = "UPDATE students SET score = ? where (id = ?)";
    public static final String DEFAULT_SELECT_COST_RANGE_REQUEST = "SELECT * FROM  students WHERE(id >= ? ) AND NOT (id > ?)";
    public static final String TABLE_NAME = "students";
    public static final String ID_COLMUN = "id";
    public static final String SURNAME_COLMUN = "surname";
    public static final String SCORE_COLMUN = "score";
    private static Connection conn = null;
    private static PreparedStatement add;
    private static PreparedStatement deleteAll;
    private static PreparedStatement change;
    private static PreparedStatement range;
    private static PreparedStatement deleteSome;
    private static PreparedStatement deleteSome60;
    private static int biggestID =0;

    @Before
    public void setUp() throws Exception {
        deleteSome.executeUpdate();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Тест пройден");
    }

    @BeforeClass
    public static void before() throws Exception {
        conn = DriverManager.getConnection(JDBC.PREFIX + DB_PATH); // устанавливаем сооединение с таблицей
        conn.setAutoCommit(true);
        add = conn.prepareStatement(DEFAULT_INSERT_INTO_REQUEST); // заполняем таблицу по дефолту
        for (int i = 0; i < 50; i++) {
            add.setInt(1, i);
            add.setString(2, ("I:" + i));
            add.setInt(3, (i + (i / 2)));
            add.executeUpdate();
            biggestID = i;
        }
        deleteSome = conn.prepareStatement(SOME_DELETE_REQUEST);
        deleteSome60 = conn.prepareStatement(DELETE_MORE_THAN_60_REQUEST);


    }

    @AfterClass
    public static void after() throws Exception {
        log.info("Все тесты были пройдены");
        deleteAll = conn.prepareStatement(DEFAULT_DELETE_REQUEST);
        deleteAll.executeUpdate();//удаляем все из таблицы
    }

    @Test
    @Ignore
    public void part1() {
        List<Integer[]> inputs = new ArrayList<>();
        inputs.add(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        inputs.add(new Integer[]{1, 2, 3, 5, 6, 7, 4, 9});
        inputs.add(new Integer[]{1, 2, 3, 5, 6, 7, 4});
        inputs.add(new Integer[]{1, 2, 3, 5, 6, 7});
        List<Integer[]> expectations = new ArrayList<>();
        expectations.add(new Integer[]{5, 6, 7, 8, 9});
        expectations.add(new Integer[]{9});
        expectations.add(new Integer[]{});
        expectations.add(new Integer[]{}); // должен выдать RunTime Exception
        for (int i = 0; i < inputs.size(); i++) {
            int[] ints = Homework.part1(inputs.get(i));
            log.info("Результат метода part1() : {}", ints);
            // т.к List хранит Integer[], мы наш конкретный массив превратим в int[], что бы можно было вызвать toString
            int[] exInts = new int[expectations.get(i).length];
            int j = 0;
            for (int expectation : expectations.get(i)) {
                exInts[j] = expectation;
                j++;
            }
            log.info("Мы ожидали : {}", exInts);
            assertEquals(expectations.get(i).length, ints.length);// сравниваем по длинне, выводим значения в log, чтобы в случае чего, мы могли узнать значения массивов и поправить
            log.info("Ожидания и результат совпали");
        }

    }

    @Test
    @Ignore
    public void part2() {
        List<Integer[]> inputs = new ArrayList<>();
        inputs.add(new Integer[]{4, 4, 4, 4, 4, 4, 4, 4});
        inputs.add(new Integer[]{4, 1, 4, 4, 4, 1, 1, 4});
        inputs.add(new Integer[]{4, 3, 1});
        inputs.add(new Integer[]{2, 3, 5, 6, 7, 8, 9, 0});
        boolean[] result = {true, true, false, false};
        for (int i = 0; i < inputs.size(); i++) {
            boolean res = Homework.doesComtainOnly1And4(inputs.get(i));
            if (log.isDebugEnabled())
                log.debug("{} : {} = {}", result[i], res, (res == result[i] ? "Совпало" : "Не совпало"));// не до конца понял, как метод isDebugEnabled работает. Также, когда я в режиме debug-инга, то данный метод все равно не срабатываетъ
            assertEquals(result[i], res);
        }
    }

    @Test
    @Ignore
    public void add() throws SQLException {
        log.info("наибольштй id : {}", biggestID);
        int i = biggestID+1;
        for (; i < biggestID + 51; i++) {
            add.setInt(1, i);
            add.setString(2, ("I:" + i));
            add.setInt(3, (i + (i / 2)));
            add.executeUpdate();
        }
        biggestID =i;
        showInfo();
        log.info("наибольштй id : {}", biggestID);
    }
    @Test
    @Ignore
    public void remove()throws SQLException{
        add();
        deleteSome60.executeUpdate();
        showInfo();
    }
    @Test
    public void changeName() throws SQLException {
        Statement changeNameToBen = conn.prepareStatement("UPDATE students SET surname = ? where (id > ?) ");
        ((PreparedStatement) changeNameToBen).setString(1,"Ben");
        ((PreparedStatement) changeNameToBen).setInt(2,30);
        ((PreparedStatement) changeNameToBen).executeUpdate();
        showInfo();
    }

    private void showInfo() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(DEFAULT_SELECT_REQUEST);
        while (resultSet.next()) {
            // Getting id
            int idColumn = resultSet.findColumn(ID_COLMUN);
            String idStr = resultSet.getString(idColumn);
            // Getting surname
            int intName = resultSet.findColumn(SURNAME_COLMUN);
            String name = resultSet.getString(intName);
            // Getting score
            int intScore = resultSet.findColumn(SCORE_COLMUN);
            String score = resultSet.getString(intScore);
            System.out.println("ID : " + idStr + " Surname : "+name+" Score : "+ score);
        }
    }

}
