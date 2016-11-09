package pl.ciruk.security.lock;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

public class BankAccount {
    BigDecimal balanceAmount;
    private final Lock lock = new ReentrantLock();
    private final Random number = new Random(123L);

    void transferTo(BankAccount target, BigDecimal amount) throws InterruptedException {
        Executors.newFixedThreadPool(2);
        Preconditions.checkArgument(
                amount.compareTo(balanceAmount) > 0,
                "Transfer cannot be completed");

        for (int i = 0; i < 5; i++) {
            boolean transferred = lockAndRun(() -> {
                return target.lockAndRun(() -> {
                    target.balanceAmount.add(amount);
                    this.balanceAmount.subtract(amount);
                    return true;
                });
            });

            if (transferred) {
                break;
            } else {
                randomPause();
            }
        }
    }

    private void randomPause() throws InterruptedException {
        int TIME = 1000 + number.nextInt(1000); // 1 second + random delay to prevent livelock
        Thread.sleep(TIME);
    }

    /** Locking idiom. */
    private boolean lockAndRun(BooleanSupplier bankAccountConsumer) {
        if (this.lock.tryLock()) {
            try {
                return bankAccountConsumer.getAsBoolean();
            } finally {
                this.lock.unlock();
            }
        }
        return false;
    }
}
