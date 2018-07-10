package ru.timokhin.lesson3;

import java.io.*;
import java.util.*;

public class HW {
    private static DataOutputStream dos;
    private static DataInputStream dis;
    private static Scanner scr;
    private static ObjectInputStream ois;
    private static byte[] bytes50 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,1,9,20,21,22,23,24,25,-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,100,127,-127,-1,0,30,30,4,50,5,126};
    public static void main(String[] args) throws IOException {
        File file = new File("part1.txt");
        chooseFileToWorkWith(file);
        // part1();
        part3();

    }
    private static void chooseFileToWorkWith(File file) throws FileNotFoundException {
        dos = new DataOutputStream(new FileOutputStream(file));
        dis = new DataInputStream(new FileInputStream(file));
        scr = new Scanner(dis);
    }
    //1-ая часть дз
    private static void part1() throws IOException {
        dos.write(bytes50);
        readAsByteArr(convertToByte());
    }

    private static byte[] convertToByte()  {
        ArrayList<Byte> wholeArray = new ArrayList<>();
        boolean haveToContinue = true;
        while (haveToContinue){
            byte[] partOfArray;
            try{partOfArray = scr.nextLine().getBytes();}catch (NoSuchElementException e){
                partOfArray = new byte[]{-1};
            }
            if(partOfArray[0]==-1)
                haveToContinue = false;
            else {
                for (byte b : partOfArray) {
                    wholeArray.add(b);
                }
            }
        }
        byte[] byteArray = new byte[wholeArray.size()];
        int i=0;
        for (Byte aByte : wholeArray) {
            byteArray[i] = aByte;
            i++;
        }
     return byteArray;
    }
    private static void readAsByteArr(byte[] bytes){
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        int x;
        while((x = bais.read()) != -1){
            System.out.println(x);
        }
    }
    // 2-ая часть дз
    private static void part2() throws FileNotFoundException {
        FileInputStream fis1 = new FileInputStream(new File("file1.txt"));
        FileInputStream fis2 = new FileInputStream(new File("file2.txt"));
        FileInputStream fis3 = new FileInputStream(new File("file3.txt"));
        FileInputStream fis4 = new FileInputStream(new File("file4.txt"));
        FileInputStream fis5 = new FileInputStream(new File("file5.txt"));
        ArrayList<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(fis1);
        inputStreams.add(fis2);
        inputStreams.add(fis3);
        inputStreams.add(fis4);
        inputStreams.add(fis5);
        Enumeration<InputStream> e = Collections.enumeration(inputStreams);
        SequenceInputStream sis = new SequenceInputStream(e); // <- преобразуем enumeration в SequenceInputStream
    }
    private static void part3() throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("part3.txt"));
            char[] page = new char[1800];
            int i=0 , x =0;
            while(x!=-1){
                x = bis.read();
                page[i] = (char)x;
                if(i==1799){
                    for (char c : page) {
                        System.out.print(c);
                    }
                    System.out.println();
                    System.out.println("New page");
                    i = 0;
                }else
                    i++;

            }
            for (char c : page) {
                System.out.print(c);
            }
            System.out.println("That's it!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
