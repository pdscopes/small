package org.madesimple.small.agent;


import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Configurable;

/**
 * <p>
 * Agents inform the Environment which action they are to take by giving it the
 * reference integer assigned to said action. The integers are assigned from
 * 0 - n, where n is the number of available actions. Because of this an array
 * of doubles with the action/value pairs can be provided and then an
 * implementing algorithm will return the index of the action chosen.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface Policy extends Configurable {
    /**
     * This method should return an action given <em>state</em> at <em>time</em>.
     *
     * @param state An array of values for the index action choice
     * @param time  Time step
     * @return The index action that has been selected
     */
    int select(State state, int time);
}