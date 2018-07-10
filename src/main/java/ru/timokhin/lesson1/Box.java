package ru.timokhin.lesson1;

import java.util.ArrayList;

public class Box<F extends Fruit> {
    private ArrayList<F> insides = new ArrayList<>();
    F fruit;

    public Box(F fruit) {
        this.fruit = fruit;
    }

    public <E extends Fruit> boolean compare(Box<E> box2) {
        return this.fruit.getWEIGHT() * this.insides.size() == box2.fruit.getWEIGHT() * box2.insides.size();
    }

    public void addFruit(F fruit) {
        insides.add(fruit);
    }

    public <E extends Fruit> void putAllFruitsIntoAnotherBox(Box<E> box2) {
        float e = box2.fruit.getWEIGHT();
        float f = this.fruit.getWEIGHT();
        if (e==f) {
            for (F inside : insides) {
                box2.addFruit((E) inside);
            }
        } else System.out.println("В двух коробках находятся не одинаковые фрукты");
        insides.removeAll(insides);
    }

    public void addNFruits(int n, F fruit) {
        for (int i = 0; i < n; i++) {
            addFruit(fruit);
        }
    }
}
