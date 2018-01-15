package xyz.shanmugavel.gf.config;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import xyz.shanmugavel.gf.entity.Person;
import xyz.shanmugavel.gf.repo.PersonRepository;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.stream.StreamSupport.stream;

@Configuration
public class CacheConfiguration {

    @Bean
    public ClientCacheFactoryBean gemfireCacheClient() {
        ClientCacheFactoryBean clientCacheFactoryBean = new ClientCacheFactoryBean();
        clientCacheFactoryBean.setPdxSerializer(new ReflectionBasedAutoSerializer(true,
                "xyz.shanmugavel.gf.entity.Person"));
        clientCacheFactoryBean.setSubscriptionEnabled(true);
        return clientCacheFactoryBean;
    }

    @Bean(name = GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME)
    public PoolFactoryBean gemfirePool() {

        PoolFactoryBean gemfirePool = new PoolFactoryBean();

        gemfirePool.addLocators(Collections.singletonList(new ConnectionEndpoint("localhost", 10334)));
        gemfirePool.setName(GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME);
        gemfirePool.setKeepAlive(false);
        gemfirePool.setPingInterval(TimeUnit.SECONDS.toMillis(5));
        gemfirePool.setRetryAttempts(1);
        gemfirePool.setSubscriptionEnabled(true);
        gemfirePool.setThreadLocalConnections(false);

        return gemfirePool;
    }

    @Bean
    ClientRegionFactoryBean<Long, Long> peopleRegion(ClientCache gemfireCache, Pool gemfirePool) {
        ClientRegionFactoryBean<Long, Long> region = new ClientRegionFactoryBean<>();
        region.setName("PeopleRegion");
        region.setLookupEnabled(true);
        region.setCache(gemfireCache);
        region.setPool(gemfirePool);
        region.setShortcut(ClientRegionShortcut.PROXY);

        return region;
    }

    @Bean
    ApplicationRunner run(PersonRepository personRepository) {

        return args -> {

            Person alice = new Person("Shan", 40);
            Person bob = new Person("Srini", 1);
            Person carol = new Person("Raj", 13);

            System.out.println("Before accessing data in GemFire...");

            asList(alice, bob, carol).forEach(person -> System.out.println("\t" + person));

            System.out.println("Save Shan, Srini and Raj to GemFire...");

            personRepository.save(alice);
            personRepository.save(bob);
            personRepository.save(carol);

            System.out.println("Lookup each person by name...");

            asList(alice.getName(), bob.getName(), carol.getName())
                    .forEach(name -> System.out.println("\t" + personRepository.findByName(name)));

            System.out.println("Query adults (over 18):");

            stream(personRepository.findByAgeGreaterThan(18).spliterator(), false)
                    .forEach(person -> System.out.println("\t" + person));

            System.out.println("Query babies (less than 5):");

            stream(personRepository.findByAgeLessThan(5).spliterator(), false)
                    .forEach(person -> System.out.println("\t" + person));

            System.out.println("Query teens (between 12 and 20):");

            stream(personRepository.findByAgeGreaterThanAndAgeLessThan(12, 20).spliterator(), false)
                    .forEach(person -> System.out.println("\t" + person));
        };
    }
}
