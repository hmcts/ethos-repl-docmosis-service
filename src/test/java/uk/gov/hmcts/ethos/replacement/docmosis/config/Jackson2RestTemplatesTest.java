package uk.gov.hmcts.ethos.replacement.docmosis.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.codec.Decoder;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Jackson2RestTemplatesTest {

    private static final String JACKSON_2_CONVERTER_CLASS =
        "org.springframework.http.converter.json.MappingJackson2HttpMessageConverter";
    private static final String JACKSON_3_CONVERTER_CLASS =
        "org.springframework.http.converter.json.JacksonJsonHttpMessageConverter";
    private static final String JACKSON_2_DECODER_CLASS =
        "org.springframework.http.codec.json.Jackson2JsonDecoder";

    @Test
    void restTemplateBeanUsesOnlyJackson2ForJson() {
        HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();
        ReflectionTestUtils.setField(httpClientConfiguration, "timeout", 1000);

        assertJackson2Only(httpClientConfiguration.restTemplate());
    }

    @Test
    void webClientCodecsUseJackson2() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(Jackson2RestTemplates::useJackson2)
            .build();
        List<HttpMessageReader<?>> readers = strategies.messageReaders();

        assertThat(readers)
            .noneMatch(Jackson2RestTemplatesTest::isJackson3Reader)
            .anyMatch(reader -> reader instanceof DecoderHttpMessageReader<?> decoderReader
                && isJackson2Decoder(decoderReader.getDecoder()))
            .anyMatch(reader -> reader instanceof ServerSentEventHttpMessageReader sseReader
                && isJackson2Decoder(sseReader.getDecoder()));
    }

    private static boolean isJackson2Decoder(Decoder<?> decoder) {
        return decoder != null && JACKSON_2_DECODER_CLASS.equals(decoder.getClass().getName());
    }

    private static boolean isJackson3Reader(HttpMessageReader<?> reader) {
        if (reader instanceof DecoderHttpMessageReader<?> decoderReader) {
            return isJackson3Class(decoderReader.getDecoder().getClass().getName());
        }
        if (reader instanceof ServerSentEventHttpMessageReader sseReader && sseReader.getDecoder() != null) {
            return isJackson3Class(sseReader.getDecoder().getClass().getName());
        }
        return isJackson3Class(reader.getClass().getName());
    }

    private static boolean isJackson3Class(String className) {
        return className.contains("tools.jackson")
            || (className.contains("JacksonJsonDecoder") && !className.contains("Jackson2JsonDecoder"));
    }

    private static void assertJackson2Only(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        assertThat(converters).anyMatch(converter ->
            JACKSON_2_CONVERTER_CLASS.equals(converter.getClass().getName()));
        assertThat(converters).noneMatch(converter ->
            JACKSON_3_CONVERTER_CLASS.equals(converter.getClass().getName()));

        converters.stream()
            .filter(FormHttpMessageConverter.class::isInstance)
            .map(FormHttpMessageConverter.class::cast)
            .forEach(formConverter -> {
                List<HttpMessageConverter<?>> partConverters = formConverter.getPartConverters();
                assertThat(partConverters).anyMatch(converter ->
                    JACKSON_2_CONVERTER_CLASS.equals(converter.getClass().getName()));
                assertThat(partConverters).noneMatch(converter ->
                    JACKSON_3_CONVERTER_CLASS.equals(converter.getClass().getName()));
            });
    }
}
