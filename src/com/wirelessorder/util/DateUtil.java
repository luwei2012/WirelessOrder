package com.wirelessorder.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	public static String formatDateStamp(Long timestamp) {
		Date now = new Date();
		// Log.d("time stamp is ",""+timestamp);
		Date createAt = new Date(timestamp);
		Date yesterday = new Date(now.getTime() - (24 * 60 * 60 * 1000));
		SimpleDateFormat f1 = new SimpleDateFormat("?????? kk:mm", Locale.CHINA);
		SimpleDateFormat f2 = new SimpleDateFormat("?????? kk:mm", Locale.CHINA);
		SimpleDateFormat f3 = new SimpleDateFormat("MM-dd kk:mm",
				Locale.CHINA);

		int yearT = now.getYear();
		int yearY = yesterday.getYear();
		int yearA = createAt.getYear();

		int monthT = now.getMonth();
		int monthY = yesterday.getMonth();
		int monthA = createAt.getMonth();

		int dayT = now.getDay();
		int dayY = yesterday.getDay();
		int dayA = createAt.getDay();

		if (yearT == yearA && monthT == monthA && dayT == dayA) {
			return f1.format(createAt);
		} else if (yearY == yearA && monthY == monthA && dayY == dayA) {
			return f2.format(createAt);
		} else {
			return f3.format(createAt);
		}

	}
}
