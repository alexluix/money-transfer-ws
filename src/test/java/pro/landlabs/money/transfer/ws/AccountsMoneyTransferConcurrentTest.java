package pro.landlabs.money.transfer.ws;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.MoneyTransfer;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AccountsMoneyTransferConcurrentTest extends AccountsWebserviceAbstractTest {

    public static final String API_TRANSFER = API_ENDPOINT + "/transfer";

    @Test
    public void shouldTransferMoneyCorrectlyUnderLoad() throws InterruptedException {
        // given
        int balance = 1_000_000;
        int accA = createAccountById(balance);
        int accB = createAccountById(balance);
        int accC = createAccountById(balance);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        // when
        int cycles = 100;
        CountDownLatch latch = new CountDownLatch(cycles);
        for (int i = 0; i < cycles; i++) {
            executorService.submit(() -> {
                moneyTransferOneDollar(accA, accB);
                moneyTransferOneDollar(accB, accC);

                latch.countDown();
            });
        }
        latch.await(1, TimeUnit.MINUTES);

        // then
        assertThat(getAccount(accA).getBalance().intValue(), equalTo(balance - cycles));
        assertThat(getAccount(accB).getBalance().intValue(), equalTo(balance));
        assertThat(getAccount(accC).getBalance().intValue(), equalTo(balance + cycles));
    }

    private Account getAccount(int accountAId) {
        Response response = target.path(API_ENDPOINT + "/" + accountAId).request().get();
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));

        return response.readEntity(Account.class);
    }

    private void moneyTransferOneDollar(int withdrawalAccountId, int depositAccountId) {
        BigDecimal amount = new BigDecimal(1);
        MoneyTransfer moneyTransfer = new MoneyTransfer(withdrawalAccountId, depositAccountId, amount);

        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        Response response = target.path(API_TRANSFER).request().post(entity);
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));
    }

}
