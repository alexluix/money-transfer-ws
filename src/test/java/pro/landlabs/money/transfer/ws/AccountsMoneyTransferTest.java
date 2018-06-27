package pro.landlabs.money.transfer.ws;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.MoneyTransfer;
import pro.landlabs.money.transfer.ws.value.MoneyTransferResult;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AccountsMoneyTransferTest extends AccountsWebserviceAbstractTest {

    public static final String API_TRANSFER = API_ENDPOINT + "/transfer";

    @Test
    public void shouldTransferMoneyFromOneAccountToTheOther() {
        // given
        int balanceA = 100;
        int balanceB = 150;
        int transferAmount = 75;
        int accountAId = createAccountById(balanceA);
        int accountBId = createAccountById(balanceB);

        // then
        MoneyTransferResult moneyTransferResult = moneyTransfer(accountAId, accountBId, transferAmount);

        assertThat(moneyTransferResult, notNullValue());
        assertThat(moneyTransferResult.getAmount().intValue(), equalTo(transferAmount));

        Account withdrawalAccount = moneyTransferResult.getWithdrawalAccount();
        assertThat(withdrawalAccount.getBalance().intValue(), equalTo(balanceA - transferAmount));

        Account depositAccount = moneyTransferResult.getDepositAccount();
        assertThat(depositAccount.getBalance().intValue(), equalTo(balanceB + transferAmount));
    }

    private MoneyTransferResult moneyTransfer(int withdrawalAccountId, int depositAccountId, int transferAmount) {
        BigDecimal amount = new BigDecimal(transferAmount);
        MoneyTransfer moneyTransfer = new MoneyTransfer(withdrawalAccountId, depositAccountId, amount);

        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        Response response = target.path(API_TRANSFER).request().post(entity);
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));

        return response.readEntity(MoneyTransferResult.class);
    }

}
