package xyz.shanmugavel.gf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import xyz.shanmugavel.gf.config.CacheConfiguration;

import java.io.IOException;

@ClientCacheApplication(name = "DataGemFireApplication", logLevel = "config")
@EnableGemfireRepositories
@Import({CacheConfiguration.class})
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }





}
