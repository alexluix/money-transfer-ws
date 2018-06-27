package pro.landlabs.money.transfer.service;

import org.junit.Test;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AccountServiceTest {

    private AccountService subject = new AccountService();

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
    public void shouldRejectNull() {
        subject.createAccount(null);
    }

    @Test
    public void shouldGetAccounts() {
        int balance = 301;
        Account account = createAccount(balance);

        List<Account> accounts = subject.getAccounts();
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

    private Account createAccount(int balance) {
        return subject.createAccount(new CreateAccount(new BigDecimal(balance)));
    }

}
