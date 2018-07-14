package ru.timokhin.lesson4;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Homework {
    private static int lastUsersId = 0;
    static Object o1 = new Object();
    volatile long totalNamOfSym = 0;
    private static String[] phrasesforT1 = {"small phrase 1","small phrase 2","small phrase 3","small phrase 4","small phrase 5","small phrase 6","small phrase 7","small phrase 8","small phrase 9","small phrase 10"};
    private static String[] phrasesforT2 = {"medium phrase 1","medium phrase 2","medium phrase 3","medium phrase 4","medium phrase 5","medium phrase 6","medium phrase 7","medium phrase 8","medium phrase 9","medium phrase 10"};
    private static String[] phrasesforT3 = {"big phrase 1","big phrase 2","big phrase 3","big phrase 4","big phrase 5","big phrase 6","big phrase 7","big phrase 8","big phrase 9","big phrase 10"};
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 3; i++) {
            executorService.submit(new Tasks(i + 1));
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);


    }

     void part1() {
        Homework lock = new Homework();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    lock.addALetter('A',1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    lock.addALetter('B',2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    lock.addALetter('C',3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();



    }

    public synchronized void addALetter(char letter, int id) throws InterruptedException {
        if(lastUsersId==1 && id==2) {
            System.out.println(letter);
            lastUsersId = id;
            notifyAll();
            wait();
        }
        else if(lastUsersId==2&& id==3){
            System.out.println(letter);
            lastUsersId = id;
            notifyAll();
            wait();
        }
        else if(lastUsersId ==3 && id==1){
            System.out.println(letter);
            lastUsersId = id;
            notifyAll();
            wait();
        }
        else if(lastUsersId==0 && id ==1){
            System.out.println(letter);
            lastUsersId = id;
            notifyAll();
            wait();
        }
        else
            wait();

    }


    static void part2() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(new File("Homework4part4.txt"));
        Thread thread1 = new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                String phrase = phrasesforT1[i];
                try {
                    fos.write(Integer.parseInt(phrase));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                String phrase = phrasesforT2[i];
                try {
                    fos.write(Integer.parseInt(phrase));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread3 = new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                String phrase = phrasesforT3[i];
                try {
                    fos.write(Integer.parseInt(phrase));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
    }

    static void part3() {
        try {
            new Homework().readAndWrite("Homework4part3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private  synchronized void readAndWrite(String pathname) throws IOException {
        synchronized (o1) {


            Thread write = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RandomAccessFile raf = new RandomAccessFile(new File(pathname), "rw");
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("part3.txt"));
                        byte[] page = new byte[1800];
                        byte[] rafPage = new byte[1800];
                        int i = 0, x = 0, pages = 1;
                        while (x != -1) {
                            x = bis.read();
                            totalNamOfSym++;
                            page[i] = (byte) x;
                            if (i == 1799) {
                                for (byte c : page) {
                                    raf.write(c);
                                }
                                raf.write(rafPage);
                                System.out.println("отпечатано : " + pages + " страниц");
                                i = 0;
                                pages++;
                            } else
                                i++;

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            );
            Thread read = new Thread(() -> {
                try {
                    RandomAccessFile raf = new RandomAccessFile(new File(pathname), "r");
                    int i = 1, pages = 1;
                    long numOfSymRed = 0;
                    Thread.sleep(50);
                    while (numOfSymRed <= totalNamOfSym) {
                        raf.read();
                        if (raf.read() != 0)

                            i++;
                        numOfSymRed++;
                        if (i == 1800) {
                            System.out.println("Отсканировано " + pages + " страниц");
                            i = 1;
                            pages++;
                            Thread.sleep(50);
                        }
                    }
                    // не понимаю, почему выводится в консоль, что отпечатоно 5 страниц, а отсканировано всего лишь 3. Но при этом, кол. символов прочтенных и записанных одинакого....
                    System.out.println(numOfSymRed);
                    System.out.println(totalNamOfSym);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            write.start();
            read.start();
        }
    }

    static class Tasks implements Runnable {
        private int id;

        public Tasks(int id) {
            this.id = id;

        }

        @Override
        public void run() {
            if (id == 1)
               new Homework().part1();
            else if (id == 2) {
                try {
                    Homework.part2();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if (id == 3)
                Homework.part3();
            else
                System.out.println("Такой задачи нет");
        }
    }
}
