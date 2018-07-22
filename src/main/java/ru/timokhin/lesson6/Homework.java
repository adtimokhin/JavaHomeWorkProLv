package ru.timokhin.lesson6;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

@Slf4j
public class Homework {
    public static final String DB_PATH ="D:\\GeekBrains\\JavaCoreProfessionalLevel\\HomeWork\\StudentsDB";
    public static final String DEFAULT_SELECT_REQUEST = "SELECT * FROM students";
    public static final String DEFAULT_INSERT_INTO_REQUEST = "INSERT INTO students ( id, surname, score) VALUES (?,?,?)";
    public static final String DEFAULT_DELETE_REQUEST = "DELETE FROM students";
    public static final String DEFAULT_UPDATE_COST_BY_TITLE_REQUEST = "UPDATE students SET score = ? where (id = ?)";
    public static final String DEFAULT_SELECT_COST_RANGE_REQUEST = "SELECT * FROM  students WHERE(id >= ? ) AND NOT (id > ?)";
    public static final String TABLE_NAME = "students";
    public static final String ID_COLMUN= "id";
    public static final String SURNAME_COLMUN= "surname";
    public static final String SCORE_COLMUN= "score";
    private static Connection conn = null;
    public static void main(String[] args) {
    }
    public static int[] part1(@NonNull Integer[] list){
        for (int i = list.length; i >0 ; i--) {
            if(list[i-1]==4){
                int num = list.length-i;
                int[] newList = new int[num];
                for (int j = 0; j < newList.length; j++) {
                    newList[j] = list[list.length-num+j];
                }
                return newList;
            }
        }
        throw new RuntimeException();
    }
    public static boolean doesComtainOnly1And4(Integer[] list){
        int count1 =0 , count4 =0;
        for (int i = 0; i <list.length ; i++) {
            if(list[i] == 1) count1++;
            else if(list[i] == 4) count4++;
            else return false;
        }
        if(count1!=0 )return true;
        if (count4!=0)return true;
        return false;
    }
}

