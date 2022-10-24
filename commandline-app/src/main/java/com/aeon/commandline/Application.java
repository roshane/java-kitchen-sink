package com.aeon.commandline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.*;

public class Application {

    static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsResourcesBase = "/js";
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
//        featchHttpResource();
//        runJsScript();
        runGraalJsScript();
    }

    public static void runJsScript() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("ES6");
        final List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        try {
            engine.eval(readJsFileAsString("simple-script.js"));
            Object result = Invocable.class.cast(engine).invokeFunction("hello", numbers);
            logger.info("Result from script: {}", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void runGraalJsScript() {
        try (Context context = Context.newBuilder("js").build()) {
            context.eval(Source.newBuilder("js", readJsFileAsString("simple-script.js"), "src.js").build());
            final Value echo = context.getBindings("js").getMember("echo");
            Map<String, Object> data = Collections.singletonMap("data", Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            final Value result = echo.execute(mapper.writeValueAsString(data));
            final List<Integer> list =  mapper.readValue(result.asString(), List.class);
            logger.info("Graal script result: {}", list);
        } catch (Exception ex) {
            logger.error("Error ", ex);
            throw new RuntimeException(ex);
        }
    }


    private static String readJsFileAsString(String fileName) {
        String filePath = jsResourcesBase + "/" + fileName;
        String result = "";
        try (InputStream stream = Application.class.getResourceAsStream(filePath)) {
            result = new String(Objects.requireNonNull(stream, "null stream").readAllBytes());
        } catch (Exception ex) {
            logger.error("JS Script file load error: ", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    static void featchHttpResource() {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .build();
        try {
            final HttpResponse<String> response = httpClient.send(
                    request,
                    handler -> HttpResponse.BodySubscribers.ofString(Charset.defaultCharset())
            );
            System.out.println("Response from server");
            System.out.println(response.body());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
