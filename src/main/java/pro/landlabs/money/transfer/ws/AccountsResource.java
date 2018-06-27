package pro.landlabs.money.transfer.ws;

import pro.landlabs.money.transfer.service.AccountService;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;
import pro.landlabs.money.transfer.ws.value.MoneyTransfer;
import pro.landlabs.money.transfer.ws.value.MoneyTransferResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Path("accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Inject
    private AccountService accountService;

    @GET
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GET
    @Path("{id}")
    public Account getAccount(@PathParam("id") int accountId) {
        return accountService.getAccount(accountId);
    }

    @POST
    public Response createAccount(CreateAccount createAccount) {
        Account account = accountService.createAccount(createAccount);

        return Response.created(URI.create("/accounts/" + account.getId()))
                .entity(account)
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteAccount(@PathParam("id") int accountId) {
        accountService.deleteAccount(accountId);

        return Response.noContent().build();
    }

    @POST
    @Path("transfer")
    public Response transfer(MoneyTransfer accountTransfer) {
        int withdrawalAccountId = accountTransfer.getWithdrawalAccountId();
        int depositAccountId = accountTransfer.getDepositAccountId();
        BigDecimal amount = accountTransfer.getAmount();
        MoneyTransferResult transferResult = accountService.transfer(withdrawalAccountId, depositAccountId, amount);

        return Response.ok().entity(transferResult).build();
    }

}
