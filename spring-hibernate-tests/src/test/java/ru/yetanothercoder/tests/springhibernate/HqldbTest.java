package ru.yetanothercoder.tests.springhibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.yetanothercoder.tests.springhibernate.dao.UserDao;
import ru.yetanothercoder.tests.springhibernate.entity.User;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:HqldbTest.xml")
public class HqldbTest {

    @Autowired
    SessionFactory sessionFactory;
    
    @Autowired
    UserDao userDao;

    private Session session;

    @Before
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        session.getTransaction().begin();
        session.createSQLQuery("INSERT INTO USER (ID, NAME) VALUES ('1', 'Mike')").executeUpdate();
    }

    @Test
    public void testUser() {
        User mike = (User) session.load(User.class, new Long(1));
        Assert.assertEquals(mike.getName(), "Mike");
    }

    @Test
    public void testUserLike() {
        List<User> mikes = userDao.getNameLike("Mik");
        Assert.assertTrue(mikes.size() == 1);
        Assert.assertEquals(mikes.get(0).getName(), "Mike");
    }
    
    @After
    public void tearDown() {
        session.getTransaction().rollback();
//        session.close();
    }
}
