package ru.yetanothercoder.tests.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

/**
 * @author www.yetanothercoder.ru
 * @created 6/20/12 7:45 AM
 */
public class NettyClientTests {

    private static Logger logger = Logger.getLogger(NettyClientTests.class);

    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new URI("http://ya.ru/");
    }

    @Test
    public void testYaRu() throws Exception {
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "localhost" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if (scheme.equalsIgnoreCase("http")) {
                port = 80;
            } else if (scheme.equalsIgnoreCase("https")) {
                port = 443;
            }
        }

        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        boolean ssl = scheme.equalsIgnoreCase("https");

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpSnoopClientPipelineFactory(ssl));

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        // Prepare the HTTP request.
        HttpRequest request = new DefaultHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.setHeader(HttpHeaders.Names.HOST, host);
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
//        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//        request.setHeader(HttpHeaders.Names.CACHE_CONTROL, "max-age=0");
//        request.setHeader(HttpHeaders.Names.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        request.setHeader(HttpHeaders.Names.ACCEPT_CHARSET, "windows-1251,utf-8;q=0.7,*;q=0.3");
//        request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, "gzip,deflate,sdch");
//        request.setHeader(HttpHeaders.Names.ACCEPT_LANGUAGE, "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
//        request.setHeader(HttpHeaders.Names.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
//                "AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5");

        // Set some example cookies.
        CookieEncoder httpCookieEncoder = new CookieEncoder(false);
        httpCookieEncoder.addCookie("yandexuid", "2408254491337239538");
        httpCookieEncoder.addCookie("ys", "bar.chrome.1.4.418#translate.chrome.1.0.102#vb.chrome.1.2.321");
        request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

        logger.debug("request: " + request);

        // Send the HTTP request.
        channel.write(request);

        // Wait for the server to close the connection.
        channel.getCloseFuture().awaitUninterruptibly();

        // Shut down executor threads to exit.
        bootstrap.releaseExternalResources();

    }


}
