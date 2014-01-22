package net.homeip.tedk.maricoparestaurantratings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

public class Version {

	private static volatile boolean hadInit = false;

	private static volatile int VersionCode = -1;
	private static volatile String VersionName = null;
	private static volatile Date InstallDate = null;

	private static volatile DateFormat InstallDateFormat = new SimpleDateFormat(
			"yyyyMMdd", Locale.US);

	public static synchronized void Init(Context c) {
		if (hadInit)
			return;
		hadInit = true;
		PackageInfo pi = null;
		try {
			pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
		} catch (Exception e) {
			Log.e("Version", "Couldn't get own package information");
			throw new IllegalStateException(
					"Couldn't get own package information");
		}
		VersionCode = pi.versionCode;
		VersionName = pi.versionName;
		InstallDate = new Date(pi.lastUpdateTime);
	}

	private static synchronized void checkInit() {
		if (!hadInit) {
			Log.e("Version", "must call Init first");
			throw new IllegalStateException("must call Init first");
		}
	}

	public static int getVersionCode() {
		checkInit();
		return VersionCode;
	}

	public static String getVersionName() {
		checkInit();
		return VersionName;
	}

	public static Date getInstallDate() {
		checkInit();
		return InstallDate;
	}

	public static String getInstallDateString() {
		return InstallDateFormat.format(getInstallDate());
	}

}
