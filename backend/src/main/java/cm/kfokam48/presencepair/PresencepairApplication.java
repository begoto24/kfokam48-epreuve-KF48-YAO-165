package cm.kfokam48.presencepair;

import java.time.Clock;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PresencepairApplication {

	public static void main(String[] args) {
		// Toutes les dates sont stockées et échangées en UTC (ENF7)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(PresencepairApplication.class, args);
	}

	/** Horloge injectable : les tests des règles RG1 et RG4 la remplacent par une horloge fixe. */
	@Bean
	Clock horloge() {
		return Clock.systemUTC();
	}

}
