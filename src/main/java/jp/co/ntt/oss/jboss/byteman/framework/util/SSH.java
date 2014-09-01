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

import java.io.File;
import java.security.PublicKey;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 * The utility class for SSH and SCP.
 * In the normal way, use {@link ServerCommandManager} instead of this class.
 */
public class SSH {

	/** Logger. **/
	protected Logger logger = Logger.getLogger();

	/**
	 * Creates the new instance of net.schmizz.sshj.SSHClient.
	 * 
	 * @return the new instance of SSHClient
	 */
	protected SSHClient newSSHClient(){
		SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new HostKeyVerifier() {
			@Override
			public boolean verify(String arg0, int arg1, PublicKey arg2) {
				// skip host key verification
				return true;
			}
		});
		return client;
	}
	
	/**
	 * Executes a command with its arguments on the remote server via SSH.
	 * 
	 * @param address the hostname or IP address of the remote server.
	 * @param username the username of the remote server.
	 * @param password the password of the remote server.
	 * @param commands a string array containing a command and its arguments
	 * @return the return value of the command
	 * @throws Exception
	 */
	public int execute(String address, String username, String password, String... commands) throws Exception {
		SSHClient client = newSSHClient();
		client.connect(address);
		logger.debug("Connected to hostname %s", address);
		try {
			client.authPassword(username, password.toCharArray());
			logger.debug("Successful authentication of user %s", username);

			Session session = client.startSession();
			logger.debug("Start the session");
			try {
				StringBuilder sb = new StringBuilder();
				for(String command: commands){
					sb.append(command).append("\n");
				}
				logger.debug("Command: %s", sb.toString());
				Command command = session.exec(sb.toString());
				command.join();
				command.close();
				int status = command.getExitStatus();
				logger.debug("Returns the exit status %d", status);

				return status;

			} finally {
				session.close();
				logger.debug("Closed the session");
			}
		} finally {
			client.disconnect();
			logger.debug("Disconnected to hostname %s", address);
		}
	}
	
	/**
	 * Uploads a local file (or directory) to the remote server via SCP.
	 *
	 * @param address the hostname or IP address of the remote server.
	 * @param username the username of the remote server.
	 * @param password the password of the remote server.
	 * @param localPath a local file or directory which will be uploaded.
	 * @param remotePath the destination path on the remote server.
	 * @throws Exception
	 */
	public void upload(String address, String username, String password, String localPath, String remotePath) throws Exception {
		SSHClient client = newSSHClient();
		client.connect(address);
		logger.debug("Connected to hostname %s", address);
		try {
			client.authPassword(username, password.toCharArray());
			logger.debug("Successful authentication of user %s", username);

			client.newSCPFileTransfer().upload(new FileSystemFile(new File(localPath)), remotePath);
			logger.debug("Successful upload from %s to %s", localPath, remotePath);
		} finally {
			client.disconnect();
			logger.debug("Disconnected to hostname %s", address);
		}
	}

	/**
	 * Downloads a remote file (or directory) to the local directory via SCP.
	 * 
	 * @param address the hostname or IP address of the remote server.
	 * @param username the username of the remote server.
	 * @param password the password of the remote server.
	 * @param remotePath a remote file or directory which will be downloaded.
	 * @param localPath the destination directory on the local file system.
	 * @throws Exception
	 */
	public void download(String address, String username, String password, String remotePath, String localPath) throws Exception {
		SSHClient client = newSSHClient();
		client.connect(address);
		logger.debug("Connected to hostname %s", address);
		try {
			client.authPassword(username, password.toCharArray());
			logger.debug("Successful authentication of user %s", username);

			client.newSCPFileTransfer().download(remotePath, new FileSystemFile(new File(localPath)));
			logger.debug("Successful download from %s to %s", remotePath, localPath);
		} finally {
			client.disconnect();
			logger.debug("Disconnected to hostname %s", address);
		}
	}

}
