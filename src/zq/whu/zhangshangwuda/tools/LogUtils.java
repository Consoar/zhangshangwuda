/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zq.whu.zhangshangwuda.tools;

import zq.whu.zhangshangwuda.ui.BuildConfig;
import android.util.Log;

/**
 * Helper methods that make logging more consistent throughout the app.
 */
public class LogUtils {
	private static final String LOG_PREFIX = "ZSWD_";
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_TAG_LENGTH = 23;
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static String makeLogTag(String str) {
		if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
			return LOG_PREFIX
					+ str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH
							- 1);
		}

		return LOG_PREFIX + str;
	}

	/**
	 * WARNING: Don't use this when obfuscating class names with Proguard!
	 */
	public static String makeLogTag(Class cls) {
		return makeLogTag(cls.getSimpleName());
	}

	public static void D(final String tag, String message) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}

	public static void D(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.d(tag, message, cause);
		}
	}

	public static void V(final String tag, String message) {
		// noinspection PointlessBooleanExpression,ConstantConditions
		if (DEBUG) {
			Log.v(tag, message);
		}
	}

	public static void V(final String tag, String message, Throwable cause) {
		// noinspection PointlessBooleanExpression,ConstantConditions
		if (DEBUG) {
			Log.v(tag, message, cause);
		}
	}

	public static void I(final String tag, String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}

	public static void I(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.i(tag, message, cause);
		}
	}

	public static void W(final String tag, String message) {
		if (DEBUG) {
			Log.w(tag, message);
		}
	}

	public static void W(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.w(tag, message, cause);
		}
	}

	public static void E(final String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}

	public static void E(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.e(tag, message, cause);
		}
	}

	private LogUtils() {
	}
}
