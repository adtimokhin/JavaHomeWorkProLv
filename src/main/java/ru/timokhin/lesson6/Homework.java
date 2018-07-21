package ru.timokhin.lesson6;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class Homework {
    public static void main(String[] args) {
        log.info("{}", 12356);
        Logger logger = LoggerFactory.getLogger(Homework.class);
        logger.info("My name is {} ","Sasha");
    }
    public static int[] part1(int[] list){
        return list;
    }
}

