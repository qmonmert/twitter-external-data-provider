package org.jahia.modules.twitter.utils;

import org.slf4j.Logger;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * The Class TwitterConfiguration.
 *
 * @author Juan Carlos Rodas
 * @version $Revision: 1.0 $
 * @since Build 11012016
 */
public class TwitterConfiguration {

    /**
     * Instantiates a new twitter configuration.
     */
    private TwitterConfiguration() {
    }

    /**
     * The Constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TwitterConfiguration.class);

    /**
     * The c builder.
     */
    private static ConfigurationBuilder cBuilder;

    /**
     * The configuration
     */
    private static Configuration configuration;

    /**
     * Gets the twitter configuration.
     *
     * @param forceNew the force new
     * @param debugEnabled           the debug enabled
     * @param oAuthConsumerKey       the o auth consumer key
     * @param oAuthConsumerSecret    the o auth consumer secret
     * @param oAuthAccessToken       the o auth access token
     * @param oAuthAccessTokenSecret the o auth access token secret
     * @return the twitter configuration
     */
    public static Configuration getTwitterConfiguration(boolean forceNew, boolean debugEnabled, String oAuthConsumerKey, String oAuthConsumerSecret, String oAuthAccessToken, String oAuthAccessTokenSecret) {
        if(forceNew || cBuilder == null || configuration ==  null) {
            cBuilder = null;
            configuration = null;
        }

        if (cBuilder == null ) {
            logger.debug("getTwitterConfiguration: Setting the twitter default configuration.");
            cBuilder = new ConfigurationBuilder();
            cBuilder.setDebugEnabled(debugEnabled);
            cBuilder.setOAuthConsumerKey(oAuthConsumerKey);
            cBuilder.setOAuthConsumerSecret(oAuthConsumerSecret);
            cBuilder.setOAuthAccessToken(oAuthAccessToken);
            cBuilder.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);
            cBuilder.setJSONStoreEnabled(true);
            configuration = cBuilder.build();
        }

        return configuration;
    }

    /**
     * Gets the twitter configuration.
     *
     * @param debugEnabled the debug enabled
     * @param oAuthConsumerKey the o auth consumer key
     * @param oAuthConsumerSecret the o auth consumer secret
     * @param oAuthAccessToken the o auth access token
     * @param oAuthAccessTokenSecret the o auth access token secret
     * @return the twitter configuration
     */
    public static Configuration getTwitterConfiguration(boolean debugEnabled, String oAuthConsumerKey, String oAuthConsumerSecret, String oAuthAccessToken, String oAuthAccessTokenSecret){
        return getTwitterConfiguration(false, debugEnabled, oAuthConsumerKey, oAuthConsumerSecret, oAuthAccessToken, oAuthAccessTokenSecret);
    }

}
