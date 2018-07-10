package ru.timokhin.lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeWork {
    public static void main(String[] args) {
        //1.
        Integer[] lp = {1, 2, 3, 4, 5, 6, 7, 8};
        Task1<Integer> problem = new Task1<>();
        problem.ChangePlaces(lp, 4, 1);
        //2.
        Object[] pl = new Object[10];
        for (Object o : pl) {
            o = new Object();
        }
        ArrayList<Object> newList = changeToArrayList(pl);

        //3.
        Box<Apple> box1= new Box<>(new Apple()); // только с добавлением такого конструктора у меня получилось использовать метод getWEIGHT, т.к. метод не Overrid'ится, если он static
        Box<Orange> box2 = new Box<>(new Orange());
        Box<Orange> box3 = new Box<>(new Orange());
        Box<Apple> box4 = new Box<>(new Apple());

        box1.addNFruits(30,new Apple());
        box2.addNFruits(10, new Orange());
        box3.addNFruits(10, new Orange());
        box4.addNFruits(2,new Apple());

        box3.putAllFruitsIntoAnotherBox(box2);
        box4.putAllFruitsIntoAnotherBox(box2);

        System.out.println(box1.compare(box2));
    }


    static class Task1<T> {
        //1.
        void ChangePlaces(T[] array, T object1, T object2) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(object1))
                    array[i] = object2;
                else if (array[i].equals(object2))
                    array[i] = object1;
            }
        }


    }

    //2.
    private static <E> ArrayList<E> changeToArrayList(E[] array) {
        ArrayList<E> theListToReturn = new ArrayList<>();
        theListToReturn.addAll(Arrays.asList(array));
        return theListToReturn;
    }

}

class Apple extends Fruit{
    public final static float WEIGHT = 1.0f;

    @Override
    public float getWEIGHT(){
        return WEIGHT;
    }



}
class Orange extends Fruit{
    public final static float WEIGHT = 1.5f;

    @Override
    public float getWEIGHT(){
        return WEIGHT;
    }
}


