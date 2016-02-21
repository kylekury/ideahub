package com.ideahub.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ideahub.dao.IdeaPartTypeDAO;
import com.ideahub.exceptions.IdeaPartTypeNotFoundException;
import com.ideahub.model.IdeaPartType;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Stores the static idea part definitions.
 * 
 * This is a very naive 'cache', and would not be optimized for a large
 * amount of data.
 * 
 * Could be replaced by Guava if we need to have an eviction strategy.
 * 
 * @author kyle
 *
 */
@PetiteBean
@NoArgsConstructor
@AllArgsConstructor
public class IdeaDefinitionCache {
    @PetiteInject
    private IdeaPartTypeDAO ideaPartTypeDAO;
    
    private Map<Integer, IdeaPartType> ideaPartTypes;
    private Map<String, IdeaPartType> ideaPartTypesByName;
    private List<IdeaPartType> ideaPartsAsList;
        
    public List<IdeaPartType> getIdeaDefinition() {
        loadDefinitions();
        
        return ideaPartsAsList;
    }

    public Map<String, IdeaPartType> getIdeaPartTypesByName() {
        loadDefinitions();

        return ideaPartTypesByName;
    }

    public void setIdeaPartTypeDAO(IdeaPartTypeDAO ideaPartTypeDAO) {
        this.ideaPartTypeDAO = ideaPartTypeDAO;
    }

    public boolean isPartTypeAllowedMultiple(final int ideaPartTypeId) throws IdeaPartTypeNotFoundException {
        loadDefinitions();
        
        if (!ideaPartTypes.containsKey(ideaPartTypeId)) {
            throw new IdeaPartTypeNotFoundException();
        }
        
        return ideaPartTypes.get(ideaPartTypeId).isAllowMultiple();
    }
    
    private void loadDefinitions() {
        if ((ideaPartTypes == null) && (ideaPartTypesByName == null)) {
            // This will purge any existing data, very inefficient 
            ideaPartTypes = new HashMap<>();
            ideaPartTypesByName = new HashMap<>();
            
            for (IdeaPartType ideaPartType : ideaPartTypeDAO.getIdeaDefinition()) {
                ideaPartTypes.put(ideaPartType.getId(), ideaPartType);
                ideaPartTypesByName.put(ideaPartType.getName(), ideaPartType);
            }
            
            ideaPartsAsList = new ArrayList<>(ideaPartTypes.values());
        }
    }
}
