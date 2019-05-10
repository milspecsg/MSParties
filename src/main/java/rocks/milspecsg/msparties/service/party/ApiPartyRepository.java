package rocks.milspecsg.msparties.service.party;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msparties.api.party.PartyRepository;
import rocks.milspecsg.msparties.db.mongodb.MongoContext;
import rocks.milspecsg.msparties.model.core.Member;
import rocks.milspecsg.msparties.model.core.Party;
import rocks.milspecsg.msparties.model.exceptions.*;
import rocks.milspecsg.msparties.model.results.UpdateResult;
import rocks.milspecsg.msparties.service.ApiRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiPartyRepository extends ApiRepository<Party> implements PartyRepository {

    private List<String> cachedPartyNames;

    @Inject
    public ApiPartyRepository(MongoContext mongoContext) {
        super(mongoContext);
        cachedPartyNames = new ArrayList<>();
    }

    @Override
    public CompletableFuture<Optional<? extends Party>> createParty(String name, User leader) throws InvalidNameException {
        // todo: name verification

        return CompletableFuture.supplyAsync(() -> {
            Party party = new Party();
            party.name = name;
            party.leaderUUID = leader.getUniqueId();

            return insertOneAsync(party).join();
        });


    }

    @Override
    public CompletableFuture<UpdateResult> disbandParty() {
        return null;
    }

    @Override
    public CompletableFuture<UpdateResult> joinParty(User user, Party party) throws PartyFullException, BannedFromPartyException {
        return null;
    }

    @Override
    public CompletableFuture<UpdateResult> leaveParty(User user, Party party) throws CannotLeavePartyAsLeaderException, NotInPartyException {
        return null;
    }

    @Override
    public CompletableFuture<UpdateResult> renameParty(String name, Party party) throws IllegalNameException, NotInPartyException {
        return null;
    }

    @Override
    public CompletableFuture<UpdateResult> inviteUser(User user) throws NotInPartyException {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Party>> getAll(String name) {
        return CompletableFuture.supplyAsync(() -> asQuery().field("name").containsIgnoreCase(name).asList());
    }

    @Override
    public CompletableFuture<Optional<? extends Party>> getOne(String name) {
        return CompletableFuture.supplyAsync(() -> {
            List<? extends Party> parties = getAll(name).join();
            return parties.size() > 0 ? Optional.of(parties.get(0)) : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<? extends Party>> getAll(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> asQuery().filter("memberUUIDs elem", userUUID).asList());
    }

    @Override
    public CompletableFuture<Optional<? extends Party>> getOne(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> {
            List<? extends Party> parties = getAll(userUUID).join();
            return parties.size() > 0 ? Optional.of(parties.get(0)) : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<? extends Party>> getOneAsync(ObjectId id) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public UpdateOperations<Party> createUpdateOperations() {
        return mongoContext.datastore.createUpdateOperations(Party.class);
    }

    @Override
    public Query<Party> asQuery() {
        return mongoContext.datastore.createQuery(Party.class);
    }
}
