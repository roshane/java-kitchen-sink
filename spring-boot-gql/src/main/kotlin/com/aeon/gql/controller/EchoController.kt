package com.aeon.gql.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EchoController {

    @GetMapping("/health")
    fun health(): String = "Ok"

    @GetMapping("/customers")
    fun customers(): List<Customer> = dummyCustomers(10)
}

data class Customer(val id: String, val name: String)

fun dummyCustomers(count: Int): List<Customer> =
    generateSequence(1) { it + 1 }
        .take(count)
        .map { Customer(it.toString(), "customer-${it}") }
        .toList()
