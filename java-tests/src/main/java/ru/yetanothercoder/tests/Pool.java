package ru.yetanothercoder.tests;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Mikhail Baturov | www.yetanothercoder.ru
 */
public class Pool {
    final CopyOnWriteArrayList<PoolObject> objects = new CopyOnWriteArrayList<PoolObject>();

    void add(PoolObject o) {
        objects.add(o);
    }

    void remove(PoolObject o) {
        objects.remove(o);
    }

    private void send(String message, PoolObject sender) {
        for (PoolObject object : objects) {
            if (object != sender) {
                object.onMessage(message);
            }
        }
    }

    PoolObject createNew(String name) {
        return new PoolObject(name);
    }

    public class PoolObject {
        final String name;

        public PoolObject(String name) {
            this.name = name;
        }

        void onMessage(String message) {
            System.out.printf("%s receved message: `%s`%n", name, message);
        }

        void sendBroadcast(String message) {
            send(message, this);
        }

    }

    public static void main(String[] args) {
        Pool pool = new Pool();
        PoolObject o1 = pool.createNew("o1");
        PoolObject o2 = pool.createNew("o2");

        pool.add(o1);
        pool.add(o2);
        pool.add(pool.createNew("o3"));

        o1.sendBroadcast("message from o1");

        pool.remove(o1);
        o2.sendBroadcast("message from o2");
    }
}

class Other {
    Other() {
        Pool pool = new Pool();
        Pool.PoolObject o1 = pool.new PoolObject("asd");
    }
}
