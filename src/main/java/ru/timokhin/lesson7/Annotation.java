package ru.timokhin.lesson7;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotation {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test { // в зависимости от value, метод должен выполняться рагьше, при меньшем значении и позже, при большем. Если у2 и более методов одинаковое value, последовательность их выполнения роли не играет
        int value() default 5;
        int minValue()default 0;
        int maxValue()default 10;
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BeforeSuite {  // метод с данной аннотацией должен срабатывать первым

    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AfterSuite{ // метод с данной аннотацией должен срабатывать последним

    }

}
