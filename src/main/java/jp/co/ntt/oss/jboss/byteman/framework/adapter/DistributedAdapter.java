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

import org.jboss.byteman.rule.helper.Helper;

/**
 * The interface to access to the controller for test control. <br/>
 * The implementation classes work on the controller node and provides methods to help testing.
 */
public interface DistributedAdapter {

	/**
	 * Provides {@link Helper#waiting(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for waiting
	 * @return the result of {@link Helper#waiting(Object)} execution
	 * @see Helper#waiting(Object)
	 * @throws Exception
	 */
	public boolean waiting(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#waitFor(Object, long)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for waiting
	 * @param millisecs waiting time (milliseconds)
	 * @see Helper#waitFor(Object, long)
	 * @throws Exception
	 */
	public void waitFor(Object identifier, long millisecs) throws Exception;

	/**
	 * Provides {@link Helper#signalWake(Object, boolean)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for waiting
	 * @param mustMeet a flag whether to force to wait for a wait signal 
	 * @return the result of {@link Helper#signalWake(Object, boolean)} execution
	 * @see Helper#signalWake(Object, boolean)
	 * @throws Exception
	 */
	public boolean signalWake(Object identifier, boolean mustMeet) throws Exception;

	/**
	 * Provides {@link Helper#signalThrow(Object, boolean)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for waiting
	 * @param mustMeet a flag whether to force to wait for a wait signal
	 * @return the result of {@link Helper#signalThrow(Object, boolean)} execution
	 * @see Helper#signalThrow(Object, boolean)
	 * @throws Exception
	 */
	public boolean signalThrow(Object identifier, boolean mustMeet) throws Exception;

	/**
	 * Provides {@link Helper#createRendezvous(Object, int, boolean)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @param restartable a flag whether restart after reaching the expected number of waiting.
	 * @return the result of {@link Helper#createRendezvous(Object, int, boolean)} execution
	 * @see Helper#createRendezvous(Object, int, boolean)
	 * @throws Exception
	 */
	public boolean createRendezvous(Object identifier, int expected, boolean restartable) throws Exception;

	/**
	 * Provides {@link Helper#isRendezvous(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link Helper#isRendezvous(Object, int)} execution
	 * @see Helper#isRendezvous(Object, int)
	 * @throws Exception
	 */
	public boolean isRendezvous(Object identifier, int expected) throws Exception;

	/**
	 * Provides {@link Helper#getRendezvous(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link Helper#getRendezvous(Object, int)} execution
	 * @see Helper#getRendezvous(Object, int)
	 * @throws Exception
	 */
	public int getRendezvous(Object identifier, int expected) throws Exception;

	/**
	 * Provides {@link Helper#rendezvous(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @return the result of {@link Helper#rendezvous(Object)}
	 * @see Helper#rendezvous(Object)
	 * @throws Exception
	 */
	public int rendezvous(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#deleteRendezvous(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the rendezvous
	 * @param expected the number of threads expected to meet at the rendezvous
	 * @return the result of {@link Helper#deleteRendezvous(Object, int)} execution
	 * @see Helper#deleteRendezvous(Object, int)
	 * @throws Exception
	 */
	public boolean deleteRendezvous(Object identifier, int expected) throws Exception;

	/**
	 * Provides {@link Helper#createJoin(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param max the max number of threads expected to meet at the join
	 * @return the result of {@link Helper#createJoin(Object, int)} execution
	 * @see Helper#createJoin(Object, int)
	 * @throws Exception
	 */
	public boolean createJoin(Object identifier, int max) throws Exception;

	/**
	 * Provides {@link Helper#isJoin(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param max the max number of threads expected to meet at the join
	 * @return the result of {@link Helper#isJoin(Object, int)} execution
	 * @see Helper#isJoin(Object, int)
	 * @throws Exception
	 */
	public boolean isJoin(Object identifier, int max) throws Exception;

	/**
	 * Provides {@link Helper#joinEnlist(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @return the result of {@link Helper#joinEnlist(Object)} execution
	 * @see Helper#joinEnlist(Object)
	 * @throws Exception
	 */
	public boolean joinEnlist(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#joinWait(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the join
	 * @param count the number of threads expected to meet at the join
	 * @return the result of {@link Helper#joinWait(Object, int)} execution
	 * @see Helper#joinWait(Object, int)
	 * @throws Exception
	 */
	public boolean joinWait(Object identifier, int count) throws Exception;

	/**
	 * Provides {@link Helper#flag(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link Helper#flag(Object)} execution
	 * @see Helper#flag(Object)
	 * @throws Exception
	 */
	public boolean flag(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#flagged(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link Helper#flagged(Object)} execution
	 * @see Helper#flagged(Object)
	 * @throws Exception
	 */
	public boolean flagged(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#clear(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the flag
	 * @return the result of {@link Helper#clear(Object)} execution
	 * @see Helper#clear(Object)
	 * @throws Exception
	 */
	public boolean clear(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#isCountDown(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @return the result of {@link Helper#isCountDown(Object)} execution
	 * @see Helper#isCountDown(Object)
	 * @throws Exception
	 */
	public boolean isCountDown(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#createCountDown(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @param count the number of times that the countdown needs to be counted down before the countdown operation returns true.
	 * @return the result of {@link Helper#createCountDown(Object, int)} execution
	 * @see Helper#createCountDown(Object, int)
	 * @throws Exception
	 */
	public boolean createCountDown(Object identifier, int count) throws Exception;

	/**
	 * Provides {@link Helper#countDown(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the countdown
	 * @return the result of {@link Helper#countDown(Object)} execution
	 * @see Helper#countDown(Object)
	 * @throws Exception
	 */
	public boolean countDown(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#createCounter(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param value the initial value for the counter
	 * @return the result of {@link Helper#createCounter(Object, int)} execution
	 * @see Helper#createCounter(Object, int)
	 * @throws Exception
	 */
	public boolean createCounter(Object identifier, int value) throws Exception;

	/**
	 * Provides {@link Helper#deleteCounter(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @return the result of {@link Helper#deleteCounter(Object)} execution
	 * @see Helper#deleteCounter(Object)
	 * @throws Exception
	 */
	public boolean deleteCounter(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#readCounter(Object, boolean)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param zero the flag to reset counter
	 * @return the result of {@link Helper#readCounter(Object, boolean)} execution
	 * @see Helper#readCounter(Object, boolean)
	 * @throws Exception
	 */
	public int readCounter(Object identifier, boolean zero) throws Exception;

	/**
	 * Provides {@link Helper#incrementCounter(Object, int)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @param amount the amount to add to the counter
	 * @return the result of {@link Helper#incrementCounter(Object, int)} execution
	 * @see Helper#incrementCounter(Object, int)
	 * @throws Exception
	 */
	public int incrementCounter(Object identifier, int amount) throws Exception;

	/**
	 * Provides {@link Helper#decrementCounter(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the counter
	 * @return the result of {@link Helper#decrementCounter(Object)} execution
	 * @see Helper#decrementCounter(Object)
	 * @throws Exception
	 */
	public int decrementCounter(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#createTimer(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link Helper#createTimer(Object)} execution
	 * @see Helper#createTimer(Object)
	 * @throws Exception
	 */
	public boolean createTimer(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#deleteTimer(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link Helper#deleteTimer(Object)} execution
	 * @see Helper#deleteTimer(Object)
	 * @throws Exception
	 */
	public boolean deleteTimer(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#getElapsedTimeFromTimer(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link Helper#getElapsedTimeFromTimer(Object)} execution
	 * @see Helper#getElapsedTimeFromTimer(Object)
	 * @throws Exception
	 */
	public long getElapsedTimeFromTimer(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#resetTimer(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the timer
	 * @return the result of {@link Helper#resetTimer(Object)} execution
	 * @see Helper#resetTimer(Object)
	 * @throws Exception
	 */
	public long resetTimer(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#traceOpen(Object, String)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param fileName the file name
	 * @return the result of {@link Helper#traceOpen(Object, String)} execution
	 * @see Helper#traceOpen(Object, String)
	 * @throws Exception
	 */
	public boolean traceOpen(Object identifier, String fileName) throws Exception;

	/**
	 * Provides {@link Helper#traceClose(Object)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @return the result of {@link Helper#traceClose(Object)} execution
	 * @see Helper#traceClose(Object)
	 * @throws Exception
	 */
	public boolean traceClose(Object identifier) throws Exception;

	/**
	 * Provides {@link Helper#trace(Object, String)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param message the message to output
	 * @return the result of {@link Helper#trace(Object, String)} execution
	 * @see Helper#trace(Object, String)
	 * @throws Exception
	 */
	public boolean trace(Object identifier, String message) throws Exception;

	/**
	 * Provides {@link Helper#traceln(Object, String)} function at the controller node in distributed environment.
	 *
	 * @param identifier an identifier for the trace
	 * @param message the message to output
	 * @return the result of {@link Helper#traceln(Object, String)} execution
	 * @see Helper#traceln(Object, String)
	 * @throws Exception
	 */
	public boolean traceln(Object identifier, String message) throws Exception;

	/**
	 * Kills the specified JVM of remote node.
	 *
	 * @param identifier an identifier for the JVM to kill
	 * @throws Exception
	 */
	public void killRemoteJVM(Object identifier) throws Exception;

	/**
	 * Kills the specified JVM of remote node with exit code.
	 *
	 * @param identifier an identifier for the JVM to kill
	 * @param exitCode the exit code
	 * @throws Exception
	 */
	public void killRemoteJVM(Object identifier, int exitCode) throws Exception;

	/**
	 * Registers the callback object.
	 * The callback object is called from a controller node by {@link #doCallback(Object, Object...)},and invokes on the remote node.
	 *
	 * @param identifier an identifier for the callback
	 * @param target the callback object
	 * @throws Exception
	 */
	public void registerCallback(Object identifier, Object target) throws Exception;

	/**
	 * Unregisters the callback object.
	 *
	 * @param identifier an identifier for the callback
	 * @throws Exception
	 */
	public void unregisterCallback(Object identifier) throws Exception;

	/**
	 * Executes the callback object.
	 *
	 * @param identifier an identifier for the callback
	 * @param parameters parameters to execute the method of the callback object
	 * @return the result of the callback object execution
	 * @throws Exception
	 */
	public Object doCallback(Object identifier, Object... parameters) throws Exception;

}
