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
import java.util.Map;

import jp.co.ntt.oss.jboss.byteman.framework.util.DistributedConfig.DistributedNodeConfig;

/**
 * The utility class for deploying resources to remote servers from the controller node.
 * <p>
 * Following properties are required to use this class.
 * <ul>
 * <li>node.ssh.username</li>
 * <li>node.ssh.password</li>
 * <li>deployment.destination</li>
 * </ul>
 */
public class Deployments {
	
	protected ServerCommandManager commandManager = new ServerCommandManager();

	/**
	 * Returns the deployment destination.
	 * 
	 * @return the deployment destination
	 */
	protected String getDeploymentDestination(){
		return DistributedConfig.getConfig().getDeploymentDestination();
	}
	
	/**
	 * Copies ./deployments/* to of all servers which are defined in byteman-framework.properties.
	 * The destination directory is specified as the deployment.destination property in byteman-framework.properties.
	 * 
	 * If the property deployment.destination is not defined (or empty), this method do nothing.
	 * And also it's the same if ./deployments directory does not exist.
	 */
	public void deploy() throws Exception {
		String destination = getDeploymentDestination();
		
		if(destination == null || destination.trim().isEmpty()){
			return;
		}
		File sourceDir = new File("deployments");
		if(sourceDir.exists() && sourceDir.isDirectory()){
			Map<String, DistributedNodeConfig> configs = DistributedConfig.getConfig().getNodeConfigs();
			for(String identifier: configs.keySet()){
				if(!identifier.equals("*")){
					for(File file: sourceDir.listFiles()){
						commandManager.upload(identifier, file.getAbsolutePath(), destination);
					}
				}
			}
		}
	}
	
}
