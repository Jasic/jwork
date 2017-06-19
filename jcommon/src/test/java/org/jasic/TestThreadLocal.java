package org.jasic;

/**
 * @Author 菜鹰.
 * @Date 2015/1/4
 */
public class TestThreadLocal {

    private static final ThreadLocal<Money> STRING_THREAD_LOCAL = new ThreadLocal<Money>() {
        public Money initialValue() {
            return new Money(10);
        }
    };

    public static void main(String[] args) {
        System.out.println(STRING_THREAD_LOCAL.get());
        new Thread(){
            public void run(){
                System.out.println(STRING_THREAD_LOCAL.get());
            }
        }.start();

    }


    static class Money {
        private Number number;

        Money(Number number) {
            this.number = number;
        }

        public Number getNumber() {
            return number;
        }

        public void setNumber(Number number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "Money{" +
                    "number=" + number +
                    '}';
        }
    }
}
