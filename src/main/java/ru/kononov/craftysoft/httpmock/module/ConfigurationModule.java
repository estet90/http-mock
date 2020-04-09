package ru.kononov.craftysoft.httpmock.module;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.kononov.craftysoft.httpmock.util.ConfigurationResolver;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class ConfigurationModule {

    private final String[] args;

    public ConfigurationModule(String[] args) {
        this.args = args;
    }

    @Provides
    @Singleton
    ConfigurationResolver configurationResolver(@Named("ApplicationOptions") Options options) {
        try {
            var parsed = new DefaultParser().parse(options, args);
            var folder = parsed.getOptionValue("folder");
            return new ConfigurationResolver(folder);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
