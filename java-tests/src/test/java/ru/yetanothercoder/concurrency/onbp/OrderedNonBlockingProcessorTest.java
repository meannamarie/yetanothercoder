package ru.yetanothercoder.concurrency.onbp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mikhail Baturov 26.03.12
 * @see <a href="http://www.yetanothercoder.ru">Author Blog</a>
 */
public class OrderedNonBlockingProcessorTest {

    private final OrderedNonBlockingProcessor processor = new OrderedNonBlockingProcessor();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNormal() throws Exception {
        YetAnotherMessage m1 = new YetAnotherMessage(1);
        YetAnotherMessage m2 = new YetAnotherMessage(2);
        YetAnotherMessage m3 = new YetAnotherMessage(3);

        Assert.assertTrue(processor.onSomeHighThroughputMessage(m1));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m2));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m3));
    }

    @Test
    public void testMissing() throws Exception {
        YetAnotherMessage m1 = new YetAnotherMessage(1);
        YetAnotherMessage m3 = new YetAnotherMessage(3);
        YetAnotherMessage m4 = new YetAnotherMessage(4);

        Assert.assertTrue(processor.onSomeHighThroughputMessage(m1));
        Assert.assertFalse(processor.onSomeHighThroughputMessage(m3));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m4));
    }

    @Test
    public void testBrokenOrder() throws Exception {
        YetAnotherMessage m1 = new YetAnotherMessage(1);
        YetAnotherMessage m2 = new YetAnotherMessage(2);
        YetAnotherMessage m3 = new YetAnotherMessage(3);
        YetAnotherMessage m4 = new YetAnotherMessage(4);

        Assert.assertTrue(processor.onSomeHighThroughputMessage(m1));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m2));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m3));
        Assert.assertFalse(processor.onSomeHighThroughputMessage(m1));
        Assert.assertTrue(processor.onSomeHighThroughputMessage(m4));
    }

    @Test
    public void testBrokenOrderWithABA() {
        /* TODO: test with special instrumentation like
           http://stackoverflow.com/questions/12159/how-should-i-unit-test-threaded-code
         */

    }
}
