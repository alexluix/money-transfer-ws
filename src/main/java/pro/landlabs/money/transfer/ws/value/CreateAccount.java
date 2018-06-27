package pro.landlabs.money.transfer.ws.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CreateAccount {

    private final BigDecimal balance;

    @JsonCreator
    public CreateAccount(@JsonProperty("balance") BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
