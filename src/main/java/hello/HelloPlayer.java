package plaindoll;

import org.joda.time.LocalTime;

public class HelloPlayer{
	public static void main(String[] args) {
		LocalTime currentTime = new LocalTime();
		System.out.println("The current local time is: " + currentTime);
		Welcomer welcomer = new Welcomer();
		System.out.println(welcomer.sayWelcome());
	}
}
