package pro.landlabs.money.transfer.service;

import com.google.common.collect.Lists;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountService {

    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final List<Account> accounts = Lists.newArrayList();

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account createAccount(CreateAccount createAccount) {
        if (createAccount == null) throw new IllegalArgumentException();

        Account account = new Account(idSequence.getAndIncrement(), createAccount.getBalance());
        accounts.add(account);

        return account;
    }

}
