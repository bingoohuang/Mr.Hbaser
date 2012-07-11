package org.phw.core.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * A ThreadContext provides a means of binding and unbinding objects to the
 * current thread based on key/value pairs.
 * <p/>
 * <p>An internal {@link java.util.HashMap} is used to maintain the key/value pairs
 * for each thread.</p>
 * <p/>
 * <p>If the desired behavior is to ensure that bound data is not shared across
 * threads in a pooled or reusable threaded environment, the application (or more likely a framework) must
 * bind and remove any necessary values at the beginning and end of stack
 * execution, respectively (i.e. individually explicitly or all via the <tt>clear</tt> method).</p>
 *
 * @see #remove()
 * @since 0.1
 */
public abstract class ThreadContext {
    private static final ThreadLocal<Map<Object, Object>> resources;
    static {
        resources = new InheritableThreadLocalMap<Map<Object, Object>>();
    }

    /**
     * Default no-argument constructor.
     */
    protected ThreadContext() {
    }

    /**
     * Returns the ThreadLocal Map. This Map is used internally to bind objects
     * to the current thread by storing each object under a unique key.
     *
     * @return the map of bound resources
     */
    public static Map<Object, Object> getResources() {
        return resources != null ? new HashMap<Object, Object>(resources.get()) : null;
    }

    /**
     * Allows a caller to explicitly set the entire resource map.  This operation overwrites everything that existed
     * previously in the ThreadContext - if you need to retain what was on the thread prior to calling this method,
     * call the {@link #getResources()} method, which will give you the existing state.
     *
     * @param newResources the resources to replace the existing {@link #getResources() resources}.
     * @since 1.0
     */
    public static void setResources(Map<?, ?> newResources) {
        if (Collections.isEmpty(newResources)) {
            return;
        }

        resources.get().clear();
        resources.get().putAll(newResources);
    }

    /**
     * Returns the value bound in the {@code ThreadContext} under the specified {@code key}, or {@code null} if there
     * is no value for that {@code key}.
     *
     * @param <T> Type Return.
     * @param key the map key to use to lookup the value
     * @return the value bound in the {@code ThreadContext} under the specified {@code key}, or {@code null} if there
     *         is no value for that {@code key}.
     * @since 1.0
     */
    public static <T> T get(Object key) {
        return (T) resources.get().get(key);
    }

    /**
     * Binds <tt>value</tt> for the given <code>key</code> to the current thread.
     * <p/>
     * <p>A <tt>null</tt> <tt>value</tt> has the same effect as if <tt>remove</tt> was called for the given
     * <tt>key</tt>, i.e.:
     * <p/>
     * <pre>
     * if ( value == null ) {
     *     remove( key );
     * }</pre>
     *
     * @param key   The key with which to identify the <code>value</code>.
     * @param value The value to bind to the thread.
     */
    public static void put(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        if (value == null) {
            remove(key);
            return;
        }

        resources.get().put(key, value);
    }

    /**
     * Unbinds the value for the given <code>key</code> from the current
     * thread.
     *
     * @param key The key identifying the value bound to the current thread.
     * @return the object unbound or <tt>null</tt> if there was nothing bound
     *         under the specified <tt>key</tt> name.
     */
    public static Object remove(Object key) {
        return resources.get().remove(key);
    }

    /**
     * {@link ThreadLocal#remove Remove}s the underlying {@link ThreadLocal ThreadLocal} from the thread.
     * <p/>
     * This method is meant to be the final 'clean up' operation that is called at the end of thread execution to
     * prevent thread corruption in pooled thread environments.
     *
     * @since 1.0
     */
    public static void remove() {
        resources.remove();
    }

    /**
     * Returns true if a value for the <code>key</code> is bound to the current thread, false otherwise.
     *
     * @param key the key that may identify a value bound to the current thread.
     * @return true if a value for the key is bound to the current thread, false
     *         otherwise.
     */
    public static boolean containsKey(Object key) {
        return getResources().containsKey(key);
    }

    /**
     * Inheritale  Thread LocalMap.
     * @author BingooHuang
     *
     * @param <T>
     */
    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends
            InheritableThreadLocal<Map<Object, Object>> {
        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap<Object, Object>();
        }

        /**
         * This implementation was added to address a
         * <a href="http://jsecurity.markmail.org/search/?q=#query:+page:1+mid:xqi2yxurwmrpqrvj+state:results">
         * user-reported issue</a>.
         * @param parentValue the parent value, a HashMap as defined in the {@link #initialValue()} method.
         * @return the HashMap to be used by any parent-spawned child threads (a clone of the parent HashMap).
         */
        @Override
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            }
            return null;
        }
    }
}
