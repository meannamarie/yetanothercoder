package ru.yetanothercoder.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Mikhail Baturov | www.yetanothercoder.ru
 */
public class GCTest {

    static class A {
        private String myName;
        public A(String myName) {
            this.myName = myName;
        }
    }

    public static void main(String[] args) {
        A a1 = new A("a1");
        A a2 = new A("a2");
        Collection list = new ArrayList();
        list.add(a1); // list -> a1
        A[] mas = new A[2];
        mas[0] = a2;    // mas -> a2
        a2 = a1;
        clear(mas);
        a1 = null;
        a2 = null;
        System.gc();
    }

    private static void clear(A[] mas) {
        mas = null;
    }
}


