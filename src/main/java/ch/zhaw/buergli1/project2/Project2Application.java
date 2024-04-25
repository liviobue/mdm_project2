package ch.zhaw.buergli1.project2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ch.zhaw.buergli1.project2")
public class Project2Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Project2Application.class, args);
	}

}
