package ru.leonov.deal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients("ru.leonov.deal.client")
@SpringBootApplication
class DealApplication

fun main(args: Array<String>) {
    runApplication<DealApplication>(*args)
}
