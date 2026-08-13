# Contributing / Local Setup

1. Install JDK 17 and Maven.
2. Clone the repo: `git clone <your-repo-url>` then `cd JagadeeshCart`.
3. Copy `.env.example` to `.env` (not committed) and adjust if needed.
4. Build: `mvn clean package`
5. Run tests: `mvn test`
6. Generate real bcrypt hashes for `src/main/resources/seed.sql`:
   `mvn compile exec:java -Dexec.mainClass=com.jagadeesh.jagadeeshcart.util.PasswordUtil -Dexec.args="admin123"`
7. Deploy `target/jagadeeshcart.war` to Tomcat 9's `webapps/` folder, or run via an embedded Tomcat Maven plugin.
8. Visit `http://localhost:8080/jagadeeshcart/`
