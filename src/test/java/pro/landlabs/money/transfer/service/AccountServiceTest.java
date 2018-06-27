package pro.landlabs.money.transfer.service;

import org.junit.Test;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.MoneyTransferResult;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AccountServiceTest extends AccountServiceAbstractTest {

    @Test
    public void shouldCreateAccount() {
        int balance = 501;

        Account account = createAccount(balance);

        assertThat(account, notNullValue());
        assertThat(account.getId(), greaterThanOrEqualTo(1));
        assertThat(account.getBalance().intValue(), equalTo(balance));
    }

    @Test
    public void shouldGetAccount() {
        int balance = 601;
        int accountId = createAccount(balance).getId();

        Account account = subject.getAccount(accountId);

        assertThat(account, notNullValue());
        assertThat(account.getId(), equalTo(accountId));
        assertThat(account.getBalance().intValue(), equalTo(balance));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNullWhenCreate() {
        subject.createAccount(null);
    }

    @Test
    public void shouldDeleteAccount() {
        // given
        int accountId = createAccount().getId();
        Account account = subject.getAccount(accountId);
        assertThat(account.getId(), equalTo(accountId));

        // when
        subject.deleteAccount(accountId);

        // then
        Account deletedAccount = subject.getAccount(accountId);
        assertThat(deletedAccount, nullValue());
    }

    @Test
    public void shouldGetAccounts() {
        // given
        int balance = 301;
        Account account = createAccount(balance);

        // when
        List<Account> accounts = subject.getAccounts();

        // then
        Optional<Account> foundAccount = accounts.stream()
                .filter(a -> a.getId() == account.getId()).findFirst();

        assertThat(foundAccount.isPresent(), is(true));
        assertThat(foundAccount.get().getBalance().intValue(), equalTo(balance));
    }

    @Test
    public void shouldGetMultipleAccounts() {
        // given
        int balance1 = 801;
        int balance2 = 802;
        Account account1 = createAccount(balance1);
        Account account2 = createAccount(balance2);

        // when
        List<Account> accounts = subject.getAccounts();

        // then
        assertThat(accounts.size(), greaterThanOrEqualTo(2));

        Optional<Account> foundAccount1 = accounts.stream().filter(a -> a.getId() == account1.getId()).findFirst();
        assertThat(foundAccount1.isPresent(), is(true));
        assertThat(foundAccount1.get().getBalance().intValue(), equalTo(balance1));

        Optional<Account> foundAccount2 = accounts.stream().filter(a -> a.getId() == account2.getId()).findFirst();
        assertThat(foundAccount2.isPresent(), is(true));
        assertThat(foundAccount2.get().getBalance().intValue(), equalTo(balance2));
    }

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
