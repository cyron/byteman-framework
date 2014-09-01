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

package jp.co.ntt.oss.jboss.byteman.framework.controller;

import jp.co.ntt.oss.jboss.byteman.framework.helper.DistributedHelper;
import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig;
import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig.DistributedNodeConfig;
import jp.co.ntt.oss.jboss.byteman.framework.util.Logger;
import jp.co.ntt.oss.jboss.byteman.framework.util.ServerCommandManager;

/**
 * The base class of the {@link NodeController} implementations.
 * Supports commands execution with SSH and attaching Byteman to remote nodes.
 * You need to configure an identifier for remote node and specify the identifier
 *  as a constructor argument to use this class.
 *
 * @see DistributedConfig
 */
public abstract class AbstractNodeController implements NodeController {

	protected Logger logger = Logger.getLogger();
	protected String identifier;
	private DistributedNodeConfig config;
	private ServerCommandManager commandManager;
	private String options;

	/**
	 * Constructs a new instance with an identifier.
	 * The identifier must be same as the identifier in the configuration file.
	 *
	 * @param identifier the identifier
	 */
	protected AbstractNodeController(String identifier) {
		this.config = DistributedConfig.getConfig().getNodeConfig(identifier);
		if(this.config == null) {
			throw new IllegalArgumentException(String.format("%s is not defined.", identifier));
		}
		this.identifier = identifier;
		this.commandManager = new ServerCommandManager();
	}

	/**
	 * Executes a command with SSH.
	 *
	 * @param command a command
	 * @return a return value of command
	 * @see #execute(String...)
	 * @throws Exception
	 */
	protected int executeWithSSH(String command) throws Exception {
		return commandManager.execute(identifier, command);
	}

	/**
	 * Returns the configuration value by the specified key.
	 *
	 * @param key the key
	 * @return the configuration value
	 */
	protected String getNodeConfig(String key) {
		return config.get(key);
	}

	/**
	 * Returns the configuration value by the specified key. If the key does not exist,
	 * returns the default value.
	 * @param key the key
	 * @param defaultValue the defaultValue
	 * @return the configuration value
	 */
	protected String getNodeConfig(String key, String defaultValue) {
		return getNodeConfig(key) == null ? defaultValue : getNodeConfig(key);
	}

	/**
	 * Returns the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets options to give JavaVM.
	 *
	 * @param options options for the JavaVM
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * Gets options to give JavaVM.
	 * If the system property of debug log exists, add it to remote nodes.
	 *
	 * @return options for the JavaVM
	 */
	protected String getOptions() {
		return (options == null ? "" : options)
				+ (Boolean.getBoolean(Logger.SYSTEM_PROPERTY)
						? String.format(" -D%s=true", Logger.SYSTEM_PROPERTY) : "");
	}

	/**
	 * Returns the definition of properties for a Byteman agent.
	 * Following properties are required.
	 * <ul>
	 * <li>node.byteman.jar</li>
	 * <li>node.bytemanframework.jar</li>
	 * <li>node.byteman.port (It is required to change a default value [1099].)</li>
	 * </ul>
	 *
	 * @param scripts the list of the path of a rule script
	 * @return the definition of properties for a Byteman agent
	 */
	protected String getBytemanAgentProperties(String... scripts) {
		String bytemanJar = getNodeConfig(DistributedConfig.NODE_BYTEMAN_JAR);
		String bytemanFWJar = getNodeConfig(DistributedConfig.NODE_BYTEMANFW_JAR);
		if(bytemanJar == null) {
			throw new IllegalStateException(String.format("%s is not defined.", DistributedConfig.NODE_BYTEMAN_JAR));
		}
		if(bytemanFWJar == null) {
			throw new IllegalStateException(String.format("%s is not defined.", DistributedConfig.NODE_BYTEMANFW_JAR));
		}

		StringBuilder sb = new StringBuilder();
		sb.append("-javaagent:").append(bytemanJar)
		  .append("=sys:").append(bytemanFWJar)
		  .append(",address:").append(getNodeConfig(DistributedConfig.NODE_ADDRESS))
		  .append(",port:").append(getNodeConfig(DistributedConfig.NODE_BYTEMAN_PORT))
		  .append(",prop:").append(DistributedHelper.PROP_HOSTNAME).append("=").append(DistributedConfig.getConfig().getRmiHost())
		  .append(",prop:").append(DistributedHelper.PROP_PORT).append("=").append(DistributedConfig.getConfig().getRmiPort());
		if(scripts.length != 0) {
			for(String script : scripts) {
				sb.append(",script:").append(script);
			}
		}
		return sb.toString();
	}

}
