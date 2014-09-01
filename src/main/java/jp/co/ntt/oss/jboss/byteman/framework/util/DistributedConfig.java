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

package jp.co.ntt.oss.jboss.byteman.framework.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A singleton object which provides configuration values.
 *
 * configuration file name is 'byteman-framework.properties'.
 *
 * The following properties can be set for controller node.
 * <table border="1">
 * <tr><th>property</th><th>required</th><th>default value</th><th>description</th></tr>
 * <tr><td>controller.rmi.address</td><td>true</td><td>&nbsp;</td><td>The host name or IP address of a RMI server.</td></tr>
 * <tr><td>controller.rmi.port</td><td>false</td><td>1099</td><td>The port of a RMI server.</td></tr>
 * <tr><td>controller.result.dir</td><td>false</td><td>&nbsp;</td><td>The directory path for result files.</td></tr>
 * <tr><td>deployment.destination</td><td>false</td><td>&nbsp;</td><td>The directory path of the deployment destination.</td></tr>
 * </table>
 * <br/>
 * The following properties can be set for remote node. A remote node property can configure with an identifier for each node.
 * At least one identifier needs to be set. If '*' is used for the identifier, then the property is applied to all the nodes.
 * These properties with 'node.' prefix are used for each node:
 * <table border="1">
 * <tr><th>property</th><th>required</th><th>default value</th><th>description</th></tr>
 * <tr><td>node.address.$identifier</td><td>true</td><td>&nbsp;</td><td>The host name or IP address of the node.</td></tr>
 * <tr><td>node.byteman.port.$identifier</td><td>false</td><td>9091</td><td>The port of a byteman agent.</td></tr>
 * <tr><td>node.byteman.jar.$identifier</td><td>false</td><td>&nbsp;</td><td>A byteman.jar file path on the node.</td></tr>
 * <tr><td>node.bytemanframework.jar.$identifier</td><td>false</td><td>&nbsp;</td><td>The jar file path of this Byteman framework.</td></tr>
 * <tr><td>node.ssh.username.$identifier</td><td>false</td><td>&nbsp;</td><td>The username for SSH login.</td></tr>
 * <tr><td>node.ssh.password.$identifier</td><td>false</td><td>&nbsp;</td><td>The password for SSH login.</td></tr>
 * </table>
 */
public class DistributedConfig {
	/** The property key for the controller RMI address. */
	public static final String CONTROLLER_RMI_ADDRESS = "controller.rmi.address";
	/** The property key for the controller RMI port. */
	public static final String CONTROLLER_RMI_PORT = "controller.rmi.port";
	/** The property key for the directory path for results on the controller. */
	public static final String CONTROLLER_RESULT_DIR = "controller.result.dir";
	/** The property key for the directory path of the deployment destination. */
	public static final String DEPLOY_DESTINATION = "deployment.destination";
	/** The property key for node addresses. */
	public static final String NODE_ADDRESS = "node.address";
	/** The property key for Byteman ports on nodes. */
	public static final String NODE_BYTEMAN_PORT = "node.byteman.port";
	/** The property key for byteman.jar path. */
	public static final String NODE_BYTEMAN_JAR = "node.byteman.jar";
	/** The property key for node.bytemanframework.jar path. */
	public static final String NODE_BYTEMANFW_JAR = "node.bytemanframework.jar";
	/** The property key for node.ssh.username. */
	public static final String NODE_SSH_USERNAME = "node.ssh.username";
	/** The property key for node.ssh.password. */
	public static final String NODE_SSH_PASSWORD = "node.ssh.password";

	private static final String FILE_NAME = "byteman-framework.properties";

	private static Map<String, String> defaultNodeValues = new HashMap<String, String>();

	private static DistributedConfig config;

	private String rmiAddress;
	private int rmiPort;
	private String resultDir;
	private String deploymentDestination;

	private Map<String, DistributedNodeConfig> nodeConfigs;

	static {
		defaultNodeValues.put(CONTROLLER_RMI_PORT, "1099");
		defaultNodeValues.put(NODE_BYTEMAN_PORT, "9091");
	}

	private DistributedConfig() {
		init(FILE_NAME);
	}

	/**
	 * Initializes this object.
	 *
	 * @param propName the name of property file
	 */
	protected void init(String propName) {
		Properties properties = getProperties(propName);
		rmiAddress = properties.getProperty(CONTROLLER_RMI_ADDRESS);
		if(rmiAddress == null) {
			throw new IllegalStateException(String.format("%s is not defined.", CONTROLLER_RMI_ADDRESS));
		}
		rmiPort = Integer.parseInt(properties.getProperty(CONTROLLER_RMI_PORT, defaultNodeValues.get(CONTROLLER_RMI_PORT)));
		resultDir = properties.getProperty(CONTROLLER_RESULT_DIR);
		deploymentDestination = properties.getProperty(DEPLOY_DESTINATION);
		nodeConfigs = new HashMap<String, DistributedConfig.DistributedNodeConfig>();
		// extracts node properties.
		for(String key : properties.stringPropertyNames()) {
			if(key.startsWith("node.")) {
				String identifier = getIdentifier(key);
				DistributedNodeConfig nodeConfig = nodeConfigs.get(identifier);
				if(nodeConfig == null) {
					// node.address is required.
					if(!identifier.equals("*") && !properties.containsKey(NODE_ADDRESS + "." + identifier)) {
						throw new IllegalStateException(String.format("%s is not defined.", NODE_ADDRESS + "." + identifier));
					}
					nodeConfig = new DistributedNodeConfig();
					nodeConfigs.put(identifier, nodeConfig);
				}
				String configKey = key.substring(0, key.length() - identifier.length() - 1);
				nodeConfig.put(configKey, properties.getProperty(key));
			}
		}
		if(nodeConfigs.size() == 0 || (nodeConfigs.size() == 1 && nodeConfigs.containsKey("*"))) {
			throw new IllegalStateException("The setup of the node is not defined at all.");
		}
	}

	/**
	 * Returns a singleton instance of {@link DistributedConfig}.
	 *
	 * @return a singleton instance of {@link DistributedConfig}.
	 */
	public static synchronized DistributedConfig getConfig() {
		if(config == null) {
			config = new DistributedConfig();
		}
		return config;
	}

	/**
	 * Returns the address of RMI.
	 *
	 * @return the address of RMI
	 */
	public String getRmiHost() {
		return rmiAddress;
	}

	/**
	 * Returns the port of RMI
	 *
	 * @return the port of RMI
	 */
	public int getRmiPort() {
		return rmiPort;
	}

	/**
	 * Returns the base result directory.
	 *
	 * @return the base result directory
	 */
	public String getResultDir() {
		return resultDir;
	}
	
	/**
	 * Returns the deployment destination.
	 * 
	 * @return the deployment destination
	 */
	public String getDeploymentDestination(){
		return deploymentDestination;
	}

	/**
	 * Returns the map object of {@link DistributedNodeConfig}.
	 *
	 * @return the map object of {@link DistributedNodeConfig}
	 */
	public Map<String, DistributedNodeConfig> getNodeConfigs() {
		return nodeConfigs;
	}

	/**
	 * Returns the {@link DistributedNodeConfig} by specified identifier.
	 *
	 * @param identifier the identifier
	 * @return the {@link DistributedNodeConfig}
	 */
	public DistributedNodeConfig getNodeConfig(String identifier) {
		return getNodeConfigs().get(identifier);
	}

	/**
	 * Extracts the identifier from the given property key.
	 */
	private String getIdentifier(String key) {
		int index = key.lastIndexOf('.');
		return key.substring(index + 1);
	}

	private Properties getProperties(String propName) {
		InputStream in = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			in = cl.getResourceAsStream(propName);
			if(in != null){
				Properties properties = new Properties();
				properties.load(in);
				return properties;
			} else {
				throw new FileNotFoundException(String.format("%s is not found.", propName));
			}
		} catch(Exception ex){
			throw new RuntimeException(ex);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}

	/**
	 * The configuration class of node.
	 *
	 */
	public static class DistributedNodeConfig {

		private Map<String, String> configs = new HashMap<String, String>();

		private void put(String key, String value) {
			configs.put(key, value);
		}

		private boolean contains(String key) {
			return configs.containsKey(key);
		}

		/**
		 * Returns a property value of node.
		 *  Returns a default value if specified property value is not set but the default value is.
		 *
		 * @param key the identifier of property
		 * @return a property value of node.
		 */
		public String get(String key) {
			if(configs.containsKey(key)) {
				return configs.get(key);
			} else if(DistributedConfig.getConfig().getNodeConfig("*") != null
					&& DistributedConfig.getConfig().getNodeConfig("*").contains(key)) {
				return DistributedConfig.getConfig().getNodeConfig("*").get(key);
			} else {
				return defaultNodeValues.get(key);
			}
		}

	}

}
