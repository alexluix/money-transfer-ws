package pro.landlabs.money.transfer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountService {

    private final static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AtomicInteger idSequence = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = Maps.newConcurrentMap();

    public List<Account> getAccounts() {
        return ImmutableList.copyOf(accounts.values());
    }

    public Account createAccount(CreateAccount createAccount) {
        if (createAccount == null) throw new IllegalArgumentException();

        Account account = new Account(idSequence.getAndIncrement(), createAccount.getBalance());
        accounts.put(account.getId(), account);

        logger.info("Account created: {}", account);

        return account;
    }

    public Account getAccount(int accountId) {
        return accounts.get(accountId);
    }

    public void deleteAccount(int accountId) {
        Account deletedAccount = accounts.remove(accountId);

        if (deletedAccount != null) {
            logger.info("Account deleted: {}", deletedAccount);
        }
    }

}
