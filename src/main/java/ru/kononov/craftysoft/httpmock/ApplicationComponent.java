package ru.kononov.craftysoft.httpmock;

import dagger.Component;
import ru.kononov.craftysoft.httpmock.module.ConfigurationModule;
import ru.kononov.craftysoft.httpmock.module.OptionsModule;
import ru.kononov.craftysoft.httpmock.module.ServerModule;
import ru.kononov.craftysoft.httpmock.server.Server;

import javax.inject.Singleton;

@Component(modules = {
        ConfigurationModule.class,
        ServerModule.class,
        OptionsModule.class
})
@Singleton
interface ApplicationComponent {

    Server server();

}
