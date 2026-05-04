package swbg.solutions.com.maintenanceapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MaintenanceAppApplication

fun main(args: Array<String>) {
    runApplication<MaintenanceAppApplication>(*args)
}
