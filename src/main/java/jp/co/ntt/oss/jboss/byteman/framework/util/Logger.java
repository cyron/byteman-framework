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

import java.util.Date;

/**
 * A singleton object which log a message.
 *
 * This Logger publishes log records to System.out.
 *
 * At startup the Logger class is located using the 'byteman.framework.debug' system property.
 * By default, the log message is not output. If you log a debug message,
 * will specify the following:
 * <pre>
 * java -Dbyteman.framework.debug=true ...
 * </pre>
 */
public class Logger {
	public static final String SYSTEM_PROPERTY = "byteman.framework.debug";
	private static final boolean ENABLED = Boolean.getBoolean(SYSTEM_PROPERTY);

	private static final String DEBUG_FORMAT = "%1$tF %1$tT,%1$tL %2$s.%3$s(%4$s:%5$d) %6$s";
	private static final String ERROR_FORMAT = "%1$tF %1$tT,%1$tL ERROR %2$s";
	private static final Logger INSTANCE = new Logger();

	private Logger() {}

	/**
	 * Returns the logger.
	 *
	 * @return a singleton instance of {@link Logger}
	 */
	public static Logger getLogger() {
		return INSTANCE;
	}

	/**
	 * Log a message with debug log level.
	 *
	 * @param message a message format string
	 * @param args replacement strings
	 */
	public void debug(String message, Object... args) {
		if(ENABLED) {
			StackTraceElement source = Thread.currentThread().getStackTrace()[2];
			System.out.println(String.format(
					DEBUG_FORMAT,
					new Date(),
					source.getClassName(),
					source.getMethodName(),
					source.getFileName(),
					source.getLineNumber(),
					String.format(message, args)));
		}
	}

	/**
	 * Log an error with error log level.
	 *
	 * @param e this cause
	 * @param message a message format string
	 * @param args replacement strings
	 */
	public void error(Throwable e, String message, Object... args) {
		if(ENABLED) {
			System.out.println(String.format(
					ERROR_FORMAT,
					new Date(),
					String.format(message, args)));
			e.printStackTrace(System.out);
		}
	}

}
