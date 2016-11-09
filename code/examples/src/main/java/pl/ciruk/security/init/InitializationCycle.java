package pl.ciruk.security.init;

public class InitializationCycle {
    private static final InitializationCycle test = new InitializationCycle();

    private static final int coefficient = (int) (Math.random() * 100 + 1);

    public final int product;

    public InitializationCycle() {
        this.product = coefficient / 10;
    }

    public static void main(String[] args) throws Exception {



        System.out.println("product = " + test.product);

    }

}
