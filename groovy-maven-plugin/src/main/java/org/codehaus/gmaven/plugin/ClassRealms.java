package org.codehaus.gmaven.plugin;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.strategy.Strategy;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class realm utilities.
 *
 * @since 2.0
 */
public class ClassRealms
{
    private static final AtomicInteger uniqueCounter = new AtomicInteger(0);

    public static String uniqueId() {
        return System.currentTimeMillis() + ":" + uniqueCounter.getAndIncrement();
    }

    /**
     * There is no public API to set/change a realms strategy, so we use some reflection muck to set the private field.
     */
    public static void setStrategy(final ClassRealm realm, final Strategy strategy) {
        assert realm != null;
        assert strategy != null;

        try {
            Field field = realm.getClass().getDeclaredField("strategy");

            try {
                field.set(realm, strategy);
            }
            catch (IllegalAccessException ignore) {
                // try again
                field.setAccessible(true);

                try {
                    field.set(realm, strategy);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                }
            }
        }
        catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }
}
