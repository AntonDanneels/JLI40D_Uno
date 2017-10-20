package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;

/**
 * @author Pieter
 * @version 1.0
 */
public interface UserTokenHandler
{
    /**
     * Obtains the username using the token.
     *
     * @param token The provided token.
     * @return A username as a string.
     * @throws InvalidTokenException Thrown if the token doesn't match.
     */
    String findUserByToken( String token ) throws InvalidTokenException;

}
