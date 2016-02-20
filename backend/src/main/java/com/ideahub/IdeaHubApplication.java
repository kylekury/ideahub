package com.ideahub;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartSuggestion;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.User;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

public class IdeaHubApplication extends Application<IdeaHubConfiguration> {
    public static void main(final String[] args) throws Exception {
        new IdeaHubApplication().run(args);
    }

    protected PetiteContainer petite;
    private final HibernateBundle<IdeaHubConfiguration> hibernate = new HibernateBundle<IdeaHubConfiguration>(
            User.class,
            Idea.class,
            IdeaPart.class,
            IdeaPartType.class,
            IdeaPartSuggestion.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(
                final IdeaHubConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

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

        // Hibernate
        bootstrap.addBundle(this.hibernate);
        
        // DB Migrations
        bootstrap.addBundle(new MigrationsBundle<IdeaHubConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(
                    final IdeaHubConfiguration configuration) {
                return configuration.getDatabase();
            }
        });
    }

    @Override
    public void run(final IdeaHubConfiguration configuration, final Environment environment)
            throws Exception {
        // Forcing serialization to JSON use snake case and skip null values.
        environment.getObjectMapper()
                .setPropertyNamingStrategy(
                        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
                .setSerializationInclusion(Include.NON_NULL);

        this.registerExternalDependencies(configuration, environment);
    }

    @Override
    public String getName() {
        return "ideahub";
    }

    /**
     * Creates all the dependencies that does not belong to our project or that
     * we need a specific instance. All the beans that we have no control need
     * to be specified here or when there are multiple instances of the same
     * class and we need to differentiate it by name.
     *
     * @param configuration
     *            The application configuration object.
     * @param environment
     *            The application environment.
     */
    protected void registerExternalDependencies(final IdeaHubConfiguration configuration,
            final Environment environment) {
        this.petite.addSelf();
        // The SessionFactory that provides connection to the database.
        this.petite.addBean(SessionFactory.class.getName(), this.hibernate.getSessionFactory());
        // Hooking up our configuration just in case we need to pass it around.
        this.petite.addBean(IdeaHubConfiguration.class.getName(), configuration);
    }
}