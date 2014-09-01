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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapter;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.DistributedAdapterImpl;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlHost;
import jp.co.ntt.oss.jboss.byteman.framework.adapter.rmi.ControlIF;
import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig;
import jp.co.ntt.oss.jboss.byteman.framework.util.Logger;

/**
 * The default implementation of {@link DistributedInstrumentor}.
 *
 *
 */
public class DistributedInstrumentorImpl extends AbstractDistributedInstrumentor {

	/** Logger. **/
	protected Logger logger = Logger.getLogger();

	private Registry registry;

	private DistributedAdapter adapter;

	private ControlHost host;

	/**
	 * Constructs a new instance.
	 * Creates a RMI registry by the port specified by the configuration file.
	 *
	 * @throws RemoteException if the registry could not be exported
	 */
	public DistributedInstrumentorImpl() throws RemoteException {
		registry = LocateRegistry.createRegistry(DistributedConfig.getConfig().getRmiPort());
		logger.debug("Created a RMI registry on the port %d", DistributedConfig.getConfig().getRmiPort());
	}

	/**
	 * Initializes a test controller as a endpoint of RMI.
	 */
	@Override
	public void init() throws Exception {
		super.init();

		host = new ControlHost();
		Remote stub = UnicastRemoteObject.exportObject(host, 0);
		registry.rebind(ControlIF.CONTROL_ID, stub);
		logger.debug("Replaced the binding in the RMI registry");

		adapter = new DistributedAdapterImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DistributedAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Destroys a registry of RMI.
	 */
	@Override
	public void destroy() throws Exception {
		super.destroy();
		registry.unbind(ControlIF.CONTROL_ID);
		UnicastRemoteObject.unexportObject(registry , true);
		logger.debug("Removed the RMI registry");
	}

}
