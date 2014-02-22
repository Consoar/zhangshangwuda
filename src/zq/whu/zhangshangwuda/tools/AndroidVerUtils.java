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

import android.os.Build;

/**
 * An assortment of UI helpers.
 */
public class AndroidVerUtils {

	// public static boolean isGoogleTV(Context context) {
	// return
	// context.getPackageManager().hasSystemFeature("com.google.android.tv");
	// }

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= 8;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= 9;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12;
	}

	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= 14;
	}

	// public static boolean hasJellyBean() {
	// return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	// }
}
