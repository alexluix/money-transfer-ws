package pro.landlabs.money.transfer.ws;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pro.landlabs.money.transfer.Application;
import pro.landlabs.money.transfer.MyObjectMapperProvider;
import pro.landlabs.money.transfer.ws.value.Account;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AccountsResourceTest {

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
    public void shouldReturnAccountJson() {
        Response response = target.path("accounts").request().get();
        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200.getStatusCode()));
        assertThat(response.getMediaType(), equalTo(MediaType.APPLICATION_JSON_TYPE));

        Account account = response.readEntity(Account.class);
        assertThat(account.getId(), equalTo(AccountsResource.ACCOUNT_DEFAULT_ID));
        assertThat(account.getBalance().intValue(), equalTo(AccountsResource.ACCOUNT_DEFAULT_BALANCE));
    }

}
