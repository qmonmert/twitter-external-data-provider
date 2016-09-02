package org.jahia.modules.twitter.admin;

import com.google.common.collect.Sets;
import net.sf.ehcache.Ehcache;
import org.jahia.modules.external.ExternalData;
import org.jahia.modules.external.ExternalDataSource;
import org.jahia.modules.external.ExternalQuery;
import org.jahia.modules.external.query.QueryHelper;
import org.jahia.modules.twitter.utils.TwitterConfiguration;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.util.*;

public class TwitterDataSource implements ExternalDataSource,  ExternalDataSource.CanCheckAvailability, ExternalDataSource.Initializable,ExternalDataSource.Searchable {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TwitterDataSource.class);

    // Twitter API
    private static Twitter twitter;

    // Cache
    private EhCacheProvider ehCacheProvider;
    private Ehcache ecache;
    private static final String CACHE_NAME            = "twitter-cache";
    private static final String CACHE_TWITTER_TWEETS  = "cacheTwitterActivities";

    // Node types
    private static final String JNT_TWITTER_TWETT     = "jnt:twitterTweet";
    private static final String JNT_CONTENT_FOLDER    = "jnt:contentFolder";

    // Twitter keys account
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String search;
    private String numberTweets;

    // Properties : twitter
    private static final String ID              = "id";
    private static final String NAME            = "name";
    private static final String TEXT            = "text";
    private static final String SCREEN_NAME     = "screen_name";
    private static final String CREATED_AT      = "created_at";
    private static final String PROFILE_IMAGE   = "profile_image_url";
    private static final String USER            = "user";

    // Properties : JCR
    private static final String ROOT            = "root";

    // Constants
    private static final String TWETT           = "tweet";

    // CONSTRUCTOR
    public TwitterDataSource() {}

    // GETTERS AND SETTERS
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret){
        this.accessTokenSecret= accessTokenSecret;
    }

    public void setSearch(String search){
        this.search= search;
    }

    public void setNumberTweets(String numberTweets){
        this.numberTweets= numberTweets;
    }

    public void setCacheProvider(EhCacheProvider ehCacheProvider) { this.ehCacheProvider = ehCacheProvider; }

    // METHODS
    public void start() {
        setCache();
        setTwitterSession();
    }

    // UTILS
    private void setCache(){
        try {
            if (!ehCacheProvider.getCacheManager().cacheExists(CACHE_NAME)) {
                ehCacheProvider.getCacheManager().addCache(CACHE_NAME);
            }
            ecache = ehCacheProvider.getCacheManager().getCache(CACHE_NAME);
        } catch (Exception e) {
            logger.error("Error with the cache : " + e.getMessage());
        }
    }

    private void cleanCache(){
        if (ehCacheProvider != null && !ehCacheProvider.getCacheManager().cacheExists(CACHE_NAME)) {
            ehCacheProvider.getCacheManager().removeCache(CACHE_NAME);
        }
        ecache = null;
    }

    private void setTwitterSession() {
        if (logger.isDebugEnabled())
            logger.info("setTwitterSession: getting the twitter session.");
        if(twitter == null){
            if (logger.isDebugEnabled())
                logger.debug("setTwitterSession: try to get a new twitter session.");

            try {
                String consumerKey       = this.consumerKey;
                String consumerSecret    = this.consumerSecret;
                String accessToken       = this.accessToken;
                String accessTokenSecret = this.accessTokenSecret;

                twitter = new TwitterFactory(
                        TwitterConfiguration.getTwitterConfiguration(
                                true,
                                consumerKey,
                                consumerSecret,
                                accessToken,
                                accessTokenSecret
                        )).getInstance();
            }catch(Error err) {
                logger.error("setTwitterSession: could not get the twitter session.", err);
            }
        }else{
            if (logger.isDebugEnabled())
                logger.debug("setTwitterSession: the twitter session already exists.");
        }
    }
    /**
     * Execute a GET search/tweets on the twitter API
     * Example :
     *      - https://api.twitter.com/1.1/search/tweets.json?q=query_text
     */
    private JSONArray queryTwitter(String search) throws RepositoryException {
            JSONArray jsonArray = new JSONArray();
            if (twitter != null) {
                if (logger.isDebugEnabled())
                    logger.info("getTwittsByTag: Getting the twitters by tag: " + search);
                try {
                    Query query = new Query(search);
                    query.setCount(Integer.parseInt(this.numberTweets));
                    QueryResult result = twitter.search(query);
                    for (Status status : result.getTweets()) {
                        jsonArray.put(new JSONObject(TwitterObjectFactory.getRawJSON(status)));
                    }
                } catch (Exception tex) {
                    logger.error("getTwittsByTag: the Twitter Query return a error searching by tag: " + search + " " + tex);
                }
            }
            return jsonArray;
    }
    /**
     * Retieve all the twitter tweets.
     * If deleteCache
     *      retrieve the tweets on twitter (exemple : after the upload of a new tweet)
     * Else
     *      retrieve the tweets on cache
     */
    private JSONArray getCacheTwitterActivities(boolean deleteCache) throws RepositoryException {
        JSONArray tweets;
        if (ecache.get(CACHE_TWITTER_TWEETS) != null && !deleteCache) {
            tweets = (JSONArray) ecache.get(CACHE_TWITTER_TWEETS).getObjectValue();
        } else {
            if (logger.isDebugEnabled())
                logger.info("Refresh the tweets");
            tweets = queryTwitter(this.search);
        }
        return tweets;
    }

    // IMPLEMENTS : ExternalDataSource
    @Override
    public List<String> getChildren(String path) throws RepositoryException {
        List<String> r = new ArrayList<>();
        if (path.equals("/")) {
            try {
                JSONArray tweets = getCacheTwitterActivities(false);
                for (int i = 1; i <= tweets.length(); i++) {
                    JSONObject tweet = (JSONObject) tweets.get(i - 1);
                    r.add(i + "-" + TWETT + "-" + tweet.get(ID));
                }
                // example : 08-tweet-401034489
            } catch (JSONException e) {
                throw new RepositoryException(e);
            }
        }
        return r;
    }

    @Override
    public ExternalData getItemByIdentifier(String identifier) throws ItemNotFoundException {
        if (identifier.equals(ROOT)) {
            return new ExternalData(identifier, "/", JNT_CONTENT_FOLDER, new HashMap<String, String[]>());
        }
        Map<String, String[]> properties = new HashMap<>();
        String[] idTweet = identifier.split("-");
        if (idTweet.length == 3) {
            try {
                JSONArray tweets = getCacheTwitterActivities(false);
                // Find the tweet by its identifier
                int numTweet = Integer.parseInt(idTweet[0]) - 1;
                JSONObject tweet = (JSONObject) tweets.get(numTweet);
                // Add some properties
                properties.put(ID,              new String[]{ tweet.getString(ID)   });
                properties.put(TEXT,            new String[]{ tweet.getString(TEXT) });
                properties.put(CREATED_AT,      new String[]{ tweet.getString(CREATED_AT) });
                properties.put(PROFILE_IMAGE,   new String[]{ tweet.getJSONObject(USER).getString(PROFILE_IMAGE) });
                properties.put(NAME,            new String[]{ tweet.getJSONObject(USER).getString(NAME) });
                properties.put(SCREEN_NAME,     new String[]{ tweet.getJSONObject(USER).getString(SCREEN_NAME) });
                // Return the external data (a node)
                ExternalData data = new ExternalData(identifier, "/" + identifier, JNT_TWITTER_TWETT, properties);
                return data;
            } catch (Exception e) {
                throw new ItemNotFoundException(identifier);
            }
        } else {
            // Node not again created
            throw new ItemNotFoundException(identifier);
        }
    }

    @Override
    public ExternalData getItemByPath(String path) throws PathNotFoundException {
        String[] splitPath = path.split("/");
        try {
            if (splitPath.length <= 1) {
                return getItemByIdentifier(ROOT);
            } else {
                return getItemByIdentifier(splitPath[1]);
            }
        } catch (ItemNotFoundException e) {
            throw new PathNotFoundException(e);
        }
    }

    @Override
    public Set<String> getSupportedNodeTypes() {
        return Sets.newHashSet(JNT_CONTENT_FOLDER, JNT_TWITTER_TWETT);
    }

    @Override
    public boolean isSupportsHierarchicalIdentifiers() {
        return false;
    }

    @Override
    public boolean isSupportsUuid() {
        return false;
    }

    @Override
    public boolean itemExists(String path) {
        return false;
    }

    // Implements : ExternalDataSource.Searchable

    @Override
    public List<String> search(ExternalQuery query) throws RepositoryException {
        List<String> paths = new ArrayList<>();
        String nodeType = QueryHelper.getNodeType(query.getSource());
        if (NodeTypeRegistry.getInstance().getNodeType(JNT_TWITTER_TWETT).isNodeType(nodeType)) {
            try {
                JSONArray tweets = getCacheTwitterActivities(false);
                for (int i = 1; i <= tweets.length(); i++) {
                    JSONObject tweet = (JSONObject) tweets.get(i - 1);
                    String path = "/" + i + "-" + TWETT + "-" + tweet.get(ID);
                    paths.add(path);
                }
                // example of a path : /08-tweet-401034489
            } catch (JSONException e) {
                throw new RepositoryException(e);
            }
        }
        return paths;
    }

    /**
     * When the datasource stops the OAuth2Token is invalidated.
     */
    @Override
    public void stop() {
        try {
            cleanCache();
            twitter.setOAuth2Token(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * This function test the Twitter API availability by doing a getId call.
     */
    public boolean isAvailable() {
        try{
            twitter.getId();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
