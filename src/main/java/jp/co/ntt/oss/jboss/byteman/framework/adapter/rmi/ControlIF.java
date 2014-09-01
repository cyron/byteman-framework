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

package jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jboss.byteman.rule.helper.Helper;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;

/**
 * The RMI communication endpoint interface of the controller for test.
 *
 */
public interface ControlIF extends Remote, DistributedAdapter {
	/** Control ID. **/
	public static final String CONTROL_ID = "DISTRIBUTED_CONTROL";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean waiting(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitFor(Object identifier, long millisecs) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean signalWake(Object identifier, boolean mustMeet) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean signalThrow(Object identifier, boolean mustMeet) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createRendezvous(Object identifier, int expected, boolean restartable) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRendezvous(Object identifier, int expected) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRendezvous(Object identifier, int expected) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int rendezvous(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteRendezvous(Object identifier, int expected) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createJoin(Object identifier, int max) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isJoin(Object identifier, int max) throws RemoteException;

	/**
	 * This method is not supported for execution over RMI.
	 * Use {@link #joinEnlistStart(Object)} and {@link #joinEnlistEnd(String)} instead.
	 */
	@Override
	public boolean joinEnlist(Object identifier) throws RemoteException;

	/**
	 * Executes {@link Helper#joinEnlist(Object)} within the thread generated newly.
	 * The generated thread waits until it is notified at {@link #joinEnlistEnd(String)}.
	 *
	 * @param identifier an identifier for join
	 * @return the key which specifies a thread on server if it succeeds, otherwise <code>null</code>.
	 * @see #joinEnlistEnd(String)
	 */
	public String joinEnlistStart(Object identifier) throws RemoteException;

	/**
	 * Executes notify to the thread by the specified key.
	 * The thread is waiting by processing of {@link #joinEnlistStart(Object)}.
	 *
	 * @param key the key which specifies a thread on server.
	 * @see #joinEnlistStart(Object)
	 */
	public void joinEnlistEnd(String key) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean joinWait(Object identifier, int count) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flag(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flagged(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clear(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCountDown(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createCountDown(Object identifier, int count) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean countDown(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createCounter(Object identifier, int value) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteCounter(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readCounter(Object identifier, boolean zero) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int incrementCounter(Object identifier, int amount) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int decrementCounter(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createTimer(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteTimer(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getElapsedTimeFromTimer(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long resetTimer(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceOpen(Object identifier, String fileName) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceClose(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean trace(Object identifier, String message) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traceln(Object identifier, String message) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void killRemoteJVM(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void killRemoteJVM(Object identifier, int exitCode) throws RemoteException;

	/**
	 * Registers the callback object. The callback object needs to be implemented {@link CallbackIF}.
	 *
	 * @param identifier an identifier for the callback
	 * @param target the callback object
	 * @throws Exception
	 */
	@Override
	public void registerCallback(Object identifier, Object target) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregisterCallback(Object identifier) throws RemoteException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object doCallback(Object identifier, Object... parameters) throws RemoteException;
}
