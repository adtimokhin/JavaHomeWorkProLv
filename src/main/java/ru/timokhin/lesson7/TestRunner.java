package ru.timokhin.lesson7;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestRunner {
    private static final String BEFORE_SUIT_ANNOTATION = "@BeforeSuit";
    private static final String AFTER_SUIT_ANNOTATION = "@AfterSuit";
    private static final String TEST_ANNOTATION = "@Test";
    public static void startTests(String name){
        Class cl = null;
        try {
             cl = Class.forName(name);
        } catch (ClassNotFoundException e) {
            log.warn("Класс {} не найден", name);
        }
        assert cl != null;
        Method[] methods = cl.getMethods();
        beginBeforeSuiteAnnotation(methods, cl);
        completeAllMethodsWithTestAnnotation(methods, cl);
        beginAfterSuiteAnnotation(methods, cl);

    }

    private static void completeAllMethodsWithTestAnnotation(Method[] methods, Class clazz) {
        List<Method> testMethodList = new ArrayList<>();
        for (Method md : methods) {
            if(md.isAnnotationPresent(Annotation.Test.class))
                testMethodList.add(md);
        }
        log.info("из класса {} было извлечино {} методов с аннотацией {}", clazz.getName(), testMethodList.size(),TEST_ANNOTATION);
        if(testMethodList.size()==0) return;
        else {
            Method[] testMethods = new Method[testMethodList.size()];
            int g =0;
            for (Method method : testMethodList) {
                testMethods[g] = method;
                g++;
            }
            for (int i = 0; i < testMethods.length; i++) {
                int val = testMethods[i].getAnnotation(Annotation.Test.class).value();
                for (int j = 0; j < testMethods.length; j++) {
                    int val1 = testMethods[j].getAnnotation(Annotation.Test.class).value();
                    if (val< testMethods[j].getAnnotation(Annotation.Test.class).minValue()||val>testMethods[j].getAnnotation(Annotation.Test.class).maxValue())throw new RuntimeException("значение,передаваемое в @Test должно быть больше 0 и не больше 10");
                    if(val<val1){
                        Method md = testMethods[i];
                        testMethods[i] = testMethods[j];
                        testMethods[j] = md;
                    }
                }
            }
            log.info("Все методы в количестве {} с аннотацией {} были переставлены в проядке возрастания",testMethods.length,TEST_ANNOTATION);
            for (Method method : testMethods) {
                runMethod(method, clazz);
            }
        }

    }

    private static void runMethod(Method method, Class clazz) {
        try {
            Object o = clazz.newInstance();
            method.setAccessible(true);
            log.info("метод {} из класса {} был вызван", method.getName(),clazz.getName());
            try {
                // как сделать так, чтобы можно было вызывать метод с абсолютно любыми параметрами, подставляя туда нулевые значения. пустой массив из неисвестно чего, итд ????
                if(!method.isVarArgs())
                    method.invoke(o);
                else
                method.invoke(o,method.getDefaultValue());

            } catch (InvocationTargetException e) {
                log.error("invoke метода {} не удался",method.getName());
                System.exit(-1);
            } catch (IllegalArgumentException e) { // оставляем программу работать, чтобы все остальные методы могли быть пройдены.
                log.error("invoke метода {} не удался. Причина : метод должен быть без параметров",method.getName());
            }
            log.info("метод {} из класса {} был успешно завершен", method.getName(),clazz.getName());

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("метод newInstance() выдал ошибку");
            System.exit(-1);
        }
    }

    private static void beginAfterSuiteAnnotation(Method[] methods, Class clazz) {
        boolean afterSuiteAnnotationFound = false;
        Method method = null;
        int count=0;
        for (Method md : methods) {
            if(md.isAnnotationPresent(Annotation.AfterSuite.class)){
                log.info("{} содержит аннотацию {}",md.getName(), AFTER_SUIT_ANNOTATION);
                afterSuiteAnnotationFound = true;
                method = md;
                count++;
            }
        }
        if(count>1){
            throw new RuntimeException("В классе было более одного метода с аннотацией "+AFTER_SUIT_ANNOTATION);
        }
        if(!afterSuiteAnnotationFound){
            log.warn("Ни один из методов класса {} не содержал аннотации {}",clazz.getName(),AFTER_SUIT_ANNOTATION);
        }else {
            /*try {
                Object o = clazz.newInstance();
                method.setAccessible(true);
                try {
                    method.invoke(o,null);
                } catch (InvocationTargetException e) {
                    log.error("invoke метода {} не удался",method.getName());
                    System.exit(-1);
                }
                log.info("метод {} из класса {} был вызвын", method.getName(),clazz.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("метод newInstance() выдал ошибку");
                System.exit(-1);
            }*/
            runMethod(method,clazz);
        }
    }

    private static void beginBeforeSuiteAnnotation(Method[] methods, Class clazz) {
        boolean beforeSuiteAnnotationFound = false;
        Method method = null;
        int count=0;
        for (Method md : methods) {
            if(md.isAnnotationPresent(Annotation.BeforeSuite.class)){
                log.info("{} содержит аннотацию {}",md.getName(), BEFORE_SUIT_ANNOTATION);
                beforeSuiteAnnotationFound =true;
                method = md;
                count++;
            }
        }

        if(count>1){
            throw new RuntimeException("В классе было более одного метода с аннотацией "+BEFORE_SUIT_ANNOTATION);
        }

        if(!beforeSuiteAnnotationFound)
            log.warn("Ни один из методов класса {} не содержал аннотации {}",clazz.getName(),BEFORE_SUIT_ANNOTATION);
        else{
            /*try {
                Object o = clazz.newInstance();
                method.setAccessible(true);
                try {
                    method.invoke(o);
                } catch (InvocationTargetException e) {
                    log.error("invoke метода {} не удался",method.getName());
                    System.exit(-1);
                }
                log.info("метод {} из класса {} был вызвын", method.getName(),clazz.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("метод newInstance() выдал ошибку");
                System.exit(-1);
            }*/
            runMethod(method,clazz);
        }
    }
}
