package rocks.milspecsg.msparties.api.member;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msparties.api.Repository;
import rocks.milspecsg.msparties.model.core.Member;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository<M extends Member> extends Repository<M> {

    /**
     * Represents the default singular identifier for a group
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "Member", ""
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return {@code "Member"} by default, otherwise the identifier specified by a subclass
     */
    default String getDefaultIdentifierSingularUpper() {
        return "Member";
    }

    /**
     * Represents the default plural identifier for a group
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "Members"
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return {@code "Parties"} by default, otherwise the identifier specified by a subclass
     */
    default String getDefaultIdentifierPluralUpper() {
        return "Members";
    }

    /**
     * Represents the default singular identifier for a group
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clan", "faction", "guild" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return {@code "party"} by default, otherwise the identifier specified by a subclass
     *
     * <p>
     * note: this will be used as the base command
     * </p>
     */
    default String getDefaultIdentifierSingularLower() {
        return "member";
    }

    /**
     * Represents the default plural identifier for a group
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clans", "factions", "guilds" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return {@code "parties"} by default, otherwise the identifier specified by a subclass
     */
    default String getDefaultIdentifierPluralLower() {
        return "members";
    }

    /**
     * Gets the corresponding {@code Member} from the database.
     * If not present, creates a new one and saves it to the database
     *
     * @param userUUID Mojang issued {@code uuid} of {@code User} to getRequiredRankIndex corresponding {@code Member}
     * @return a ready-to-use {@code Member} that corresponds with the given {@code uuid}
     */
    CompletableFuture<Optional<M>> getOneOrGenerate(UUID userUUID);


    CompletableFuture<Optional<M>> getOne(UUID userUUID);


    Optional<User> getUser(UUID uuid);


    Optional<User> getUser(String lastKnownName);


    CompletableFuture<Optional<ObjectId>> getId(UUID uuid);


    CompletableFuture<Optional<UUID>> getUUID(ObjectId id);


    CompletableFuture<Optional<User>> getUser(ObjectId id);


    Query<M> asQuery(UUID userUUID);

}
