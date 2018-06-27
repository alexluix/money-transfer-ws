package pro.landlabs.money.transfer.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pro.landlabs.money.transfer.Application;
import pro.landlabs.money.transfer.MyObjectMapperProvider;
import pro.landlabs.money.transfer.ws.value.Account;
import pro.landlabs.money.transfer.ws.value.CreateAccount;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class AccountsResourceTest {

    private static final String API_ENDPOINT = "accounts";

    private final TypeReference accountsTypeRef = new TypeReference<List<Account>>() { };
    private final GenericType<List<Account>> accountsGenericType = new GenericType<>(accountsTypeRef.getType());

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        server = Application.startServer();

        Client c = ClientBuilder.newClient()
                .register(MyObjectMapperProvider.class)
                .register(JacksonFeature.class);

        target = c.target(Application.BASE_URI);
    }

    @After
    public void tearDown() {
        server.shutdownNow();
    }

    @Test
    public void shouldCreateAccount() {
        // given
        int balance = 500;

        // when
        Response response = createAccount(balance);

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.CREATED_201.getStatusCode()));

        Account account = response.readEntity(Account.class);
        assertThat(account.getId(), greaterThan(0));
        assertThat(account.getBalance().intValue(), equalTo(balance));
    }

    @Test
    public void shouldGetAccount() {
        // given
        int balance = 507;
        int accountId = createAccountById(balance);

        // when
        Response response = target.path(API_ENDPOINT + "/" + accountId).request().get();

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));

        Account account = response.readEntity(Account.class);
        assertThat(account.getId(), greaterThan(0));
        assertThat(account.getBalance().intValue(), equalTo(balance));
    }

    @Test
    public void shouldNotGetNonExistingAccount() {
        // given
        int nonExistingAccountId = 567485326;

        // when
        Response response = target.path(API_ENDPOINT + "/" + nonExistingAccountId).request().get();

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.NO_CONTENT_204.getStatusCode()));
    }

    @Test
    public void shouldDeleteAccount() {
        // given
        int accountId = createAccountById(500);

        // when
        Response response = target.path(API_ENDPOINT + "/" + accountId).request().delete();

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.NO_CONTENT_204.getStatusCode()));

        Response listAllResponse = target.path(API_ENDPOINT).request().get();
        List<Account> accounts = listAllResponse.readEntity(accountsGenericType);
        Optional<Account> foundDeletedAccount = accounts.stream()
                .filter(account -> account.getId() == accountId).findFirst();

        assertThat(foundDeletedAccount.isPresent(), is(false));
    }

    @Test
    public void shouldNotDeleteNonExistingAccount() {
        // given
        int nonExistingAccountId = 340004328;

        // when
        Response response = target.path(API_ENDPOINT + "/" + nonExistingAccountId).request().delete();

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.NO_CONTENT_204.getStatusCode()));
    }

    @Test
    public void shouldCreateMultipleAccounts() {
        // given
        int balance1 = 601;
        int balance2 = 602;

        // when
        Response response1 = createAccount(balance1);
        Response response2 = createAccount(balance2);

        // then
        assertThat(response1.getStatus(), equalTo(HttpStatus.CREATED_201.getStatusCode()));
        assertThat(response2.getStatus(), equalTo(HttpStatus.CREATED_201.getStatusCode()));

        Account account1 = response1.readEntity(Account.class);
        assertThat(account1.getId(), greaterThan(0));
        assertThat(account1.getBalance().intValue(), equalTo(balance1));

        Account account2 = response2.readEntity(Account.class);
        assertThat(account2.getId(), greaterThan(0));
        assertThat(account2.getBalance().intValue(), equalTo(balance2));
    }

    @Test
    public void shouldCreateMultipleAccountsAndListThem() {
        // given
        int balance1 = 701;
        int balance2 = 702;

        int account1Id = createAccountById(balance1);
        int account2Id = createAccountById(balance2);

        // when
        Response response = target.path(API_ENDPOINT).request().get();

        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));
        assertThat(response.getMediaType(), equalTo(MediaType.APPLICATION_JSON_TYPE));

        List<Account> accounts = response.readEntity(accountsGenericType);
        assertThat(accounts.size(), greaterThanOrEqualTo(2));

        Optional<Account> account1 = accounts.stream()
                .filter(account -> account.getId() == account1Id).findFirst();
        assertThat(account1.isPresent(), is(true));
        assertThat(account1.get().getBalance().intValue(), is(balance1));

        Optional<Account> account2 = accounts.stream()
                .filter(account -> account.getId() == account2Id).findFirst();
        assertThat(account2.isPresent(), is(true));
        assertThat(account2.get().getBalance().intValue(), is(balance2));
    }

    private int createAccountById(int balance) {
        Response response = createAccount(balance);
        assertThat(response.getStatus(), equalTo(HttpStatus.CREATED_201.getStatusCode()));

        Account account = response.readEntity(Account.class);
        return account.getId();
    }

    private Response createAccount(int balance) {
        CreateAccount createAccount = new CreateAccount(new BigDecimal(balance));
        Entity<CreateAccount> entity = Entity.entity(createAccount, MediaType.APPLICATION_JSON_TYPE);
        return target.path(API_ENDPOINT).request().post(entity);
    }

}
