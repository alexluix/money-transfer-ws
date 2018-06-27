package pro.landlabs.money.transfer.service;

import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import java.math.BigDecimal;

public class AccountServiceAbstractTest {

    public static final int DEFAULT_BALANCE = 1000;

    AccountService subject = new AccountService();


    Account createAccount() {
        return createAccount(DEFAULT_BALANCE);
    }

    Account createAccount(int balance) {
        return subject.createAccount(new CreateAccount(new BigDecimal(balance)));
    }

}
