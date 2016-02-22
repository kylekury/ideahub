package com.ideahub.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.model.Idea;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 
 * LRU cache for popular/recent idea lists.
 * 
 * @author kyle
 *
 */
@PetiteBean
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFeedCache {
    @PetiteInject
    IdeaDAO ideaDAO;

    public enum IdeaFeedType {
        RECENT, POPULAR
    }

    private final long cacheTimeoutInSeconds = 10;
    private final int cacheSize = 5;

    private LoadingCache<IdeaFeedType, List<Idea>> feedCache;

    public List<Idea> getIdeaFeed(IdeaFeedType feedType, int itemsToReturn) throws ExecutionException {
        if (feedCache == null) {
            feedCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(cacheTimeoutInSeconds, TimeUnit.SECONDS)
                    .build(new CacheLoader<IdeaFeedType, List<Idea>>() {
                        public List<Idea> load(IdeaFeedType key) { // no checked exception
                            switch (key) {
                            case RECENT:
                                return ideaDAO.findRecent(cacheSize);
                            case POPULAR:
                                return ideaDAO.findPopularIdeas(cacheSize);
                            }

                            // This should never happen
                            return new ArrayList<>();
                        }
                    });
        }
        
        return feedCache.get(feedType);
    }
}
