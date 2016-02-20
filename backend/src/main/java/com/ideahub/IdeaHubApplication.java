package com.ideahub;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

public class IdeaHubApplication extends Application<IdeaHubConfiguration> {
    public static void main(final String[] args) throws Exception {
        new IdeaHubApplication().run(args);
    }

    protected PetiteContainer petite;

    @Override
    public void initialize(final Bootstrap<IdeaHubConfiguration> bootstrap) {
        // Setting up the dependency injection and enabling the automatic
        // configuration.
        this.petite = new PetiteContainer();
        // Enables to use Class full names when referencing them for injection.
        // This will prevent us of having conflicts when generic class name
        // exists.
        this.petite.getConfig().setUseFullTypeNames(true);
        // This enables automatic registration of PetiteBeans.
        final AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
        petiteConfigurator.configure(this.petite);
    }

    @Override
    public void run(final IdeaHubConfiguration configuration, final Environment environment)
            throws Exception {

    }
}