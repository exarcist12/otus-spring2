package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestService;

@ShellComponent
@RequiredArgsConstructor
public class TestCommand {

    private final StudentService studentService;
    private final TestRunnerService testRunnerService;

    private Student currentStudent;

    @ShellMethod(key = "login", value = "Login student")
    public String login(
            @ShellOption String firstName,
            @ShellOption String lastName
    ) {
        currentStudent = studentService.determineCurrentStudent(firstName, lastName);
        return String.format("Welcome, %s %s!", firstName, lastName);
    }

    @ShellMethod(key = "start", value = "Start testing")
    @ShellMethodAvailability("isTestAvailable")
    public void startTest() {
        testRunnerService.run(currentStudent.firstName(), currentStudent.lastName());
    }

    private Availability isTestAvailable() {
        return currentStudent != null
                ? Availability.available()
                : Availability.unavailable("Please login first using 'login' command");
    }
}