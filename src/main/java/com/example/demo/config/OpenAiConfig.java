package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiConfig {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @PostConstruct
    public void init() {
        System.out.println("🔥 OpenAI key loaded = " + apiKey.substring(0, 10));
    }
}
