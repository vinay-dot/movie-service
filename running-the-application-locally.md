# Running the Application

## Create `application-local.properties`

```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:sql/insert-movies.sql
```

## Run the Application

Start the application using the **local** Spring profile:

```bash
./mvnw spring-boot:test-run -Dspring-boot.run.profiles=local
```

The application will start with the `local` profile and automatically populate the database with the sample movie data.
