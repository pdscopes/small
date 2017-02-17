package org.madesimple.small.utility;

/**
 * <p>
 * Factory is a generic interface that allows for the creation of
 * the generic type <em>E</em> being created.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface Factory<E> {
    E generate();
}
