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
    public void shouldNotTransferToNonExistingAccount() {
        // given
        int accountId = createAccountById();
        int nonExistingAccount = 647832549;

        // when
        Response response = moneyTransferResponse(accountId, nonExistingAccount, BigDecimal.ONE);

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND_404.getStatusCode()));
    }

    @Test
    public void shouldNotTransferFromNonExistingAccount() {
        // given
        int accountId = createAccountById();
        int nonExistingAccount = 647832549;

        // when
        Response response = moneyTransferResponse(nonExistingAccount, accountId, BigDecimal.ONE);

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND_404.getStatusCode()));
    }

    @Test
    public void shouldNotTransferNegative() {
        // given
        int accountFromId = createAccountById();
        int accountToId = createAccountById();

        // when
        Response response = moneyTransferResponse(accountFromId, accountToId, new BigDecimal("-1"));

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.BAD_REQUEST_400.getStatusCode()));
    }

    @Test
    public void shouldNotTransferZeroAmount() {
        // given
        int accountFromId = createAccountById();
        int accountToId = createAccountById();

        // when
        Response response = moneyTransferResponse(accountFromId, accountToId, BigDecimal.ZERO);

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.BAD_REQUEST_400.getStatusCode()));
    }

    @Test
    public void shouldNotTransferToTheSameAccount() {
        // given
        int accountId = createAccountById();

        // when
        Response response = moneyTransferResponse(accountId, accountId, BigDecimal.ONE);

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.BAD_REQUEST_400.getStatusCode()));
    }

    @Test
    public void shouldTransferMoneyFromOneAccountToTheOther() {
        // given
        int balanceA = 100;
        int balanceB = 150;
        int transferAmount = 75;
        int accountAId = createAccountById(balanceA);
        int accountBId = createAccountById(balanceB);

        // when
        MoneyTransferResult moneyTransferResult = moneyTransfer(accountAId, accountBId, transferAmount);

        // then
        assertThat(moneyTransferResult, notNullValue());
        assertThat(moneyTransferResult.getAmount().intValue(), equalTo(transferAmount));

        Account withdrawalAccount = moneyTransferResult.getWithdrawalAccount();
        assertThat(withdrawalAccount.getBalance().intValue(), equalTo(balanceA - transferAmount));

        Account depositAccount = moneyTransferResult.getDepositAccount();
        assertThat(depositAccount.getBalance().intValue(), equalTo(balanceB + transferAmount));
    }

    @Test
    public void shouldTransferMoneyAmountWithFractions() {
        // given
        BigDecimal balanceA = new BigDecimal("99.554354654367");
        BigDecimal balanceB = new BigDecimal("150.111111111");
        BigDecimal transferAmount = new BigDecimal("75.95464574567");
        int accountAId = createAccountById(balanceA);
        int accountBId = createAccountById(balanceB);

        // when
        MoneyTransferResult moneyTransferResult = moneyTransfer(accountAId, accountBId, transferAmount);

        // then
        assertThat(moneyTransferResult, notNullValue());
        assertThat(moneyTransferResult.getAmount().compareTo(transferAmount), equalTo(0));

        Account withdrawalAccount = moneyTransferResult.getWithdrawalAccount();
        assertThat(withdrawalAccount.getBalance().compareTo(balanceA.subtract(transferAmount)), equalTo(0));

        Account depositAccount = moneyTransferResult.getDepositAccount();
        assertThat(depositAccount.getBalance().compareTo(balanceB.add(transferAmount)), equalTo(0));
    }

    private MoneyTransferResult moneyTransfer(int withdrawalAccountId, int depositAccountId, int transferAmount) {
        return moneyTransfer(withdrawalAccountId, depositAccountId, new BigDecimal(transferAmount));
    }

    private MoneyTransferResult moneyTransfer(int withdrawalAccountId, int depositAccountId, BigDecimal transferAmount) {
        Response response = moneyTransferResponse(withdrawalAccountId, depositAccountId, transferAmount);
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));

        return response.readEntity(MoneyTransferResult.class);
    }

    private Response moneyTransferResponse(int withdrawalAccountId, int depositAccountId, BigDecimal transferAmount) {
        MoneyTransfer moneyTransfer = new MoneyTransfer(withdrawalAccountId, depositAccountId, transferAmount);

        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        return target.path(API_TRANSFER).request().post(entity);
    }

}
