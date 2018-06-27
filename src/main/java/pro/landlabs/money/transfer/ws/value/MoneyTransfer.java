package pro.landlabs.money.transfer.ws.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MoneyTransfer {

    private final int withdrawalAccountId;
    private final int depositAccountId;
    private final BigDecimal amount;

    @JsonCreator
    public MoneyTransfer(
            @JsonProperty("withdrawalAccountId") int withdrawalAccountId,
            @JsonProperty("depositAccountId") int depositAccountId,
            @JsonProperty("amount") BigDecimal amount) {

        this.withdrawalAccountId = withdrawalAccountId;
        this.depositAccountId = depositAccountId;
        this.amount = amount;
    }

    public int getWithdrawalAccountId() {
        return withdrawalAccountId;
    }

    public int getDepositAccountId() {
        return depositAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
