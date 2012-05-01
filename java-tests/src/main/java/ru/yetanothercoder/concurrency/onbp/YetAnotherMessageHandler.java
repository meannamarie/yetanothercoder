package ru.yetanothercoder.concurrency.onbp;

/**
 * @author Mikhail Baturov 26.03.12
 * @see <a href="http://www.yetanothercoder.ru">Author Blog</a>
 */
interface YetAnotherMessageHandler {
    public boolean onSomeHighThroughputMessage(YetAnotherMessage m);
}
