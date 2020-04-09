package ru.kononov.craftysoft.httpmock.module;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.kononov.craftysoft.httpmock.server.Server;
import ru.kononov.craftysoft.httpmock.util.ConfigurationResolver;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class ServerModule {

    private final String[] args;

    public ServerModule(String[] args) {
        this.args = args;
    }

    @Provides
    @Singleton
    Server server(@Named("ApplicationOptions") Options options, ConfigurationResolver configurationResolver) {
        try {
            var parsed = new DefaultParser().parse(options, args);
            var port = Integer.parseInt(parsed.getOptionValue("port", "8080"));
            return new Server(port, configurationResolver);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
