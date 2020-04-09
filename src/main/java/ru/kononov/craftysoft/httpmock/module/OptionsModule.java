package ru.kononov.craftysoft.httpmock.module;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.cli.Options;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class OptionsModule {

    @Provides
    @Singleton
    @Named("ApplicationOptions")
    Options options() {
        var options = new Options();
        options.addOption("p", "port", true, "Server port");
        options.addOption("f", "folder", true, "Config folder");
        return options;
    }

}
