package pro.landlabs.money.transfer.ws;

import pro.landlabs.money.transfer.ws.value.Account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

    public static final int ACCOUNT_DEFAULT_ID = 1;
    public static final int ACCOUNT_DEFAULT_BALANCE = 150;

    @GET
    public Account getAccount() {
        return new Account(ACCOUNT_DEFAULT_ID, new BigDecimal(ACCOUNT_DEFAULT_BALANCE));
    }

}
