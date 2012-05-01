package ru.yetanothercoder.concurrency.onbp;

import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author Mikhail Baturov 3/24/12
 * @see <a href="http://www.yetanothercoder.ru">Author Blog</a>
 */
public class OrderedNonBlockingProcessor implements YetAnotherMessageHandler {
    private static Logger log = Logger.getLogger(OrderedNonBlockingProcessor.class);
    
    private final AtomicStampedReference<Long> messageCounter = new AtomicStampedReference<Long>((long)0, 0);


    @Override
    public boolean onSomeHighThroughputMessage(final YetAnotherMessage message) {
        final StampedReferencePairPub current = getAndSetMessageCounter(message.getCounter());
        final long expectedCounter = current.ref + 1;

        if (expectedCounter == message.getCounter()) {
            processBusinessStuff(message);
            return true;
        } else if (expectedCounter > message.getCounter()) {
            /* ignore stale message: attempt to restore the sequence to prevent an error on next good message
             */
            final int expectedStamp = current.stamp + 1;
            boolean restored = messageCounter.compareAndSet(message.getCounter(), current.ref, expectedStamp, expectedStamp + 1);
                    
            log.error(String.format("messaging system ordering bug: got stale message %s while expected %s! Sequence restored: %s",
                    message.getCounter(), expectedCounter, restored));

            // some other notifying stuff...

        } else if (expectedCounter < message.getCounter()) {
            log.error(String.format("got forward message %s while expected %s, probably missed: %s",
                    message.getCounter(), expectedCounter, message.getCounter() - expectedCounter));

            // some other notifying stuff...

        }
        return false;
    }

    
    
    private void processBusinessStuff(YetAnotherMessage message) {
        log.info(String.format("process message %s", message.getCounter()));
        // some business logic...
    }

    private StampedReferencePairPub getAndSetMessageCounter(final long newValue) {
        while (true) {
            StampedReferencePairPub current = new StampedReferencePairPub(messageCounter.getReference(), messageCounter.getStamp());
            if (messageCounter.compareAndSet(current.ref, newValue, current.stamp, current.stamp + 1))
                return current;
        }
    }
    
    public static class StampedReferencePairPub {
        public final long ref;
        public final int stamp;
        
        StampedReferencePairPub(long r, int i) {
            ref = r; stamp = i;
        }
    }
}

