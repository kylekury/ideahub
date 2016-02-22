package com.ideahub.cache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final int cacheSize = 100;
    private final int cachePage = 0;

    private LoadingCache<IdeaFeedType, Set<Idea>> feedCache;

    public Set<Idea> getIdeaFeed(IdeaFeedType feedType, int itemsToReturn) throws ExecutionException {
        if (feedCache == null) {
            feedCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(cacheTimeoutInSeconds, TimeUnit.SECONDS)
                    .build(new CacheLoader<IdeaFeedType, Set<Idea>>() {
                        public Set<Idea> load(IdeaFeedType key) { // no checked exception
                            switch (key) {
                            case RECENT:
                                return ideaDAO.findRecent(cacheSize, cachePage);
                            case POPULAR:
                                return ideaDAO.findPopular(cacheSize, cachePage);
                            }

                            // This should never happen
                            return new HashSet<>();
                        }
                    });
        }
        
        return feedCache.get(feedType);
    }
}
