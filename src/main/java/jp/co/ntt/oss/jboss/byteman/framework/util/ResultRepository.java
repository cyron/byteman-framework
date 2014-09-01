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

/**
 * The repository class for collecting result data.
 *
 */
public class ResultRepository {

	private String className;

	private String methodName;

	/**
	 * Constructs a new instance with specified class name and method name.
	 *
	 * @param className the class name
	 * @param methodName the method name
	 */
	public ResultRepository(String className, String methodName) {
		if(className == null || className.trim().length() == 0) {
			throw new IllegalArgumentException("The class name is empty.");
		}
		if(methodName == null || methodName.trim().length() == 0) {
			throw new IllegalArgumentException("The method name is empty.");
		}
		this.className = className;
		this.methodName = methodName;
	}

	/**
	 * Returns the path of result directory.
	 * The result directory is combined class name and method name with result directory of configuration.
	 *
	 * @return the path of result directory
	 */
	public String getPath() {
		validateResultBaseDir();
		return DistributedConfig.getConfig().getResultDir() + File.separator + className + File.separator + methodName;
	}

	/**
	 * Clears the collected files at {@link #getPath()}.
	 */
	public void clear() {
		deleteDirectory(new File(getPath()));
	}

	/**
	 * Deletes the directory including files.
	 *
	 * @param directory the target directory
	 */
	protected void deleteDirectory(File directory) {
		if(directory.exists()) {
			for(File file : directory.listFiles()) {
				if(file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
			directory.delete();
		}
	}

	/**
	 * Validates whether result directory is configured and available.
	 *
	 */
	protected void validateResultBaseDir() {
		String resultDir = DistributedConfig.getConfig().getResultDir();
		if(resultDir == null) {
			throw new IllegalStateException("controller.result.dir is not defined.");
		} else if(!new File(resultDir).exists()) {
			throw new IllegalStateException(String.format("%s does not exist.", resultDir));
		}
	}

}