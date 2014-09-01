/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @authors Nippon Telegraph and Telephone Corporation
 */

package jp.co.ntt.oss.jboss.byteman.framework.adapter;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackIF;
import jp.co.ntt.oss.jboss.byteman.framework.util.Logger;

import org.jboss.byteman.rule.helper.Helper;

/**
 * The default implementation of {@link DistributedAdapter}. <br/>
 *  As for the following methods, they wrap and call corresponding {@link Helper} class's methods on the controller node.
 * <ul>
 * <li>{@link #waiting(Object)}</li>
 * <li>{@link #waitFor(Object, long)}</li>
 * <li>{@link #signalWake(Object, boolean)}</li>
 * <li>{@link #signalThrow(Object, boolean)}</li>
 * <li>{@link #createRendezvous(Object, int, boolean)}</li>
 * <li>{@link #isRendezvous(Object, int)}</li>
 * <li>{@link #getRendezvous(Object, int)}</li>
 * <li>{@link #rendezvous(Object)}</li>
 * <li>{@link #deleteRendezvous(Object, int)}</li>
 * <li>{@link #createJoin(Object, int)}</li>
 * <li>{@link #isJoin(Object, int)}</li>
 * <li>{@link #joinEnlist(Object)}</li>
 * <li>{@link #joinWait(Object, int)}</li>
 * <li>{@link #flag(Object)}</li>
 * <li>{@link #flagged(Object)}</li>
 * <li>{@link #clear(Object)}</li>
 * <li>{@link #isCountDown(Object)}</li>
 * <li>{@link #createCountDown(Object, int)}</li>
 * <li>{@link #countDown(Object)}</li>
 * <li>{@link #createCounter(Object, int)}</li>
 * <li>{@link #deleteCounter(Object)}</li>
 * <li>{@link #readCounter(Object, boolean)}</li>
 * <li>{@link #incrementCounter(Object, int)}</li>
 * <li>{@link #decrementCounter(Object)}</li>
 * <li>{@link #createTimer(Object)}</li>
 * <li>{@link #deleteTimer(Object)}</li>
 * <li>{@link #getElapsedTimeFromTimer(Object)}</li>
 * <li>{@link #resetTimer(Object)}</li>
 * <li>{@link #traceOpen(Object, String)}</li>
 * <li>{@link #traceClose(Object)}</li>
 * <li>{@link #trace(Object, String)}</li>
 * <li>{@link #traceln(Object, String)}</li>
 * </ul> 
 */
public class DistributedAdapterImpl implements DistributedAdapter {
	/** Default helper. **/
	protected Helper helper = new Helper(null) {
	};
	/** Logger. **/
	protected Logger logger = Logger.getLogger();

	private static Map<Object, CallbackIF> callbacks = new ConcurrentHashMap<Object, CallbackIF>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean waiting(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.waiting(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitFor(Object identifier, long millisecs) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		helper.waitFor(identifier, millisecs);
		logger.debug("End identifier %s", identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean signalWake(Object identifier, boolean mustMeet) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.signalWake(identifier, mustMeet);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean signalThrow(Object identifier, boolean mustMeet) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.signalThrow(identifier, mustMeet);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createRendezvous(Object identifier, int expected, boolean restartable) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.createRendezvous(identifier, expected, restartable);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRendezvous(Object identifier, int expected) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.isRendezvous(identifier, expected);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRendezvous(Object identifier, int expected) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		int result = helper.getRendezvous(identifier, expected);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int rendezvous(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		int result = helper.rendezvous(identifier);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteRendezvous(Object identifier, int expected) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.deleteRendezvous(identifier, expected);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createJoin(Object identifier, int max) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.createJoin(identifier, max);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isJoin(Object identifier, int max) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.isJoin(identifier, max);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean joinEnlist(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.joinEnlist(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean joinWait(Object identifier, int count) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.joinWait(identifier, count);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flag(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.flag(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flagged(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.flagged(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clear(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.clear(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCountDown(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.isCountDown(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean countDown(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.countDown(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createCountDown(Object identifier, int count) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.createCountDown(identifier, count);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createCounter(Object identifier, int value) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.createCounter(identifier, value);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteCounter(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.deleteCounter(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readCounter(Object identifier, boolean zero) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		int result = helper.readCounter(identifier, zero);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int incrementCounter(Object identifier, int amount) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		int result = helper.incrementCounter(identifier, amount);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int decrementCounter(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		int result = helper.decrementCounter(identifier);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createTimer(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.createTimer(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteTimer(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.deleteTimer(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getElapsedTimeFromTimer(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		long result = helper.getElapsedTimeFromTimer(identifier);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long resetTimer(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		long result = helper.resetTimer(identifier);
		logger.debug("End identifier %s: Returns %d", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceOpen(Object identifier, String fileName) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.traceOpen(identifier, fileName);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceClose(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.traceClose(identifier);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean trace(Object identifier, String message) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.trace(identifier, message);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceln(Object identifier, String message) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		boolean result = helper.traceln(identifier, message);
		logger.debug("End identifier %s: Returns %b", identifier, result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void killRemoteJVM(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		killRemoteJVM(identifier, -1);
		logger.debug("End identifier %s", identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void killRemoteJVM(Object identifier, int exitCode) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		CallbackIF callback = callbacks.get(identifier);
		if(callback != null) {
			try {
				callback.invoke(exitCode);
			} catch(Exception ex){
				// ignored for kill itself
			}
			logger.debug("End identifier %s", identifier);
			return;
		}
		RuntimeException e = new IllegalArgumentException(String.format("The callback object of the identifier [%s] is not registered.", identifier));
		logger.error(e, "identifier %s", identifier);
		throw e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerCallback(Object identifier, Object target) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		callbacks.put(identifier, (CallbackIF) target);
		logger.debug("End identifier %s", identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregisterCallback(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		callbacks.remove(identifier);
		logger.debug("End identifier %s", identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object doCallback(Object identifier, Object... parameters) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		CallbackIF callback = callbacks.get(identifier);
		if(callback != null) {
			Object result = callback.invoke(parameters);
			logger.debug("End identifier %s: Returns %s", identifier, result);
			return result;
		}
		RuntimeException e = new IllegalArgumentException(String.format("The callback object of the identifier [%s] is not registered.", identifier));
		logger.error(e, "identifier %s", identifier);
		throw e;
	}

}
