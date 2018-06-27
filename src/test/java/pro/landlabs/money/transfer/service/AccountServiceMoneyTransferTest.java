package pro.landlabs.money.transfer.service;

import org.junit.Test;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;

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

    @Test
    public void shouldTransferMoney() {
        int accountAId = createAccount(100).getId();
        int accountBId = createAccount().getId();

        subject.transfer(accountAId, accountBId, new BigDecimal(-1));
    }

}
