package pro.landlabs.money.transfer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountService {

    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = Maps.newConcurrentMap();

    public List<Account> getAccounts() {
        return ImmutableList.copyOf(accounts.values());
    }

    public Account createAccount(CreateAccount createAccount) {
        if (createAccount == null) throw new IllegalArgumentException();

        Account account = new Account(idSequence.getAndIncrement(), createAccount.getBalance());
        accounts.put(account.getId(), account);

        return account;
    }

    public Account getAccount(int accountId) {
        return accounts.get(accountId);
    }

}
