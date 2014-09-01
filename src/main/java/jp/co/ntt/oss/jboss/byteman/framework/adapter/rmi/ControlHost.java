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

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.byteman.rule.helper.Helper;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapterImpl;

/**
 * The implementation of {@link ControlIF}.
 *
 */
public class ControlHost extends DistributedAdapterImpl implements ControlIF {

	private Map<String, JoinServerThread> joinThreadMap = new ConcurrentHashMap<String, JoinServerThread>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean joinEnlist(final Object identifier) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String joinEnlistStart(Object identifier) throws RemoteException {
		logger.debug("Start identifier %s", identifier);
		Object lock = new Object();
		JoinServerThread thread = new JoinServerThread(identifier, lock);
		thread.start();
		synchronized (lock) {
			logger.debug("waiting until to finish joinEnlist");
			try {
				// wait until to finish Helper#joinEnlist in JoinServerThread#run.
				while(!thread.isEnlistFinished()) {
					lock.wait();
				}
			} catch (InterruptedException e) {
			}
			logger.debug("finish joinEnlist");
		}
		String key = null;
		if(thread.isResult()) {
			key = String.format("%d%d", System.currentTimeMillis(), thread.getId());
			joinThreadMap.put(key, thread);
		} else {
			synchronized (thread) {
				thread.notify();
			}
		}
		logger.debug("End identifier %s: Returns %s", identifier, key);
		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void joinEnlistEnd(String key) throws RemoteException {
		logger.debug("Start key %s", key);
		JoinServerThread thread = joinThreadMap.remove(key);
		if(thread != null) {
			synchronized (thread) {
				logger.debug("Start notify all thread");
				thread.notifyAll();
				logger.debug("End notify all thread");
			}
		}
		logger.debug("End key %s", key);
	}

	/**
	 * A server thread for function of join.
	 *
	 */
	protected class JoinServerThread extends Thread {

		private boolean result;

		private boolean enlistFinished = false;

		private Object identifier;

		private Object parentLock;

		/**
		 * Constructs a new instance.
		 *
		 * @param identifier an identifier for join
		 * @param parentLock lock object
		 */
		public JoinServerThread(Object identifier, Object parentLock) {
			this.identifier = identifier;
			this.parentLock = parentLock;
		}

		/**
		 * Executes {@link Helper#joinEnlist(Object)} and waits after that.
		 * This thread is not finished until it is notified from outside.
		 */
		@Override
		public void run() {
			logger.debug("Start identifier %s", identifier);
			result = helper.joinEnlist(identifier);
			enlistFinished = true;
			logger.debug("finish joinEnlist");

			synchronized (parentLock) {
				parentLock.notify();
			}
			synchronized (this) {
				logger.debug("waiting...");
				try {
					wait();
				} catch (InterruptedException e) {
					// ignore
				}
				logger.debug("Wakes up");
			}
			logger.debug("End identifier %s", identifier);
		}

		/**
		 * Returns the result of {@link Helper#joinEnlist(Object)}.
		 * @return the result of {@link Helper#joinEnlist(Object)}
		 */
		public boolean isResult() {
			return result;
		}

		/**
		 * Returns the enlist finished flag.
		 * @return the enlist finished flag
		 */
		public boolean isEnlistFinished() {
			return enlistFinished;
		}

	}
}
