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

/**
 * The manager class for executing a command on the remote server via SSH.
 * <p>
 * Following properties are required to use this class.
 * <ul>
 * <li>node.ssh.username</li>
 * <li>node.ssh.password</li>
 * </ul>
 * You can define connection settings in the byteman-framework.properties.
 * If you want to connect other servers, use {@link SSH} directory.
 */
public class ServerCommandManager {
	
	protected SSH ssh = new SSH();
	
	/**
	 * Executes a command with its arguments on the remote server via SSH.
	 * 
	 * @param identifier the identifier of the remote server.
	 * @param commands a string array containing a command and its arguments
	 * @return the return value of the command
	 * @throws Exception
	 */
	public int execute(String identifier, String... commands) throws Exception {
		DistributedConfig config = DistributedConfig.getConfig();
		String address  = config.getNodeConfig(identifier).get(DistributedConfig.NODE_ADDRESS);
		String username = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_USERNAME);
		String password = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_PASSWORD);
		
		return ssh.execute(address, username, password, commands);
	}
	
	/**
	 * Uploads a local file (or directory) to the remote server via SCP.
	 *
	 * @param identifier the identifier of the remote server.
	 * @param localPath a local file or directory which will be uploaded.
	 * @param remotePath the destination path on the remote server.
	 * @throws Exception
	 */
	public void upload(String identifier, String localPath, String remotePath) throws Exception {
		DistributedConfig config = DistributedConfig.getConfig();
		String address  = config.getNodeConfig(identifier).get(DistributedConfig.NODE_ADDRESS);
		String username = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_USERNAME);
		String password = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_PASSWORD);
		
		ssh.upload(address, username, password, localPath, remotePath);
	}
	
	/**
	 * Downloads a remote file (or directory) to the local directory via SCP.
	 * 
	 * @param identifier the identifier of the remote server.
	 * @param remotePath a remote file or directory which will be downloaded.
	 * @param localPath the destination directory on the local file system.
	 * @throws Exception
	 */
	public void download(String identifier, String remotePath, String localPath) throws Exception {
		DistributedConfig config = DistributedConfig.getConfig();
		String address  = config.getNodeConfig(identifier).get(DistributedConfig.NODE_ADDRESS);
		String username = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_USERNAME);
		String password = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_PASSWORD);
		
		ssh.download(address, username, password, remotePath, localPath);
	}
	
	/**
	 * Deletes a remote file (or directory) via SSH.
	 * 
	 * @param identifier the identifier of the remote server.
	 * @param remotePath a remote file or directory which will be deleted.
	 * @throws Exception
	 */
	public void delete(String identifier, String remotePath) throws Exception {
		DistributedConfig config = DistributedConfig.getConfig();
		String address  = config.getNodeConfig(identifier).get(DistributedConfig.NODE_ADDRESS);
		String username = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_USERNAME);
		String password = config.getNodeConfig(identifier).get(DistributedConfig.NODE_SSH_PASSWORD);
		
		ssh.execute(address, username, password, "rm -rf " + remotePath);
	}
}
