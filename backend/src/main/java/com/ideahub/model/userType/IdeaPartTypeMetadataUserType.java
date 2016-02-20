package com.ideahub.model.userType;

import com.ideahub.model.IdeaPartTypeMetadata;

/**
 * Map the generic JSON user type to the IdeaPartTypeMetadata class, to allow
 * easy persistence & serialization/deserialization to a Hibernate column
 * that stores JSON as a VARCHAR. 
 * 
 * @author kyle
 *
 */
public class IdeaPartTypeMetadataUserType extends JsonUserType {

    @Override
    public Class<IdeaPartTypeMetadata> returnedClass() {
        return IdeaPartTypeMetadata.class;
    }
    
}

