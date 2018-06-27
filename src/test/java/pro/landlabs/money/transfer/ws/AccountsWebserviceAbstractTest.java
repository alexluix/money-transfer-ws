package pro.landlabs.money.transfer.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public abstract class AccountsWebserviceAbstractTest {

    protected static final int DEFAULT_BALANCE = 1000;

    protected static final String API_ENDPOINT = "accounts";

    protected final TypeReference accountsTypeRef = new TypeReference<List<Account>>() {};
    protected final GenericType<List<Account>> accountsGenericType = new GenericType<>(accountsTypeRef.getType());

    protected HttpServer server;
    protected WebTarget target;

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

    protected int createAccountById() {
        return createAccountById(DEFAULT_BALANCE);
    }

    protected int createAccountById(int balance) {
        return createAccountById(new BigDecimal(balance));
    }

    protected int createAccountById(BigDecimal balance) {
        Response response = createAccount(balance);
        assertThat(response.getStatus(), equalTo(HttpStatus.CREATED_201.getStatusCode()));

        Account account = response.readEntity(Account.class);
        return account.getId();
    }

    protected Response createAccount(int balance) {
        return createAccount(new BigDecimal(balance));
    }

    protected Response createAccount(BigDecimal balance) {
        CreateAccount createAccount = new CreateAccount(balance);
        Entity<CreateAccount> entity = Entity.entity(createAccount, MediaType.APPLICATION_JSON_TYPE);
        return target.path(API_ENDPOINT).request().post(entity);
    }

}
