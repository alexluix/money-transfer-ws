package pro.landlabs.money.transfer.service;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AccountServiceMoneyTransferConcurrentTest extends AccountServiceAbstractTest {

    @Test
    public void shouldTransferMoneyCorrectlyUnderLoad() throws InterruptedException {
        // given
        int balance = 1_000_000;
        int accA = createAccount(balance).getId();
        int accB = createAccount(balance).getId();
        int accC = createAccount(balance).getId();

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        // when
        int cycles = 1000;
        CountDownLatch latch = new CountDownLatch(cycles);
        for (int i = 0; i < cycles; i++) {
            executorService.submit(() -> {
                moneyTransferOneDollar(accA, accB);
                moneyTransferOneDollar(accB, accA);
                moneyTransferOneDollar(accB, accC);

                latch.countDown();
            });
        }
        latch.await(1, TimeUnit.MINUTES);

        // then
        assertThat(subject.getAccount(accA).getBalance().intValue(), equalTo(balance));
        assertThat(subject.getAccount(accB).getBalance().intValue(), equalTo(balance - cycles));
        assertThat(subject.getAccount(accC).getBalance().intValue(), equalTo(balance + cycles));
    }

    private void moneyTransferOneDollar(int accA, int accB) {
        subject.transfer(accA, accB, BigDecimal.ONE);
    }

}
