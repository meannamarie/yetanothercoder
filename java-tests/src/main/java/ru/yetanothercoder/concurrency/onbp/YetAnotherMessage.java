package ru.yetanothercoder.concurrency.onbp;

/**
 * @author Mikhail Baturov 26.03.12
 * @see <a href="http://www.yetanothercoder.ru">Author Blog</a>
 */
class YetAnotherMessage {
    private long counter;
    private String someValue;

    YetAnotherMessage(long counter) {
        this(counter, "");
    }

    YetAnotherMessage(long counter, String someValue) {
        this.counter = counter;
        this.someValue = someValue;
    }

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
