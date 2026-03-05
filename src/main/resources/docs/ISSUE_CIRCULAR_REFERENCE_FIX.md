# JPA 엔티티 순환 참조로 인한 무한 루프 문제 해결

## 문제 발생일
2024-12-19

## 문제 현상

### 증상
- 서버 로그에 동일한 SQL 쿼리가 무한 반복 출력
- 단일 API 요청에서 수십~수백 개의 쿼리 발생
- 서버 응답 지연 및 리소스 과다 사용

### 영향받은 API
- `GET /api/v1/activity-monitor/weekly-session/{id}`
- `GET /api/v1/classes/{id}/enrollments`
- 기타 엔티티 조회 관련 API

---

## 로그 분석

### 문제가 된 로그 패턴

```
2025-12-19 11:55:45.832 [http-nio-8080-exec-7] DEBUG org.hibernate.SQL -
    /* <criteria> */ select ... from tbl_class_enroll ce1_0
    left join tbl_class ce2_0 on ce2_0.class_id=ce1_0.class_id
    where ce2_0.class_id=?

2025-12-19 11:55:45.833 [http-nio-8080-exec-7] DEBUG org.hibernate.SQL -
    select ... from tbl_user u1_0 where u1_0.user_id=?

2025-12-19 11:55:45.834 [http-nio-8080-exec-7] DEBUG org.hibernate.SQL -
    /* <criteria> */ select ... from tbl_class_enroll ...

(동일 패턴 무한 반복)
```

### 로그 분석 결과

1. **동일 스레드에서 반복**: `http-nio-8080-exec-7` 스레드가 단일 요청 내에서 같은 쿼리를 수십 번 반복
2. **`/* <criteria> */` 주석**: JPA Criteria API 또는 연관 엔티티 조회 시 발생
3. **조회 패턴**: `tbl_class_enroll` → `tbl_user` → `tbl_curriculum` → 다시 `tbl_class_enroll` (순환)

---

## 원인 분석

### 근본 원인: JPA 엔티티 간 양방향 참조 + JSON 직렬화

Spring Boot에서 엔티티를 직접 JSON으로 반환할 때, Jackson이 모든 필드를 직렬화하면서 발생하는 문제입니다.

### 순환 참조 구조

```
User
  └── enrollments (List<ClassEnroll>)
        └── classEntity (ClassEntity)
              └── enrollments (List<ClassEnroll>)
                    └── user (User)
                          └── enrollments... (무한 반복)
```

### 문제가 된 엔티티 관계

```java
// User.java
@OneToMany(mappedBy = "user")
private List<ClassEnroll> enrollments;

// ClassEntity.java
@OneToMany(mappedBy = "classEntity")
private List<ClassEnroll> enrollments;

// ClassEnroll.java
@ManyToOne
private ClassEntity classEntity;

@ManyToOne
private User user;
```

### 왜 무한 루프가 발생했는가?

1. API 응답으로 엔티티 반환 시 Jackson이 JSON 직렬화 시작
2. `User` 객체의 `enrollments` 필드 접근 → Lazy Loading으로 `ClassEnroll` 조회
3. 각 `ClassEnroll`의 `classEntity` 접근 → `ClassEntity` 조회
4. `ClassEntity`의 `enrollments` 접근 → 다시 `ClassEnroll` 조회
5. 각 `ClassEnroll`의 `user` 접근 → `User` 조회
6. 1번으로 돌아가서 무한 반복

---

## 해결 방법

### 적용한 해결책: `@JsonIgnore` 어노테이션 추가

양방향 참조 관계에서 한쪽에 `@JsonIgnore`를 추가하여 JSON 직렬화 시 해당 필드를 무시하도록 설정.

### 수정 예시

```java
// Before
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<ClassEnroll> enrollments;

// After
@JsonIgnore  // 추가
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<ClassEnroll> enrollments;
```

---

## 수정된 파일 목록

| 파일 | 수정된 필드 |
|------|-------------|
| `User.java` | `enrollments`, `workLogs`, `emailVerifications`, `authLogs`, `notifications` |
| `ClassEntity.java` | `user`, `curriculum`, `enrollments`, `submits` |
| `ClassEnroll.java` | `classEntity`, `user` |
| `Progress.java` | `user`, `classEntity`, `weeklySession` |
| `Curriculum.java` | `curriculumWeeks` |
| `CurriculumWeek.java` | `curriculum` |
| `WeeklySession.java` | `classInvite`, `activityMonitors` |
| `ClassInvite.java` | `classEntity`, `weeklySessions` |
| `ClassSubmit.java` | `task`, `classEntity` |
| `ActivityMonitor.java` | `weeklySession` |

---

## 대안적 해결 방법들

### 1. DTO 패턴 사용 (권장)

엔티티를 직접 반환하지 않고 DTO로 변환하여 반환:

```java
// DTO 정의
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    // 순환 참조 없이 필요한 필드만 포함
}

// Service에서 변환
public UserResponse getUser(Long id) {
    User user = userRepository.findById(id);
    return UserResponse.from(user);
}
```

### 2. `@JsonManagedReference` / `@JsonBackReference` 사용

양방향 관계에서 부모-자식 관계를 명시적으로 지정:

```java
// 부모 측
@JsonManagedReference
@OneToMany(mappedBy = "user")
private List<ClassEnroll> enrollments;

// 자식 측
@JsonBackReference
@ManyToOne
private User user;
```

### 3. `@JsonIdentityInfo` 사용

객체 ID를 기반으로 순환 참조 방지:

```java
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
public class User { ... }
```

---

## 권고사항

### 1. 엔티티 직접 반환 지양

```java
// Bad - 엔티티 직접 반환
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userRepository.findById(id);
}

// Good - DTO 사용
@GetMapping("/users/{id}")
public UserResponse getUser(@PathVariable Long id) {
    return userService.getUserResponse(id);
}
```

### 2. 새 엔티티 작성 시 체크리스트

- [ ] `@OneToMany` 관계에 `@JsonIgnore` 추가 검토
- [ ] `@ManyToOne` 관계에서 양방향인 경우 `@JsonIgnore` 추가 검토
- [ ] API 응답용 DTO 클래스 작성
- [ ] MapStruct 등 매퍼 라이브러리 활용

### 3. 로그 레벨 설정

개발 환경에서 SQL 로그 모니터링:

```properties
# application.properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## 참고 자료

- [Jackson Bidirectional Relationships](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
- [Spring Data JPA Best Practices](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [N+1 Problem in JPA](https://vladmihalcea.com/n-plus-1-query-problem/)

---

## 변경 이력

| 날짜 | 작성자 | 내용 |
|------|--------|------|
| 2024-12-19 | - | 최초 작성 및 문제 해결 |
