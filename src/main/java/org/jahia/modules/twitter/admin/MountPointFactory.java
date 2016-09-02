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

import org.hibernate.validator.constraints.NotEmpty;
import org.jahia.modules.external.admin.mount.AbstractMountPointFactory;
import org.jahia.modules.external.admin.mount.validator.LocalJCRFolder;
import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * Created by Carlos Monzon
 */
public class MountPointFactory extends AbstractMountPointFactory {
    private static final Logger logger = LoggerFactory.getLogger(MountPointFactory.class);
    /**
     * The constant CONSUMER_KEY.
     */
    public static final String CONSUMER_KEY = "consumerKey";
    /**
     * The constant CONSUMER_SECRET.
     */
    public static final String CONSUMER_SECRET = "consumerSecret";
    /**
     * The constant ACCESS_TOKEN.
     */
    public static final String ACCESS_TOKEN = "accessToken";
    /**
     * The constant ACCESS_TOKEN_SECRET.
     */
    public static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
    /**
     * The constant SEARCH.
     */
    public static final String SEARCH = "search";
    /**
     * The constant NUMBER_TWEETS.
     */
    public static final String NUMBER_TWEETS = "numberTweets";


    @NotEmpty
    private String name;

    @LocalJCRFolder
    private String localPath;

    @NotEmpty
    private String consumerKey;

    @NotEmpty
    private String consumerSecret;

    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String accessTokenSecret;

    @NotEmpty
    private String search;

    @NotEmpty
    private String numberTweets;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalPath() {
        return localPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMountNodeType() {
        return "jnt:twitterMountPoint";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(JCRNodeWrapper mountNode) throws RepositoryException {
        mountNode.setProperty(CONSUMER_KEY, consumerKey);
        mountNode.setProperty(CONSUMER_SECRET, consumerSecret);
        mountNode.setProperty(ACCESS_TOKEN, accessToken);
        mountNode.setProperty(ACCESS_TOKEN_SECRET, accessTokenSecret);
        mountNode.setProperty(SEARCH, search);
        mountNode.setProperty(NUMBER_TWEETS, numberTweets);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(JCRNodeWrapper nodeWrapper) throws RepositoryException {
        super.populate(nodeWrapper);
        this.name = getName(nodeWrapper.getName());
        try {
            this.localPath = nodeWrapper.getProperty("mountPoint").getNode().getPath();
        } catch (PathNotFoundException e) {
            logger.error("No local path defined for this mount point");
        }
        this.consumerSecret = nodeWrapper.getPropertyAsString(CONSUMER_SECRET);
        this.accessToken = nodeWrapper.getPropertyAsString(ACCESS_TOKEN);
        this.consumerKey = nodeWrapper.getPropertyAsString(CONSUMER_KEY);
        this.accessTokenSecret = nodeWrapper.getPropertyAsString(ACCESS_TOKEN_SECRET);
        this.search = nodeWrapper.getPropertyAsString(SEARCH);
        this.numberTweets = nodeWrapper.getPropertyAsString(NUMBER_TWEETS);
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets local path.
     *
     * @param localPath the local path
     */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    /**
     * Gets consumer key.
     *
     * @return the consumer key
     */
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * Sets consumer key.
     *
     * @param consumerKey the consumer key
     */
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    /**
     * Gets consumer secret.
     *
     * @return the consumer secret
     */
    public String getConsumerSecret() {
        return consumerSecret;
    }

    /**
     * Sets consumer secret.
     *
     * @param consumerSecret the consumer secret
     */
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret= consumerSecret;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() { return accessToken; }

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken= accessToken;
    }

    /**
     * Gets access token secret.
     *
     * @return the access token secret
     */
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    /**
     * Sets access token secret.
     *
     * @param accessTokenSecret the access token secret
     */
    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    /**
     * Gets search.
     *
     * @return the search
     */
    public String getSearch() {
        return search;
    }

    /**
     * Sets search.
     *
     * @param search the search
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Gets number tweets.
     *
     * @return the number tweets
     */
    public String getNumberTweets() {
        return numberTweets;
    }

    /**
     * Sets number tweets.
     *
     * @param numberTweets the number tweets
     */
    public void setNumberTweets(String numberTweets) {
        this.numberTweets = numberTweets;
    }
}
