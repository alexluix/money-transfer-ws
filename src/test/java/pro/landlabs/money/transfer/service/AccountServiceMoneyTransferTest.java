package pro.landlabs.money.transfer.service;

import org.junit.Test;
import pro.landlabs.money.transfer.ws.value.MoneyTransferResult;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AccountServiceMoneyTransferTest extends AccountServiceAbstractTest {

    @Test(expected = BadRequestException.class)
    public void shouldNotTransferToTheSameAccount() {
        int accountId = createAccount().getId();

        subject.transfer(accountId, accountId, BigDecimal.ONE);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotTransferZeroAmount() {
        int accountAId = createAccount().getId();
        int accountBId = createAccount().getId();

        subject.transfer(accountAId, accountBId, BigDecimal.ZERO);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotTransferNegative() {
        int accountAId = createAccount().getId();
        int accountBId = createAccount().getId();

        subject.transfer(accountAId, accountBId, new BigDecimal(-1));
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotTransferFromNonExistingAccount() {
        int nonExistingAccountId = 5476283;
        int accountId = createAccount().getId();

        subject.transfer(nonExistingAccountId, accountId, BigDecimal.ONE);
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotTransferToNonExistingAccount() {
        int accountId = createAccount().getId();
        int nonExistingAccountId = 5476283;

        subject.transfer(accountId, nonExistingAccountId, BigDecimal.ONE);
    }

    @Test
    public void shouldTransferCorrectly() {
        int balanceA = 100;
        int accountAId = createAccount(balanceA).getId();
        int balanceB = 150;
        int accountBId = createAccount(balanceB).getId();
        int transferAmount = 75;

        MoneyTransferResult result = subject.transfer(accountAId, accountBId, new BigDecimal(transferAmount));

        assertThat(result, notNullValue());
        assertThat(result.getAmount().intValue(), equalTo(transferAmount));
        assertThat(result.getWithdrawalAccount().getBalance().intValue(), equalTo(balanceA - transferAmount));
        assertThat(result.getDepositAccount().getBalance().intValue(), equalTo(balanceB + transferAmount));
    }

    @Test(expected = NotAcceptableException.class)
    public void shouldNotTransferWhenInsufficientFunds() {
        int accountAId = createAccount(50).getId();
        int accountBId = createAccount(100).getId();
        int transferAmount = 75;

        subject.transfer(accountAId, accountBId, new BigDecimal(transferAmount));
    }

}
