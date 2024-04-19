## Words extractor for organization in github

Develop a Spring Boot application that provides the user with two input fields:

- Input for the GitHub organization link.
- Input for the GitHub access token.
- 'Go' button that, when clicked, displays all repositories of the specified organization. Highlight repositories where the 'README.md' file contains the word 'Hello'.

### how to run the docker container
```bash
docker build -t myorg/myapp .           
docker run -p 8080:8080 myorg/myapp      
```

Then enter  the required command
### what to do next
the current version of the application extracts README.md in a row, you may need to make a stream handler that will batch process the content README.md in base64, but this is still + 500 lines of code and plus you need to additionally think about how to glue packages so that the string search works
