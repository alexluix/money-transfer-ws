package pro.landlabs.money.transfer;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import pro.landlabs.money.transfer.service.AccountService;

public class DependencyBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(new AccountService());
    }

}
