package study.jpadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class JpaDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaDataApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider(){
		//실제로는 사용자의 ID를 꺼내와야함
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
