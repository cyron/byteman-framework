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

package jp.co.ntt.oss.jboss.byteman.framework.instrumentor;

import java.io.InputStream;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;

/**
 * The interface of the instrumentor for test controller.
 * The instrumentor provides a implementation of the {@link DistributedAdapter}
 * and a function which installs a rule script.
 *
 */
public interface DistributedInstrumentor {

	/**
	 * Initializes a instrumentor.
	 *
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * Installs a rule script to remote node.
	 *
	 * @param identifier an identifier of remote node
	 * @param filePath a script file path
	 * @throws Exception
	 */
	public void installScript(Object identifier, String filePath) throws Exception;

	/**
	 * Installs a rule script to remote node.
	 *
	 * @param identifier an identifier of remote node
	 * @param scriptName a script name
	 * @param resourceStream an input stream of a script file
	 * @throws Exception
	 */
	public void installScript(Object identifier, String scriptName, InputStream resourceStream) throws Exception;

	/**
	 * Installs a rule script to remote node.
	 *
	 * @param identifier an identifier of remote node
	 * @param scriptName a script name
	 * @param scriptText a script text
	 * @throws Exception
	 */
	public void installScript(Object identifier, String scriptName, String scriptText) throws Exception;

	/**
	 * Destroys a instrumentor. Instrumentor cannot be used after executing this method.
	 *
	 * @throws Exception
	 */
	public void destroy() throws Exception ;

	/**
	 * Returns a {@link DistributedAdapter}.
	 *
	 * @return a {@link DistributedAdapter}
	 */
	public DistributedAdapter getAdapter();

}
