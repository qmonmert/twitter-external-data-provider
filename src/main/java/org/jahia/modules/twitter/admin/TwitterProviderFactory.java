/**
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms & Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.twitter.admin;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.modules.external.ExternalContentStoreProvider;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRStoreProvider;
import org.jahia.services.content.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.jcr.RepositoryException;
import java.util.Arrays;

/**
 * This is the provider for the Twitter API Created by Carlos Monzon
 */
public class TwitterProviderFactory implements ProviderFactory, ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TwitterProviderFactory.class);

    private ApplicationContext applicationContext;
    private EhCacheProvider ehCacheProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeTypeName() {
        return "jnt:twitterMountPoint";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JCRStoreProvider mountProvider(JCRNodeWrapper mountPoint) throws RepositoryException {

        if (logger.isDebugEnabled())
            logger.info("TWITTER external provider module initialization: mountPoint.getPath() - " + mountPoint.getPath() + ", name - " + mountPoint.getName());

        // Define the provider basing on the mount point parameters
        ExternalContentStoreProvider externalContentStoreProvider = (ExternalContentStoreProvider) SpringContextSingleton.getBean("ExternalStoreProviderPrototype");
        externalContentStoreProvider.setKey(mountPoint.getIdentifier());
        externalContentStoreProvider.setMountPoint(mountPoint.getPath());

        //Define the datasource using the credentials defined in the mount point
        TwitterDataSource dataSource = new TwitterDataSource();
        dataSource.setAccessToken(mountPoint.getProperty("accessToken").getString());
        dataSource.setAccessTokenSecret(mountPoint.getProperty("accessTokenSecret").getString());
        dataSource.setConsumerKey(mountPoint.getProperty("consumerKey").getString());
        dataSource.setConsumerSecret(mountPoint.getProperty("consumerSecret").getString());
        dataSource.setSearch(mountPoint.getProperty("search").getString());
        dataSource.setNumberTweets(mountPoint.getProperty("numberTweets").getString());
        dataSource.setCacheProvider(ehCacheProvider);

        //
        dataSource.start();

        //Finalize the provider setup with datasource and some JCR parameters
        externalContentStoreProvider.setDataSource(dataSource);
        externalContentStoreProvider.setOverridableItems(Arrays.asList("jmix:description.*", "jmix:i18n.*"));
        externalContentStoreProvider.setReservedNodes(Arrays.asList("j:acl", "j:workflowRules"));
        externalContentStoreProvider.setNonExtendableMixins(Arrays.asList("jmix:image"));
        externalContentStoreProvider.setDynamicallyMounted(true);
        externalContentStoreProvider.setSessionFactory(JCRSessionFactory.getInstance());
        try {
            externalContentStoreProvider.start();
        } catch (JahiaInitializationException e) {
            logger.error(e.getMessage(), e);
        }
        if (logger.isDebugEnabled())
            logger.info("Twitter external provider module initialized");
        return externalContentStoreProvider;
    }

    /**
     * Sets cache provider.
     *
     * @param ehCacheProvider the eh cache provider
     */
    public void setCacheProvider(EhCacheProvider ehCacheProvider) {
        this.ehCacheProvider = ehCacheProvider;
    }

}
