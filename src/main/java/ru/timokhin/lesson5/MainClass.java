package ru.timokhin.lesson5;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public static final int MAX_CARS_IN_TUNNEL = CARS_COUNT / 2;
    public static boolean[] isNotEmpty = new boolean[MAX_CARS_IN_TUNNEL];
    private static final int SMALL_FIELD_SIZE = 20;
    private static final int MEDIUM_FIELD_SIZE = 40;
    private static final int LARGE_FIELD_SIZE = 60;
    private static final int SMALL_TUNNEL_SIZE = 40;
    private static final int MEDIUM_TUNNEL_SIZE = 60;
    private static final int LARGE_TUNNEL_SIZE = 80;
    static Race race = new Race(new Road(LARGE_FIELD_SIZE), new Tunnel(LARGE_TUNNEL_SIZE), new Road(MEDIUM_FIELD_SIZE), new Finish());// создаем трассу
    public static Semaphore placesInTunnel = new Semaphore(MAX_CARS_IN_TUNNEL, true);
    public static CountDownLatch start = new CountDownLatch(CARS_COUNT);
    public static CountDownLatch finish = new CountDownLatch(race.getStagesNo() * CARS_COUNT);
    private static volatile int counter = 0;
    private static Map<String, Long> results = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");

        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }


        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        while (start.getCount() != 0)
            Thread.sleep(100);


        while (finish.getCount() != 0)
            Thread.sleep(100);
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

    public static void begin() { // метод создан для того, чтобы ДО превого этапа вывелась данная надпись
        if (counter == 0) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            counter++;
        }
    }

    public static synchronized void addResults(String name, long time) {
        results.put(name,time);
        if(results.size() == CARS_COUNT)
            printResults();
    }

    private static void printResults() {
       String[] names = new String[CARS_COUNT];
       long[] times = new long[CARS_COUNT];
       int i=0;
       Iterator<Map.Entry<String,Long>> iterator = results.entrySet().iterator();
       while (iterator.hasNext()){
           Map.Entry<String,Long> pair = iterator.next();
           String name = pair.getKey();
           long val = pair.getValue();
           names[i] = name;
           times[i] = val;
           ++i;
       }
        for (int j = 0; j < times.length; j++) {// данный цикл поменяет все значения по порядку убывания
            for (int k = 0; k < names.length; k++) {
                if(times[j] < times[k]){
                    long helpingVal = times[k];
                    String helpingName = names[k];
                    times[k] = times[j];
                    names[k] = names[j];
                    times[j] = helpingVal;
                    names[j] = helpingName;
                }
            }
        }

        final int SECONDS = 1000;
        final int MINUTES = 60*SECONDS;

        for (int j = 0; j < times.length; j++) {
            long timeInMin = (times[j])/MINUTES;
            long leftOverSeconds =((times[j]) -  timeInMin*MINUTES)/SECONDS;
            long leftOverNanoSeconds =(times[j]) -(timeInMin*MINUTES+leftOverSeconds*SECONDS);
            System.out.printf("#%d: %s. Он прошел трассу за :%d минут, %d секуд, %d миллмсекунд\n",j+1, names[j],timeInMin, leftOverSeconds, leftOverNanoSeconds );
        }

    }
}

class Car implements Runnable {
    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            MainClass.start.countDown();// наш участник приготовился, и мы теперь ждем
            MainClass.start.await();    //      пока число неподготовленных участников не станет равным 0

        } catch (Exception e) {
            e.printStackTrace();
        }
        MainClass.begin();
        Date currentMoment = new Date();
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
            MainClass.finish.countDown();
        }
        Date timePassed = new Date();
        try {
            MainClass.finish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = timePassed.getTime() - currentMoment.getTime();
        MainClass.addResults(this.name, time);

    }

    private void addCarResults(String name, Date before, Date after) {
        long time = after.getTime()-before.getTime();

    }
}

abstract class Stage {
    protected int length;
    protected String description;

    public String getDescription() {
        return description;
    }

    public abstract void go(Car c);
}

class Road extends Stage {
    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000);
            System.out.println(c.getName() + " закончил этап: " + description);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Tunnel extends Stage {
    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }
    public Tunnel(int length){
        this.length = length;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        int placeToked = 0;
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                MainClass.placesInTunnel.acquire();// ожидаем, пока место в туннеле освободится
                for (int i = 0; i < MainClass.MAX_CARS_IN_TUNNEL; i++) {
                    if (placeToked == 0) {
                        if (!MainClass.isNotEmpty[i]) {
                            placeToked = i;
                            System.out.println(c.getName()+ " занял  свободное место #"+(placeToked + 1)+" в туннеле");
                            MainClass.isNotEmpty[placeToked] = true;
                        }
                    }
                }
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
                MainClass.isNotEmpty[placeToked] = false;
                MainClass.placesInTunnel.release();
                placeToked = 0;
                System.out.printf("%s освободил  место #%d  в туннеле\n", c.getName(), (placeToked + 1));
                System.out.println(c.getName() + " закончил этап: " + description);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.printf("По какой-то неизвестной нам причине,%s не справился с управлением и навечно изчез в туннеле...\n", c.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Finish extends Stage {

    @Override
    public void go(Car c) {
        System.out.printf("%s финишировал\n", c.getName());

    }
}

class Race {
    private ArrayList<Stage> stages;

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public int getStagesNo() {
        return stages.size();
    }

    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
    }
}
