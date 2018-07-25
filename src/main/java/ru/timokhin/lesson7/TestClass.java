package ru.timokhin.lesson7;

public class TestClass implements Startable{

    public static void main(String[] args) {
        TestClass ts = new TestClass();
        ts.start();
    }

    @Override
    public void start() {
        String name = String.valueOf(this.getClass());
        String className = name.substring(6);
            TestRunner.startTests(className);

    }
    @Annotation.BeforeSuite
    public static void test1(){
        System.out.println("1");
    }
    @Annotation.AfterSuite()
    public static void test2(){
        System.out.println("last");
    }
    @Annotation.Test(1)
    public static void test3(){
        System.out.println(2);
    }
    @Annotation.Test()
    public static void test4(){
        System.out.println(3);
    }
    @Annotation.Test(8)
    public static void test5(){
        System.out.println(4);
    }



}
