package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SuppressWarnings("HideUtilityClassConstructor")
public class MockHttpURLConnectionFactory {

    private static final HttpUrlStreamHandler httpUrlStreamHandler;

    static {
        var urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);

        httpUrlStreamHandler = new HttpUrlStreamHandler();
        given(urlStreamHandlerFactory.createURLStreamHandler("http")).willReturn(httpUrlStreamHandler);
    }

    public MockHttpURLConnectionFactory() {
        // All access through static methods
    }

    public static HttpURLConnection create(String url) throws MalformedURLException {
        var urlConnection = mock(HttpURLConnection.class);
        httpUrlStreamHandler.reset();
        httpUrlStreamHandler.addConnection(new URL(url), urlConnection);

        return urlConnection;
    }
}
