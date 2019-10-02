package com.radwan;

import com.radwan.config.RevolutBankingConfiguration;
import com.radwan.resources.AccountResource;
import com.radwan.resources.AccountTransferExceptionMapper;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class BankingApplication extends Application<RevolutBankingConfiguration> {
    public static void main(String[] args) throws Exception {
        new BankingApplication().run(args);
    }

    @Override
    public String getName() {
        return "banking-service";
    }

    @Override
    public void initialize(Bootstrap<RevolutBankingConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<RevolutBankingConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(RevolutBankingConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }


    @Override
    public void run(RevolutBankingConfiguration configuration,
                    Environment environment) {
        final AccountResource resource = new AccountResource();
        environment.jersey().register(resource);
        environment.jersey().register(new AccountTransferExceptionMapper());
    }

}