package com.mycorp.zendesk;

import java.nio.charset.Charset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@ComponentScan({"com.mycorp"})
public class ZendeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZendeskApplication.class, args);
    }

    // RestTemplate Bean using UTF-8 chatset
    @Bean(name = "restTemplateUTF8")
    public RestTemplate getRestClient() {
        RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

		restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }
}
