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

import java.util.Iterator;
import java.util.List;

/**
 * The interface for collecting and getting result data.
 *
 * @param <T> the type of the fragments
 */
public interface ResultCollector<T> {

	/**
	 * Collects result data.
	 *
	 * @throws Exception
	 */
	public void collect() throws Exception;

	/**
	 * Returns collected data by {@link #collect()}.
	 *
	 * @return collected data
	 */
	public List<Iterator<T>> getResult();

}