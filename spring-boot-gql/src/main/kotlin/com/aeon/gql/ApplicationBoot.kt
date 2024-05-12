package com.aeon.gql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.templatemode.TemplateMode

@EnableWebMvc
@Configuration
@SpringBootApplication
class ApplicationBoot() : WebMvcConfigurationSupport(), ApplicationContextAware {

    private lateinit var context: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    @Bean
    fun templateResolver(context: ApplicationContext) = SpringResourceTemplateResolver()
        .apply {
            setApplicationContext(context)
            prefix = "classpath:/templates/"
            suffix = ".html"
            templateMode = TemplateMode.HTML
        }

    @Bean
    fun templateEngine(templateResolver: SpringResourceTemplateResolver) = SpringTemplateEngine()
        .apply {
            setTemplateResolver(templateResolver)
            enableSpringELCompiler = true
        }

    @Bean
    fun viewResolver(templateEngine: SpringTemplateEngine) = ThymeleafViewResolver()
        .apply {
            setTemplateEngine(templateEngine)
            order = 1
            viewNames = arrayOf(".html", ".xhtml")
        }
}

fun main(args: Array<String>) {
    runApplication<ApplicationBoot>(*args)
}