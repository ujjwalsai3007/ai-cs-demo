package org.example.aicsdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AiCsDemoApplication

fun main(args: Array<String>) {
    runApplication<AiCsDemoApplication>(*args)
}
