package pro.landlabs.money.transfer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;
import pro.landlabs.money.transfer.ws.value.MoneyTransferResult;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
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

    public MoneyTransferResult transfer(int withdrawalAccountId, int depositAccountId, BigDecimal amount) {
        if (withdrawalAccountId == depositAccountId) throw new BadRequestException();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException();

        Account withdrawalAccount = accounts.get(withdrawalAccountId);
        Account depositAccount = accounts.get(depositAccountId);

        if (withdrawalAccount == null) {
            logger.info("Withdrawal account not found: {}", withdrawalAccountId);

            throw new NotFoundException();
        }
        if (depositAccount == null) {
            logger.info("Deposit account not found: {}", depositAccountId);

            throw new NotFoundException();
        }

        Object lock1;
        Object lock2;
        if (withdrawalAccount.getId() < depositAccount.getId()) {
            lock1 = withdrawalAccount;
            lock2 = depositAccount;
        } else {
            lock1 = depositAccount;
            lock2 = withdrawalAccount;
        }

        synchronized (lock1) {
            synchronized (lock2) {
                return doTransfer(amount, withdrawalAccount, depositAccount);
            }
        }
    }

    private MoneyTransferResult doTransfer(BigDecimal amount, Account withdrawalAccount, Account depositAccount) {
        BigDecimal balance = withdrawalAccount.getBalance();
        BigDecimal remaining = balance.subtract(amount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            logger.info("Insufficient funds: balance {}, requested {}", balance, amount);

            throw new NotAcceptableException();
        }

        withdrawalAccount.setBalance(remaining);
        depositAccount.setBalance(depositAccount.getBalance().add(amount));

        MoneyTransferResult moneyTransferResult =
                new MoneyTransferResult(withdrawalAccount, depositAccount, amount);

        logger.info("Money transferred: {}", moneyTransferResult);

        return moneyTransferResult;
    }

}
