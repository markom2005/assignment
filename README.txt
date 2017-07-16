This simple webapp is created using Java, Spring Boot and Gradle. It runs using Jetty on port 8080.

Instructions:
1) Clone the project from GitHub and open it in you favourite IDE (for example IntelliJ IDEA).

2) Start the application in one of two ways:
  a) Open AssignmentApplication.java. Since it has a main method you can run it using either Run or Debug option.
  b) Naigate to assignment project from the teminal window (in your ide or OS command line) and execute: 
      gradlew build && java -jar -Xdebug build/libs/gs-spring-boot-0.1.0.jar
      
3) You have now started a webapp using Jetty on port 8080.

4) Available "features":

  a) GET /assignments
        Retrieves the list of assignments collected from this url: https://jsonplaceholder.typicode.com/posts.        
    
        Execute the following command from your command line or from Google Chrome Postman:
          curl localhost:8080/assignment
    
        Result: 
          List of assignments in JSON format. JSON attribures are returned in the following order: userId, id, body, title (body and title are returned in the reversed order then in the original JSON response from https://jsonplaceholder.typicode.com/posts).
   
   b) POST /ingest
        Receives a text send through "text" parameter and creates an image properly sized to fit received text.
   
        Execute the following command from your command line or from Google Chrome Postman:
          curl --data "text=Pozdrav iz Srbije! Hello from Serbia!" localhost:8080/ingest
    
        Result: 
          Url with the location of the created image with received text.
            For example: http://localhost:8080/image/2017-07-16-295007398247848025526069066.png
      
   c) GET /image/{imageName}
        Returns an image based on received image name. Image can be generated using the /ingest feature above.
        
        Execute the following command from your command line or from Google Chrome Postman:
          curl localhost:8080/image/2017-07-16-295007398247848025526069066.png
          
        Result:
          Image containing the text created using /ingest feature above.  
