package rocks.milspecsg.msparties.service.party;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msparties.ConfigKeys;
import rocks.milspecsg.msparties.MSParties;
import rocks.milspecsg.msparties.api.config.ConfigurationService;
import rocks.milspecsg.msparties.api.party.PartyCacheService;
import rocks.milspecsg.msparties.model.core.Party;
import rocks.milspecsg.msparties.service.ApiRepositoryCacheService;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Singleton
public class ApiPartyCacheService<P extends Party> extends ApiRepositoryCacheService<P> implements PartyCacheService<P> {

    @Inject
    public ApiPartyCacheService(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public Optional<P> getOne(String name) {
        return getOne(party -> party.name.equalsIgnoreCase(name));
    }

    @Override
    public void remove(String name) {
        remove(party -> party.name.equalsIgnoreCase(name));
    }
}
