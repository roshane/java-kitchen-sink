package com.aeon.gql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApplicationBoot

fun main(args: Array<String>) {
    runApplication<ApplicationBoot>(*args)
}