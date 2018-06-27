package pro.landlabs.money.transfer.ws.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MoneyTransferResult {

    private final Account withdrawalAccount;
    private final Account depositAccount;
    private final BigDecimal amount;

    @JsonCreator
    public MoneyTransferResult(
            @JsonProperty("withdrawalAccount") Account withdrawalAccount,
            @JsonProperty("depositAccount") Account depositAccount,
            @JsonProperty("amount") BigDecimal amount) {
        this.withdrawalAccount = withdrawalAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
    }

    public Account getWithdrawalAccount() {
        return withdrawalAccount;
    }

    public Account getDepositAccount() {
        return depositAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
