# In Eclipse, add to arguments in the Run Configurations for...

Start Server: server src/main/resources/config.yml

Run DB Migrations: db migrate src/main/resources/config.yml

## Calling APIs

** Unless otherwise specified, all APIs require an authenticated user

### Get a list of idea definitions

```
URI: /idea/definition
Method: GET
Headers:
    Accept: application/json;
    
Output:
    [{
        "name": "name",
        "allow_multiple": false,
        "metadata": {
            "name_text": "Idea Name",
            "justification_text": "How does the name tie into what your idea is about?"
        }
    }, {
        "name": "elevator_pitch",
        "allow_multiple": false,
        "metadata": {
            "name_text": "Elevator Pitch",
            "justification_text": "How will this entice someone to want to know more about your idea?"
        }
    }, {
        "name": "features",
        "allow_multiple": true,
        "metadata": {
            "name_text": "Feature",
            "justification_text": "How does this provide more value to your core idea/elevator pitch?"
    }]
```

### Create an idea

```
URI: /idea
Method: POST
Headers:
    Accept: application/json;
    
Output:
    {
        "id": 1,
        "user_id": 1,
        "is_private": 0
    }
```

### Update an idea's privacy

```
URI: /idea/private/{isPrivate}
Method: PUT
Headers:
    Accept: application/json;
    
Output:
    {
        "id": 1,
        "user_id": 1,
        "is_private": 0
    }
```

### Delete an idea

```
URI: /idea/{ideaId}
Method: DELETE
Headers:
    Accept: application/json;
    
Output: true
```

### Load an idea

```
URI: /idea/{ideaId}
Method: GET
Headers:
    Accept: application/json;
    
Output:
    {
        "id": 1,
        "user_id": 1,
        "idea_parts": [{
            "id": 1,
            "user_id": 1,
            "idea_id": 1,
            "idea_part_type_id": 1,
            "upvotes": 4,
            "downvotes": 2,
            "content": "IdeaHub",
            "justification": "The project is GitHub for ideas, so IdeaHub seemed to be a natural fit.",
            "idea_part_suggestions": null
        }, {
            "id": 2,
            "user_id": 1,
            "idea_id": 1,
            "idea_part_type_id": 2,
            "upvotes": 3,
            "downvotes": 3,
            "content": "GitHub for ideas; iterate and flesh out ideas so that you can actually begin to execute.",
            "justification": "Showing potential users that we can guide them through the idea-forming process.",
            "idea_part_suggestions": null
        }, {
            "id": 3,
            "user_id": 1,
            "idea_id": 1,
            "idea_part_type_id": 3,
            "upvotes": 4,
            "downvotes": 2,
            "content": "Private and public ideas.",
            "justification": "This will allow users to prevent their ideas from being stolen, and generate revenue for us.",
            "idea_part_suggestions": null
        }, {
            "id": 4,
            "user_id": 1,
            "idea_id": 1,
            "idea_part_type_id": 3,
            "upvotes": 2,
            "downvotes": 5,
            "content": "Allow users to allow others to collaborate on their ideas.",
            "justification": "Further helps someone flesh out an idea, instead of relying on just themselves.",
            "idea_part_suggestions": null
        }]
    }
```

### Add/Update Idea Parts

```
URI: /idea/part
Method: PUT
Headers:
    Accept: application/json;
    Content-Type: application/json;

* Assume that idea_part_id 2 is a pre-existing part that you're attempting to modify
Input:
    [{
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "content": "Cool Awesome Idea",
        "justification": "It's awesome"
    },
     {
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "content": "Feature B",
        "justification": "It's really cool, so yeah."
    },
     {
        "id": 15,
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "content": "New Feature C Name.",
        "justification": "It's a great update!"
    }]
    
Output:
    [{
        "id": 33,
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "content": "Cool Awesome Idea",
        "justification": "It's awesome"
    }, {
        "id": 34,
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "content": "Feature B",
        "justification": "It's really cool, so yeah."
    }, {
        "id": 15,
        "user_id": 1,
        "idea_id": 1,
        "idea_part_type_id": 7,
        "upvotes": 3,
        "downvotes": 3,
        "content": "New Feature C Name.",
        "justification": "It's a great update!"
    }]
```

### Delete Idea Part

```
URI: /idea/part/{ideaPartId}
Method: DELETE
Headers:
    Accept: application/json;
    
Output: true
```

### Get an idea part w/ suggestions

```
URI: /idea/part/{ideaPartId}
Method: GET
Headers:
    Accept: application/json;
    
Output: { See update /idea/part output }
```


### Vote on an idea part

```
URI: /idea/part/{ideaPartId}/[upvote|downvote]
Method: PUT
Headers:
    Accept: application/json;
    
Output: {See update /idea/part output}
```


### Create or update an idea part suggestion

```
URI: /idea/part/suggestion
Method: PUT
Headers:
    Accept: application/json;

* If you supply "id" it will update the suggestion, provided you are the owner of it.
    
Input:
    {
        "user_id": 1,
        "idea_part_id": 2,
        "suggestion": "Maybe make it a little more accessible to users."
    }
    
Output: 
    {
        "id": 4,
        "idea_id": 1
        "idea_part_id": 2,
        "user_id": 1,
        "suggestion": "Maybe make it a little more accessible to users.",
        "upvotes": 0,
        "downvotes": 0
    }
```

### Delete Idea Part Suggestion

```
URI: /idea/part/suggestion/{ideaPartSuggestionId}
Method: DELETE
Headers:
    Accept: application/json;
    
Output: true
```

### Vote on an idea part suggestion

```
URI: /idea/part/{ideaPartSuggestionId}/[upvote|downvote]
Method: PUT
Headers:
    Accept: application/json;
    
Output: {See update /idea/part/suggestion output}
```

### List out popular ideas

```
URI: /idea/popular?total={itemsToReturn}&page={currentPage}
Method: GET
Headers:
    Accept: application/json;
    
Output: {See load idea output}
```

### List out recent ideas

```
URI: /idea/recent?total={itemsToReturn}&page={currentPage}
Method: GET
Headers:
    Accept: application/json;
    
Output: {See load idea output}
```