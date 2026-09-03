package com.middle.end.config;

import com.middle.end.api.model.PokemonType;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Query parameters bind enums by constant name by default, which would only
     * accept "FIRE". The spec declares the lowercase wire values, so convert via
     * the generated {@code fromValue} and accept any casing while we are at it.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, PokemonType.class,
                source -> PokemonType.fromValue(source.trim().toLowerCase(Locale.ROOT)));
    }
}
