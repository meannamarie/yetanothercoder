package ru.yetanothercoder.tests.springhibernate.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.yetanothercoder.tests.springhibernate.entity.User;

import java.util.List;

public class UserDao {
    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<User> getNameLike(String name) {
        Session session = sessionFactory.openSession();
        Query q = session.createQuery("from User where name like :name1");
        q.setString("name1", name + "%");
        return q.list();
    }

}
