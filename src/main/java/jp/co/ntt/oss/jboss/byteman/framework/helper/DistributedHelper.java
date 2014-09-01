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

package jp.co.ntt.oss.jboss.byteman.framework.helper;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackIF;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.CallbackInvoker;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlIF;
import jp.co.ntt.oss.jboss.byteman.framework.util.Logger;

import org.jboss.byteman.agent.Transformer;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.helper.Helper;

/**
 *  This helper class works at remote nodes on a distributed environment and provides methods to help testing.<br/> 
 *  As for the following methods, they wrap and extend {@link Helper} class's methods for the distributed environment. 
 *  These methods call corresponding {@link DistributedAdapter} class's methods on the controller node.
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
 * 
 */
public class DistributedHelper extends Helper {
	/** The system property key for the local hostname. */
	public static final String PROP_HOSTNAME = Transformer.BYTEMAN_PACKAGE_PREFIX + "jp.co.ntt.oss.jboss.byteman.framework.host";
	/** The system property key for the RMI port. */
	public static final String PROP_PORT = Transformer.BYTEMAN_PACKAGE_PREFIX + "jp.co.ntt.oss.jboss.byteman.framework.port";
	/** Logger. **/
	protected Logger logger = Logger.getLogger();
	/** For accessing to the controller. */
	protected DistributedAdapter adapter;

	/**
	 * Constructs a new instance with a {@link Rule}.
	 *
	 * @param rule a {@link Rule}
	 */
	protected DistributedHelper(Rule rule) {
		super(rule);
		initAdapter();
	}

	/**
	 * Sets up a instance of {@link DistributedAdapter}.
	 * The instance of {@link ControlIF} is acquired from <code>LocateRegistry</code> of RMI.
	 */
	protected void initAdapter() {
		logger.debug("Start rule %s", rule.getName());
		String hostname = System.getProperty(PROP_HOSTNAME, "localhost");
		String portValue = System.getProperty(PROP_PORT, "1099");
		int port = Integer.parseInt(portValue);
		try {
			Registry registry = LocateRegistry.getRegistry(hostname, port);
			adapter = (ControlIF) registry.lookup(ControlIF.CONTROL_ID);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : initAdapter ", rule.getName()), e);
		}
		logger.debug("End rule %s", rule.getName());
	}

	/**
	 * Provides {@link Helper#waiting(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the waiting
	 * @return the result of {@link DistributedAdapter#waiting(Object)} execution
	 * @see DistributedAdapter#waiting(Object)
	 */
	@Override
	public boolean waiting(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.waiting(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : waiting ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#waitFor(Object, long)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the waiting
	 * @param millisecs waiting time (milliseconds)
	 * @see DistributedAdapter#waitFor(Object, long)
	 */
	@Override
	public void waitFor(Object identifier, long millisecs) {
		logger.debug("Start rule %s", rule.getName());
		try {
			adapter.waitFor(identifier, millisecs);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : waitFor ", rule.getName()), e);
		}
		logger.debug("End rule %s", rule.getName());
	}

	/**
	 * Provides {@link Helper#signalWake(Object, boolean)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the waiting
	 * @param mustMeet the flag which forces waiting of the signal of waiting
	 * @return the result of {@link DistributedAdapter#signalWake(Object, boolean)} execution
	 * @see DistributedAdapter#signalWake(Object, boolean)
	 */
	@Override
	public boolean signalWake(Object identifier, boolean mustMeet) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.signalWake(identifier, mustMeet);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : signalWake ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#signalThrow(Object, boolean)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the waiting
	 * @param mustMeet the flag which forces waiting of the signal of waiting
	 * @return the result of {@link DistributedAdapter#signalThrow(Object, boolean)} execution
	 * @see DistributedAdapter#signalThrow(Object, boolean)
	 */
	@Override
	public boolean signalThrow(Object identifier, boolean mustMeet) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.signalThrow(identifier, mustMeet);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : signalThrow ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#createRendezvous(Object, int, boolean)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link DistributedAdapter#createRendezvous(Object, int, boolean)} execution
	 * @see DistributedAdapter#createRendezvous(Object, int, boolean)
	 */
	@Override
	public boolean createRendezvous(Object identifier, int expected, boolean restartable) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.createRendezvous(identifier, expected, restartable);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : createRendezvous ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#isRendezvous(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link DistributedAdapter#isRendezvous(Object, int)} execution
	 * @see DistributedAdapter#isRendezvous(Object, int)
	 */
	@Override
	public boolean isRendezvous(Object identifier, int expected) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.isRendezvous(identifier, expected);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : isRendezvous ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#getRendezvous(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link DistributedAdapter#getRendezvous(Object, int)} execution
	 * @see DistributedAdapter#getRendezvous(Object, int)
	 */
	@Override
	public int getRendezvous(Object identifier, int expected) {
		logger.debug("Start rule %s", rule.getName());
		int result;
		try {
			result = adapter.getRendezvous(identifier, expected);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : getRendezvous ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#rendezvous(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @return the result of {@link DistributedAdapter#rendezvous(Object)} execution
	 * @see DistributedAdapter#rendezvous(Object)
	 */
	@Override
	public int rendezvous(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		int result;
		try {
			result = adapter.rendezvous(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : rendezvous ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#deleteRendezvous(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link DistributedAdapter#deleteRendezvous(Object, int)} execution
	 * @see DistributedAdapter#deleteRendezvous(Object, int)
	 */
	@Override
	public boolean deleteRendezvous(Object identifier, int expected) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.deleteRendezvous(identifier, expected);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : deleteRendezvous ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#createJoin(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param max the max number of threads expected to meet at the join
	 * @return the result of {@link DistributedAdapter#createJoin(Object, int)} execution
	 * @see DistributedAdapter#createJoin(Object, int)
	 */
	@Override
	public boolean createJoin(Object identifier, int max) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.createJoin(identifier, max);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : createJoin ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#isJoin(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param max the max number of threads expected to meet at the join
	 * @return the result of {@link DistributedAdapter#isJoin(Object, int)} execution
	 * @see DistributedAdapter#isJoin(Object, int)
	 */
	@Override
	public boolean isJoin(Object identifier, int max) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.isJoin(identifier, max);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : isJoin ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	private static Map<Object, List<Thread>> joinThreadMap = new ConcurrentHashMap<Object, List<Thread>>();

	/**
	 * Provides {@link Helper#joinEnlist(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @return true if it succeeds in execution of {@link ControlIF#joinEnlistStart(Object)}, otherwise false.
	 * @see ControlIF#joinEnlistStart(Object)
	 * @see ControlIF#joinEnlistEnd(String)
	 */
	@Override
	public boolean joinEnlist(Object identifier) {
		logger.debug("Start rule %s", rule.getName());

		Thread current = Thread.currentThread();
		// checks whether it was called from the same thread.
		if(joinThreadMap.containsKey(identifier) && joinThreadMap.get(identifier).contains(current)) {
			logger.debug("End rule %s: Returns %b because of the same thread", rule.getName(), false);
			return false;
		}
		try {
			String key = ((ControlIF) adapter).joinEnlistStart(identifier);
			if(key == null) {
				logger.debug("End rule %s: Returns %b", rule.getName(), false);
				return false;
			}
			JoinClientThread thread = new JoinClientThread(identifier, key, current);
			thread.start();
			List<Thread> threads = joinThreadMap.get(identifier);
			if(threads == null) {
				threads = Collections.synchronizedList(new ArrayList<Thread>());
				joinThreadMap.put(identifier, threads);
			}
			threads.add(current);

			logger.debug("End rule %s: Returns %b", rule.getName(), true);
			return true;
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : joinEnlist ", rule.getName()), e);
		}
	}

	/**
	 * A client thread for function of join.
	 *
	 */
	private class JoinClientThread extends Thread {

		private Object identifier;
		private String serverThreadKey;
		private Thread parent;

		/**
		 * Constructs a new instance.
		 *
		 * @param identifier an identifier for join
		 * @param serverThreadKey the key of server thread
		 * @param parent a parent thread
		 */
		public JoinClientThread(Object identifier, String serverThreadKey, Thread parent) {
			this.identifier = identifier;
			this.serverThreadKey = serverThreadKey;
			this.parent = parent;
		}

		/**
		 * Executes {@link ControlIF#joinEnlistEnd(String)} after parent thread end.
		 */
		@Override
		public void run() {
			logger.debug("Start server key %s", serverThreadKey);
			try {
				parent.join();
				logger.debug("%s to die", parent);

				((ControlIF) adapter).joinEnlistEnd(serverThreadKey);
				List<Thread> threads = joinThreadMap.get(identifier);
				threads.remove(parent);
				if(threads.size() == 0) {
					joinThreadMap.remove(identifier);
				}
			} catch (Exception e) {
				// ignore
			}
			logger.debug("End server key %s", serverThreadKey);
		}
	}

	/**
	 * Provides {@link Helper#joinWait(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param count the number of threads expected to meet at the join
	 * @return the result of {@link DistributedAdapter#joinEnlist(Object)} execution
	 * @see DistributedAdapter#joinWait(Object, int)
	 */
	@Override
	public boolean joinWait(Object identifier, int count) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.joinWait(identifier, count);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : joinWait ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#flag(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link DistributedAdapter#flag(Object)} execution
	 * @see DistributedAdapter#flag(Object)
	 */
	@Override
	public boolean flag(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.flag(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : flag ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#flagged(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link DistributedAdapter#flagged(Object)} execution
	 * @see DistributedAdapter#flagged(Object)
	 */
	@Override
	public boolean flagged(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.flagged(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : flagged ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#clear(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link DistributedAdapter#clear(Object)} execution
	 * @see DistributedAdapter#clear(Object)
	 */
	@Override
	public boolean clear(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.clear(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : clear ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#isCountDown(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @return the result of {@link DistributedAdapter#isCountDown(Object)} execution
	 * @see DistributedAdapter#isCountDown(Object)
	 */
	@Override
	public boolean isCountDown(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.isCountDown(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : isCountDown ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#createCountDown(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @param count the number of times that the countdown needs to be counted down
	 * @return the result of {@link DistributedAdapter#createCountDown(Object, int)} execution
	 * @see DistributedAdapter#createCountDown(Object, int)
	 */
	@Override
	public boolean createCountDown(Object identifier, int count) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.createCountDown(identifier, count);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : createCountDown ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#countDown(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @return the result of {@link DistributedAdapter#countDown(Object)} execution
	 */
	@Override
	public boolean countDown(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.countDown(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : countDown ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#createCounter(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param value the initial value for the counter
	 * @return the result of {@link DistributedAdapter#createCounter(Object, int)} execution
	 * @see DistributedAdapter#createCounter(Object, int)
	 */
	@Override
	public boolean createCounter(Object identifier, int value) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.createCounter(identifier, value);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : createCounter ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#deleteCounter(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @return the result of {@link DistributedAdapter#deleteCounter(Object)} execution
	 * @see DistributedAdapter#deleteCounter(Object)
	 */
	@Override
	public boolean deleteCounter(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.deleteCounter(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : deleteCounter ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#readCounter(Object, boolean)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param zero the flag to reset counter
	 * @return the result of {@link DistributedAdapter#readCounter(Object, boolean)} execution
	 * @see DistributedAdapter#readCounter(Object, boolean)
	 */
	@Override
	public int readCounter(Object identifier, boolean zero) {
		logger.debug("Start rule %s", rule.getName());
		int result;
		try {
			result = adapter.readCounter(identifier, zero);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : readCounter ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#incrementCounter(Object, int)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param amount the amount to add to the counter
	 * @return the result of {@link DistributedAdapter#incrementCounter(Object, int)} execution
	 * @see DistributedAdapter#incrementCounter(Object, int)
	 */
	@Override
	public int incrementCounter(Object identifier, int amount) {
		logger.debug("Start rule %s", rule.getName());
		int result;
		try {
			result = adapter.incrementCounter(identifier, amount);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : incrementCounter ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#decrementCounter(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @return the result of {@link DistributedAdapter#decrementCounter(Object)} execution
	 * @see DistributedAdapter#decrementCounter(Object)
	 */
	@Override
	public int decrementCounter(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		int result;
		try {
			result = adapter.decrementCounter(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : decrementCounter ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#createTimer(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link DistributedAdapter#decrementCounter(Object)} execution
	 * @see DistributedAdapter#createTimer(Object)
	 */
	@Override
	public boolean createTimer(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.createTimer(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : createTimer ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#deleteTimer(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link DistributedAdapter#deleteTimer(Object)} execution
	 * @see DistributedAdapter#deleteTimer(Object)
	 */
	@Override
	public boolean deleteTimer(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.deleteTimer(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : deleteTimer ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#getElapsedTimeFromTimer(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link DistributedAdapter#getElapsedTimeFromTimer(Object)} execution
	 * @see DistributedAdapter#getElapsedTimeFromTimer(Object)
	 */
	@Override
	public long getElapsedTimeFromTimer(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		long result;
		try {
			result = adapter.getElapsedTimeFromTimer(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : getElapsedTimeFromTimer ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#resetTimer(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link DistributedAdapter#resetTimer(Object)} execution
	 * @see DistributedAdapter#resetTimer(Object)
	 */
	@Override
	public long resetTimer(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		long result;
		try {
			result = adapter.resetTimer(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : resetTimer ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %d", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#traceOpen(Object, String)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param fileName the file name
	 * @return the result of {@link DistributedAdapter#traceOpen(Object, String)} execution
	 * @see DistributedAdapter#traceOpen(Object, String)
	 */
	@Override
	public boolean traceOpen(Object identifier, String fileName) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.traceOpen(identifier, fileName);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : traceOpen ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#traceClose(Object)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @return the result of {@link DistributedAdapter#traceClose(Object)} execution
	 * @see DistributedAdapter#traceClose(Object)
	 */
	@Override
	public boolean traceClose(Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.traceClose(identifier);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : traceClose ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#trace(Object, String)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param message the message to output
	 * @return the result of {@link DistributedAdapter#trace(Object, String)} execution
	 * @see DistributedAdapter#trace(Object, String)
	 */
	@Override
	public boolean trace(Object identifier, String message) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.trace(identifier, message);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : trace ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Provides {@link Helper#traceln(Object, String)} function for the distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param message the message to output
	 * @return the result of {@link DistributedAdapter#traceln(Object, String)} execution
	 * @see DistributedAdapter#traceln(Object, String)
	 */
	@Override
	public boolean traceln(Object identifier, String message) {
		logger.debug("Start rule %s", rule.getName());
		boolean result;
		try {
			result = adapter.traceln(identifier, message);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : traceln ", rule.getName()), e);
		}
		logger.debug("End rule %s: Returns %b", rule.getName(), result);
		return result;
	}

	/**
	 * Registers the callback object with the specified identifier.
	 * The specified target object will be invoked from a controller node by {@link CallbackInvoker}.
	 *
	 * @param identifier an identifier for the callback
	 * @param target the target object
	 * @param methodName the target method name
	 * @see DistributedAdapter#registerCallback(Object, Object)
	 * @see DistributedAdapter#doCallback(Object, Object...)
	 */
	public void registerCallback(Object identifier, Object target, String methodName) {
		logger.debug("Start rule %s", rule.getName());
		try {
			adapter.registerCallback(identifier, new CallbackInvoker(target, methodName));
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : registerCallback ", rule.getName()), e);
		}
		logger.debug("End rule %s", rule.getName());
	}

	/**
	 * Registers the callback object to kill JVM with the specified identifier. It will be invoked with a exit code from a controller node.
	 *
	 * @param identifier an identifier for the callback
	 * @see DistributedAdapter#registerCallback(Object, Object)
	 * @see DistributedAdapter#killRemoteJVM(Object, int)
	 */
	public void prepareKillJVM(final Object identifier) {
		logger.debug("Start rule %s", rule.getName());
		try {
			CallbackIF callback = new CallbackIF() {
				@Override
				public Object invoke(Object... parameters) throws RemoteException {
					logger.debug("Start Kill JVM: identifier %s", identifier);
					int exitCode = -1;
					if (parameters.length > 0) {
						exitCode = (Integer) parameters[0];
					}
					java.lang.Runtime.getRuntime().halt(exitCode);
					logger.debug("End Kill JVM: identifier %s", identifier);
					return null;
				}
			};
			UnicastRemoteObject.exportObject(callback, 0);
			adapter.registerCallback(identifier, callback);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : prepareKillJVM ", rule.getName()), e);
		}
		logger.debug("End rule %s", rule.getName());
	}

	/**
	 * Kills the specified JVM of on the remote node.
	 *
	 * @param identifier identifier an identifier for the callback
	 */
	public void killRemoteJVM(Object identifier) {
		killRemoteJVM(identifier, -1);
	}

	/**
	 * Kills the specified JVM on the remote node with exit code.
	 *
	 * @param identifier an identifier to the kill JVM
	 * @param exitCode the exit code
	 * @see DistributedAdapter#killRemoteJVM(Object, int)
	 */
	public void killRemoteJVM(Object identifier, int exitCode) {
		logger.debug("Start rule %s", rule.getName());
		try {
			adapter.killRemoteJVM(identifier, exitCode);
		} catch (Exception e) {
			logger.error(e, "rule %s", rule.getName());
			throw new ExecuteException(String.format("rule %s : killRemoteJVM ", rule.getName()), e);
		}
		logger.debug("End rule %s", rule.getName());
	}
}
