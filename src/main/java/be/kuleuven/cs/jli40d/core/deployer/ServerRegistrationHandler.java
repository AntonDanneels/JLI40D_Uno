package be.kuleuven.cs.jli40d.core.deployer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * This class provides an endpoint for general servers to connect to
 * a deployment server on booting.
 *
 * @author Pieter
 * @version 1.0
 */
public interface ServerRegistrationHandler extends Remote, Serializable
{
    /**
     * This function allows a server to obtain a port for a certain hostname.
     * <p>
     * Note that hostname resolving is not implied in this contract. This means
     * that e.g. localhost and 127.0.0.1 might be equal, but this is not
     * guaranteed by the contract.
     * <p>
     * Note also that the deployer has no knowledge of available/non-blocked ports
     * on the host. This means it's up to the host to verify the functionality of
     * the given port.
     *
     * @param host The hostname/ip address of the host requesting a port.
     * @param serverType The type of server as a {@link ServerType} enum.
     * @return A {@link Server} object with the provided hostname and generated port number.
     * @throws RemoteException
     */
    Server obtainPort( String host, ServerType serverType ) throws RemoteException;

    /**
     * After the deployer is finished booting, has obtained a port and has registered
     * his RMI server, this function can be called.
     * <p>
     * It provides al list with all known other servers. This is blocking and
     * will only return when all expected servers have also called this function and
     * are registered on the deployer.
     *
     * @param self The {@link Server} object given by {@link #obtainPort(String, ServerType)}.
     * @return A set of all {@link Server} objects, including the caller itself.
     * @throws RemoteException
     */
    Set<Server> register( Server self ) throws RemoteException;

}
