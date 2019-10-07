package com.radwan;

import com.radwan.config.BankingServiceConfiguration;
import com.radwan.exception.AccountAccessException;
import com.radwan.resources.AccountAccessExceptionMapper;
import com.radwan.resources.AccountCreationExceptionMapper;
import com.radwan.resources.AccountResource;
import com.radwan.resources.AccountTransferExceptionMapper;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class BankingApplication extends Application<BankingServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new BankingApplication().run(args);
    }

    @Override
    public String getName() {
        return "banking-service";
    }

    @Override
    public void initialize(Bootstrap<BankingServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<BankingServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(BankingServiceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
    }


    @Override
    public void run(BankingServiceConfiguration configuration,
                    Environment environment) {
        final AccountResource resource = new AccountResource();
        environment.jersey().register(resource);
        environment.jersey().register(new AccountTransferExceptionMapper());
        environment.jersey().register(new AccountCreationExceptionMapper());
        environment.jersey().register(new AccountAccessExceptionMapper());
    }

}