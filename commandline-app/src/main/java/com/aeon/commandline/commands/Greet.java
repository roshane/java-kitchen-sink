package com.aeon.commandline.commands;

import com.aeon.commandline.commands.services.JsonPlaceholderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;

@Command
public class Greet {
    private final JsonPlaceholderService jsonPlaceholderService;
    private final Logger logger = LoggerFactory.getLogger(Greet.class);

    public Greet(JsonPlaceholderService jsonPlaceholderService) {
        this.jsonPlaceholderService = jsonPlaceholderService;
    }

    @Command(command = "greet")
    public String doGreet() {
        return "Hello There, how are you doing?";
    }

    @Command(command = "todos")
    public String getSampleJson() {
        logger.info("Trying to retrieve todo json");
        return jsonPlaceholderService
                .todosJson()
                .stream()
                .map(it -> "id: %3s\tuserId: %3s\tcompleted: %5s\ttitle: %s".formatted(
                        it.get("id"),
                        it.get("userId"),
                        it.get("completed"),
                        it.get("title")
                ))
                .reduce("", (prev, next) -> String.join("\n", prev, next));

    }
}
