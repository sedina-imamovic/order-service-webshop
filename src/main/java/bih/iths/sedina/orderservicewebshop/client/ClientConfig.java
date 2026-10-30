package bih.iths.sedina.orderservicewebshop.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {
    // Bean för att skapa en RestClient för produktservice med bas-URL från application.properties
    @Bean
    public RestClient productRestClient(
            @Value("${product-service.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
