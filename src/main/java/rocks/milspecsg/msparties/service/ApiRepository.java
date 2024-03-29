package rocks.milspecsg.msparties.service;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msparties.PluginInfo;
import rocks.milspecsg.msparties.api.Repository;
import rocks.milspecsg.msparties.api.RepositoryCacheService;
import rocks.milspecsg.msparties.db.mongodb.MongoContext;
import rocks.milspecsg.msparties.model.Dbo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ApiRepository<T extends Dbo> implements Repository<T> {

    protected MongoContext mongoContext;

    @Inject
    public ApiRepository(MongoContext mongoContext) {
        this.mongoContext = mongoContext;
    }

    @Override
    public CompletableFuture<Optional<T>> insertOne(T item) {
        return CompletableFuture.supplyAsync(() -> {
            Key<T> key = mongoContext.datastore.save(item);
            item.setId((ObjectId) key.getId());
            return Optional.of(item);
        });
    }

    @Override
    public CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(id).get()));
    }

    @Override
    public UpdateOperations<T> inc(String field, Number value) {
        return createUpdateOperations().inc(field, value);
    }

    @Override
    public UpdateOperations<T> inc(String field) {
        return inc(field, 1);
    }

    @Override
    public Query<T> asQuery(ObjectId id) {
        return asQuery().field("id").equal(id);
    }

    public <R extends RepositoryCacheService<T>> Supplier<List<T>> saveToCache(R repositoryCacheService, Supplier<List<T>> fromDB) {
        return () -> repositoryCacheService.put(fromDB.get());
    }

    @Override
    public <R extends RepositoryCacheService<T>> Supplier<Optional<T>> ifNotPresent(R repositoryCacheService, Function<R, Optional<T>> fromCache, Supplier<Optional<T>> fromDB) {
        Optional<T> main = fromCache.apply(repositoryCacheService);
        if (main.isPresent()) {
            Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Found in cache"));
            return () -> main;
        } else {
            Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Not present in cache, saving"));
            return () -> fromDB.get().flatMap(repositoryCacheService::put);
        }
    }

    @Override
    public Supplier<Optional<T>> ifNotPresent(RepositoryCacheService<T> repositoryCacheService, ObjectId id) {
        return ifNotPresent(repositoryCacheService, service -> service.getOne(id), () -> Optional.ofNullable(asQuery(id).get()));
    }

}
