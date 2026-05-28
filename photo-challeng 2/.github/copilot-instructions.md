# AI Coding Assistant Instructions for Photo Challenge App

## Project Overview
This is a Spring Boot web application for managing 52-week photography challenges. Users can select from multiple challenge plans (e.g., street photography, night photography), track progress through 52 sub-themes per plan, and upload photos with EXIF metadata extraction.

## Architecture
- **Backend**: Spring Boot 3.5+ with MVC pattern
- **Frontend**: Thymeleaf templates with Bootstrap 5
- **Database**: MySQL with JPA/Hibernate (ddl-auto: update)
- **Security**: Spring Security with email-based authentication
- **File Storage**: Local filesystem uploads with configurable directory

## Key Components
- **Entities**: `Plan` (challenge types), `SubTheme` (52 weekly topics), `User`, `Progress` (user completion tracking), `Photo` (uploads with metadata)
- **Services**: Business logic layer (PlanService, PhotoService, etc.)
- **Controllers**: REST endpoints and view handlers
- **Repositories**: JPA interfaces with custom query methods

## Development Workflow
1. **Build & Run**: Use `./mvnw spring-boot:run` (wrapper included)
2. **Database**: Ensure MySQL is running; schema auto-created via JPA
3. **File Uploads**: Create upload directory (`/Users/darren_li/uploads/photography_challenge`) or update `application.yml`
4. **Development**: Thymeleaf cache disabled, SQL logging enabled for debugging

## Code Patterns & Conventions

### Dependency Injection
```java
@Service
@RequiredArgsConstructor
public class PlanService {
  private final PlanRepository planRepository;
  // No @Autowired needed - constructor injection
}
```

### Entity Design
```java
@Entity
@Table(name = "plans")
@Data  // Lombok generates getters/setters
public class Plan {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
  private List<SubTheme> subThemes;
}
```

### Repository Queries
```java
public interface SubThemeRepository extends JpaRepository<SubTheme, Long> {
  List<SubTheme> findByPlanIdOrderByTopicOrderAsc(Long planId);
}
```

### Controller Patterns
```java
@Controller
@RequiredArgsConstructor
public class PlanController {
  @GetMapping("/")
  public String index(Model model, Principal principal) {
    // Principal.getName() returns authenticated user's email
    // Add attributes to model for Thymeleaf
  }
}
```

### Thymeleaf Templates
```html
<div th:each="plan : ${plans}">
  <h5 th:text="${plan.name}">Plan Name</h5>
  <a th:href="@{/plan/{id}(id=${plan.id})}">View Details</a>
</div>
```

## Common Tasks

### Adding a New Plan
1. Create SQL insert in `resources/SQL/` (follow naming: `XX_plan_name.sql`)
2. Update plan count references if needed

### User Progress Tracking
- `Progress` entity links User, Plan, and SubTheme
- Status enum: ACTIVE, COMPLETED, etc.
- Initialize 52 progress records when user selects a plan

### Photo Upload Handling
- Use `metadata-extractor` library for EXIF data
- Store files in configured upload directory
- Associate with Progress records

## Database Schema Notes
- Plans have 52 sub-themes each (topic_order 0-51)
- Progress tracks completion per sub-theme per user
- Photos linked to progress entries

## Configuration
- `application.yml`: Database credentials, upload paths, Thymeleaf settings
- Multipart limits: 10MB per file
- JPA: MySQL dialect, format_sql enabled

## Testing
- Standard Spring Boot test setup
- Use `@SpringBootTest` for integration tests
- Mock repositories/services as needed

## Deployment Notes
- Ensure upload directory exists and is writable
- Database connection configured for target environment
- Static resources served from `src/main/resources/static/`</content>
<parameter name="filePath">/Users/darren_li/Code/Java/Photography52Week/photo-challeng/.github/copilot-instructions.md