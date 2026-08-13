# JagadeeshCart

Multi-seller e-commerce marketplace — Java Servlets · JDBC · Tomcat · H2
Anna University R2025, Semester 3 capstone (solo project).

**This zip is your Week 1 checkpoint deliverable**: project skeleton, DB schema v1,
and working authentication (register / login / logout / session).

## What's inside
```
JagadeeshCart/
├── pom.xml
├── README.md
├── CONTRIBUTING.md
├── .gitignore
├── .env.example
├── .github/workflows/build.yml       ← CI pipeline
├── db/migrations/V1__init_schema.sql ← numbered migration (Section 14 of spec)
└── src/
    ├── main/java/com/jagadeesh/jagadeeshcart/
    │   ├── controller/   (RegisterServlet, LoginServlet, LogoutServlet, HealthServlet)
    │   ├── service/      (UserService)
    │   ├── dao/           (UserDAO interface, DAOFactory)
    │   ├── dao/impl/      (UserDAOImpl — all SQL, PreparedStatement only)
    │   ├── model/         (User)
    │   ├── dto/           (UserResponseDTO, ApiResponse)
    │   ├── filter/        (EncodingFilter, AuthFilter)
    │   ├── listener/      (DataSourceListener — HikariCP singleton)
    │   ├── util/          (PasswordUtil, ValidationUtil, JsonUtil)
    │   └── exception/     (ValidationException, AuthException)
    ├── main/resources/    (application.properties, logback.xml, schema.sql, seed.sql)
    ├── main/webapp/       (index.jsp, WEB-INF/web.xml, WEB-INF/jsp/*.jsp)
    └── test/java/...      (UserDAOTest — runs against embedded H2)
```

## How to upload this to GitHub (exact steps)

1. Unzip the file you downloaded — you'll get a folder called `JagadeeshCart`.
2. Go to https://github.com/new and create a new **empty** repository named `JagadeeshCart`
   (do NOT check "Add a README" — you already have one).
3. On your computer, open a terminal inside the unzipped `JagadeeshCart` folder and run:
   ```
   git init
   git add .
   git commit -m "feat: week 1 - auth, DB schema, project skeleton"
   git branch -M main
   git remote add origin https://github.com/<your-username>/JagadeeshCart.git
   git push -u origin main
   ```
4. Refresh your GitHub repo page — all files should now be visible.

If you don't have `git` installed or don't want to use the terminal, you can instead:
1. Go to your new empty repo on GitHub → click **"uploading an existing file"**.
2. Drag the *contents* of the unzipped `JagadeeshCart` folder (not the zip itself) into the browser.
3. Commit directly to `main`.

## How to run it locally
See `CONTRIBUTING.md`.

## Tech stack
| Component | Choice |
|---|---|
| Language | Java 17 |
| Web layer | Servlets 5.0 + JSP/JSTL |
| Database | H2 (embedded/file mode for dev) |
| Connection pool | HikariCP |
| Password hashing | jBCrypt |
| JSON | Gson |
| Logging | SLF4J + Logback |
| Testing | JUnit 5 + Mockito |
| Build | Maven |
| CI | GitHub Actions |

## Status against the spec (Week 1: Jul 27 – Aug 2)
- [x] Authentication: register / login / logout, session-based, session ID regenerated on login
- [x] Base DAO layer: `UserDAO` interface + JDBC impl, PreparedStatement only, try-with-resources
- [x] Project skeleton: Maven + web.xml, layered package structure per spec Section 2
- [x] DB schema v1: all 6 tables, FKs indexed, DECIMAL for money, `created_at` everywhere
- [x] Connection pool owned by a single `ServletContextListener` (HikariCP)
- [x] Passwords bcrypt-hashed, never logged
- [x] `GET /api/v1/health` endpoint
- [ ] Product/cart/order/review features — Week 2 onward, not part of this checkpoint

## Deployed link
_Add your live Tomcat URL here once deployed (Section 10 of the spec)._
