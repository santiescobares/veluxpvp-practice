package club.veluxpvp.practice.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

	public static Date getDateInGMT3() {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT-3"));
		c.setTime(new Date());
		
		return c.getTime();
	}
	
	public static String getFormattedDuration(int time, boolean startingZero) {
		int hours;
		int minutes;
		int seconds;
		hours = (int) (time % 86400) / 3600;
		minutes = (int) ((time % 3600) / 60);
		seconds = (int) ((time % 3600) % 60);
		
		String duration = "";
		
		if(hours >= 1) {
			if(hours < 10) {
				duration = "0" + hours + ":";
			} else {
				duration = hours + ":";
			}
		}
		
		if(minutes < 10) {
			duration += (startingZero ? "0" : "") + minutes + ":";
		} else {
			duration += minutes + ":";
		}
		
		if(seconds < 10) {
			duration += "0" + seconds;
		} else {
			duration += seconds;
		}
		
		return duration;
	}
	
	public static String formatDuration(int time) {
		int days;
		int hours;
		int minutes;
		int seconds;
		days = (int) (time / 86400);
		hours = (int) (time % 86400) / 3600;
		minutes = (int) ((time % 3600) / 60);
		seconds = (int) ((time % 3600) % 60);
		
		String duration = "";
		
		if(days > 0) {
			duration += days + (days > 1 ? "days" : "day") + ", ";
		}
		
		if(hours > 0) {
			duration += hours + (hours > 1 ? "hours" : "hour") + ", ";
		}
		
		if(minutes > 0) {
			duration += minutes + (minutes > 1 ? "minutes" : "minute") + " and ";
		}
		
		if(seconds >= 0) {
			duration += seconds + (seconds > 1 ? "seconds" : "second");
		}
		
		return duration;
	}
	
	public static String getTimeBetweenDates(Date date1, Date date2) {
		long difference_In_Time = date1.getTime() - date2.getTime(); 
		int seconds = (int) (difference_In_Time / 1000) % 60; 
		int minutes = (int) (difference_In_Time / (1000 * 60)) % 60; 
		int hours = (int) (difference_In_Time / (1000 * 60 * 60)) % 24; 
		int years = (int) (difference_In_Time / (1000l * 60 * 60 * 24 * 365)); 
		int days = (int) (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
		
		String duration = "";
		
		if(years > 0) duration = (years > 9 ? years + ":" : "0" + years + ":");
		if(days > 0) duration += (days > 9 ? days + ":" : "0" + days + ":");
		if(hours > 0) duration += (hours > 9 ? hours + ":" : "0" + hours + ":");
		
		duration += (minutes > 9 ? minutes + ":" : "0" + minutes + ":");
		duration += (seconds > 9 ? seconds : "0" + seconds);
		
		return duration;
	}
	
	public static Date addTimeToDate(String durationInString) {
		Calendar c = Calendar.getInstance();
		
		c.setTime(new Date());
		
		if(durationInString.toLowerCase().contains("s")) {
			int seconds = Integer.valueOf(durationInString.replaceAll("s", "").replaceAll("S", ""));
			
			c.add(Calendar.SECOND, seconds);
		} else if(durationInString.toLowerCase().contains("m")) {
			int minutes = Integer.valueOf(durationInString.replaceAll("m", "").replaceAll("M", ""));
			
			c.add(Calendar.MINUTE, minutes);
		} else if(durationInString.toLowerCase().contains("h")) {
			int hours = Integer.valueOf(durationInString.replaceAll("h", "").replaceAll("H", ""));
			
			c.add(Calendar.HOUR_OF_DAY, hours);
		} else if(durationInString.toLowerCase().contains("d")) {
			int days = Integer.valueOf(durationInString.replaceAll("d", "").replaceAll("D", ""));
			
			c.add(Calendar.DAY_OF_MONTH, days);
		} else if(durationInString.toLowerCase().contains("w")) {
			int weeks = Integer.valueOf(durationInString.replaceAll("w", "").replaceAll("W", ""));
			
			c.add(Calendar.WEEK_OF_MONTH, weeks);
		} else if(durationInString.toLowerCase().contains("y")) {
			int years = Integer.valueOf(durationInString.replaceAll("y", "").replaceAll("Y", ""));
			
			c.add(Calendar.YEAR, years);
		}
		
		return c.getTime();
	}
}
