package io.quarkus.hibernate.orm.runtime;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.service.spi.ServiceContributor;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.hibernate.orm.HibernateMetadata;
import io.quarkus.hibernate.orm.runtime.proxies.PreGeneratedProxies;
import io.quarkus.runtime.annotations.Recorder;

/**
 * @author Emmanuel Bernard emmanuel@hibernate.org
 */
@Recorder
public class HibernateOrmRecorder {

    // TODO: this needs to be updated when multiple PU as supported
    public Supplier<HibernateMetadata> enlistPersistenceUnit(final Set<String> defaultPersistentUnitEntities) {
        Logger.getLogger("io.quarkus.hibernate.orm").debugf("List of entities found by Quarkus deployment:%n%s",
                defaultPersistentUnitEntities);
        return new Supplier<HibernateMetadata>() {
            @Override
            public HibernateMetadata get() {
                return new DefaultHibernateMetadata(defaultPersistentUnitEntities);
            }
        };
    }

    /**
     * The feature needs to be initialized, even if it's not enabled.
     *
     * @param enabled Set to false if it's not being enabled, to log appropriately.
     */
    public void callHibernateFeatureInit(boolean enabled) {
        Hibernate.featureInit(enabled);
    }

    /**
     * Initializes the JPA configuration to be used at runtime.
     * 
     * @param jtaEnabled Should JTA be enabled?
     * @param strategy Multitenancy strategy to use.
     * @param multiTenancySchemaDataSource Data source to use in case of {@link MultiTenancyStrategy#SCHEMA} approach or
     *        {@link null} in case the default data source.
     * 
     * @return
     */
    public BeanContainerListener initializeJpa(boolean jtaEnabled, MultiTenancyStrategy strategy,
            String multiTenancySchemaDataSource) {
        return new BeanContainerListener() {
            @Override
            public void created(BeanContainer beanContainer) {
                JPAConfig instance = beanContainer.instance(JPAConfig.class);
                instance.setJtaEnabled(jtaEnabled);
                instance.setMultiTenancyStrategy(strategy);
                instance.setMultiTenancySchemaDataSource(multiTenancySchemaDataSource);
            }
        };
    }

    public BeanContainerListener registerPersistenceUnit(String unitName) {
        return new BeanContainerListener() {
            @Override
            public void created(BeanContainer beanContainer) {
                beanContainer.instance(JPAConfig.class).registerPersistenceUnit(unitName);
            }
        };
    }

    public BeanContainerListener initDefaultPersistenceUnit() {
        return new BeanContainerListener() {
            @Override
            public void created(BeanContainer beanContainer) {
                beanContainer.instance(JPAConfig.class).initDefaultPersistenceUnit();
            }
        };
    }

    public BeanContainerListener initMetadata(List<ParsedPersistenceXmlDescriptor> parsedPersistenceXmlDescriptors,
            Scanner scanner, Collection<Class<? extends Integrator>> additionalIntegrators,
            Collection<Class<? extends ServiceContributor>> additionalServiceContributors,
            PreGeneratedProxies proxyDefinitions, MultiTenancyStrategy strategy) {
        return new BeanContainerListener() {
            @Override
            public void created(BeanContainer beanContainer) {
                PersistenceUnitsHolder.initializeJpa(parsedPersistenceXmlDescriptors, scanner, additionalIntegrators,
                        additionalServiceContributors, proxyDefinitions, strategy);
            }
        };
    }

    public void startAllPersistenceUnits(BeanContainer beanContainer) {
        beanContainer.instance(JPAConfig.class).startAll();
    }
}
