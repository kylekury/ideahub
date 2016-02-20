# In Eclipse, add to arguments in the Run Configurations for...

Start Server: server src/main/resources/config.yml

Run DB Migrations: db migrate src/main/resources/config.yml

## Calling APIs

### Get a list of idea definitions

```
URI: /idea/definition
Headers:
    Accept: application/json;
    
Output:
    [
        0: {
            "name": "name"
            "metadata": {
                "name_text": "Idea Name"
                "justification_text": "How does the name tie into what your idea is about?"
            }
        },
        1: {
            "name": "elevator_pitch"
            "metadata": {
                "name_text": "Elevator Pitch"
                "justification_text": "How will this entice someone to want to know more about your idea?"
            }
        }
    ]
```
