package ru.otus.hw;
import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.otus.hw.repositories.AuthorRepository;

import java.sql.SQLException;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws SQLException {
		ApplicationContext context = SpringApplication.run(Application.class, args);

		Console.main(args);
	}

}
