package ru.yetanothercoder.tests.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

/**
 * @author www.yetanothercoder.ru
 * @created 6/20/12 9:14 AM
 */
public class HttpSnoopClientPipelineFactory implements ChannelPipelineFactory {

    private final boolean ssl;

    public HttpSnoopClientPipelineFactory(boolean ssl) {
        this.ssl = ssl;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Enable HTTPS if necessary.
        if (ssl) {
            /*SSLEngine engine =
                      SecureChatSslContextFactory.getClientContext().createSSLEngine();
          engine.setUseClientMode(true);

          pipeline.addLast("ssl", new SslHandler(engine));*/
        }

        pipeline.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        pipeline.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", new HttpSnoopClientHandler());
        return pipeline;
    }
}

