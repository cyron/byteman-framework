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

package jp.co.ntt.oss.jboss.byteman.framework.assertion;

import java.io.File;

import jp.co.ntt.oss.jboss.byteman.framework.util.ResultRepository;

/**
 * The base class for {@link ResultCollector} implementations.
 *
 * @param <T> the type of the fragments
 */
public abstract class AbstractResultCollector<T> implements ResultCollector<T> {
	protected ResultRepository resultRepository;

	/**
	 * Constructs a new instance.
	 * @param resultRepository the repository for this collector class
	 */
	protected AbstractResultCollector(ResultRepository resultRepository) {
		this.resultRepository = resultRepository;
	}

	/**
	 * Returns the destination path.
	 * The destination path is combined the identifier with {@link ResultRepository#getPath()}.
	 *
	 * @param identifier the identifier
	 * @return the destination path
	 */
	protected String destPath(String identifier) {
		String path = resultRepository.getPath() + File.separator + identifier;
		// Checks whether the path is available.
		// If the path does not exists, creates a directory of the path.
		File file = new File(path);
		if (!file.exists() && !file.mkdirs()) {
			throw new IllegalStateException(String.format("Failed to create the directory %s.", path));
		}
		return path;
	}

}