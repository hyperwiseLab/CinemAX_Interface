# LLM 분석 결과 저장 기능 구현 가이드

## 📋 목차
1. [개요](#개요)
2. [현재 시스템 구조](#현재-시스템-구조)
3. [구현 요구사항](#구현-요구사항)
4. [구현 계획](#구현-계획)
5. [데이터베이스 설계](#데이터베이스-설계)
6. [API 명세](#api-명세)
7. [구현 체크리스트](#구현-체크리스트)

---

## 개요

### 목적
학생이 수업 중 과제를 수행하고 AI 분석을 요청했을 때, 분석 결과를 데이터베이스에 저장하여 향후 교수의 통계 분석 및 학생의 복습 기능에 활용

### 주요 기능
- LLM 분석 완료 시 결과를 T/F 형식으로 DB에 저장
- 입력 일시 저장
- LLM 전체 응답 텍스트 저장 (향후 업그레이드)
- 학생이 작성한 프롬프트 전체 저장 (복습 히스토리 활용)

### 사용자 플로우
```
1. 학생 수업 중 과제 수행
   ↓
2. "실행 > AI 분석 및 제출" 버튼 클릭
   ↓
3. AI 분석 결과 표시 (프론트엔드)
   ↓
4. "제출" 버튼 클릭
   ↓
5. DB 저장 API 호출
   ↓
6. 분석 결과 저장 완료
```

---

## 현재 시스템 구조

### 1. AI 분석 API 엔드포인트

**위치**: `/src/main/java/com/cinemax/infrastructure/gemini/controller/GeminiController.java`

#### 과제 피드백 생성
```java
@PostMapping("/feedback")
@PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'STUDENT')")
public ResponseEntity<ApiResponse<String>> generateFeedback(
    @RequestParam String studentCode,           // 학생 코드
    @RequestParam String expectedOutput,        // 기대 출력값
    @RequestParam(required = false) String rubric  // 채점 기준
)
```

#### 코드 리뷰
```java
@PostMapping("/code-review")
public ResponseEntity<ApiResponse<String>> reviewCode(
    @RequestParam String code,          // 소스 코드
    @RequestParam String language       // 프로그래밍 언어
)
```

### 2. Gemini 서비스 구현

**위치**: `/src/main/java/com/cinemax/infrastructure/gemini/service/impl/GeminiServiceImpl.java`

#### 피드백 생성 로직
```java
@Override
public String generateFeedback(String studentCode, String expectedOutput, String rubric) {
    // 프롬프트 구성
    StringBuilder promptBuilder = new StringBuilder();
    promptBuilder.append("학생이 작성한 코드에 대한 피드백을 생성해주세요.\n\n");
    promptBuilder.append("학생 코드:\n```\n").append(studentCode).append("\n```\n\n");
    promptBuilder.append("기대 출력:\n").append(expectedOutput).append("\n\n");

    if (rubric != null && !rubric.isBlank()) {
        promptBuilder.append("채점 기준:\n").append(rubric).append("\n\n");
    }

    promptBuilder.append("""
        다음 항목을 포함하여 피드백을 작성해주세요:
        1. 코드가 요구사항을 충족하는지 여부
        2. 잘한 점
        3. 개선이 필요한 점
        4. 구체적인 개선 제안
        5. 학습 조언
    """);

    // Gemini API 호출 (최대 8192 토큰)
    VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
            .withMaxOutputTokens(8192)
            .build();

    Prompt prompt = new Prompt(promptBuilder.toString(), options);
    ChatResponse response = chatModel.call(prompt);

    return response.getResult().getOutput().getContent();
}
```

### 3. 현재 데이터 모델

#### 과제 제출 엔티티 (ClassSubmit)
**위치**: `/src/main/java/com/cinemax/domain/classes/entity/ClassSubmit.java`

```java
@Entity
@Table(name = "TBL_CLASS_SUBMIT")
public class ClassSubmit extends BaseTimeEntity {
    @Id
    private Long submitId;

    private Long taskId;              // 과제 ID
    private Long classId;             // 수업 ID
    private Long cycleId;             // 사이클 ID
    private Long weeklySessionId;     // 주차 수업 ID

    private LocalDateTime submitAt;   // 제출 시간
    private Boolean result;           // 전체 통과 여부
    private BigDecimal score;         // 점수
    private String detailJson;        // 상세 결과 (JSON)

    private Boolean isFirstEval;      // 첫 제출 여부
    private Integer submitNum;        // 제출 횟수
}
```

#### 피드백 엔티티 (Feedback)
**위치**: `/src/main/java/com/cinemax/domain/feedback/entity/Feedback.java`

```java
@Entity
@Table(name = "TBL_FEEDBACK")
public class Feedback extends BaseTimeEntity {
    @Id
    private Long feedbackId;

    private Long taskId;
    private Long cycleId;

    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;

    private String title;
    private String subTitle;
    private String feedbackContent;   // 고정된 피드백 내용

    private String characterImg;
    private String characterPath;
}
```

**현재 문제점**:
- `Feedback` 엔티티는 **UI용 고정 피드백** 저장용
- **동적 AI 분석 결과**를 저장하는 구조가 없음
- 학생별 분석 히스토리 관리 불가

---

## 구현 요구사항

### 필수 기능
1. ✅ LLM 분석 결과를 T/F 형식으로 저장
2. ✅ 분석 요청 시간 (입력 일시) 저장
3. ✅ 학생 ID, 과제 ID 연관 관계 설정

### 향후 업그레이드 대비 기능
4. ✅ LLM 전체 응답 텍스트 저장 (`@Lob` 컬럼)
5. ✅ 학생이 제출한 프롬프트 전체 저장 (복습 히스토리용)
6. ✅ 토큰 사용량 저장 (통계 분석용)

### 분석 결과 T/F 항목 (예시)
- `requirementsMet`: 과제 요구사항 충족 여부
- `codeQualityPass`: 코드 품질 기준 통과 여부
- `hasLogicError`: 논리 오류 존재 여부
- `hasSecurityIssue`: 보안 이슈 존재 여부
- `needsImprovement`: 개선 필요 여부

---

## 구현 계획

### Phase 1: 데이터베이스 설계
- [ ] `AnalysisResult` 엔티티 생성
- [ ] `AnalysisResultRepository` 생성
- [ ] DB 마이그레이션 스크립트 작성 (필요 시)

### Phase 2: 서비스 레이어 구현
- [ ] `AnalysisResultService` 인터페이스 정의
- [ ] `AnalysisResultServiceImpl` 구현
- [ ] Gemini 응답 파싱 로직 추가 (T/F 추출)

### Phase 3: API 레이어 구현
- [ ] `AnalysisResultController` 생성
- [ ] POST `/api/v1/analysis-results` 엔드포인트 구현
- [ ] GET `/api/v1/analysis-results/{userId}/history` 구현

### Phase 4: 기존 코드 통합
- [ ] `GeminiService`와 연동
- [ ] `ClassSubmit`와 연관 관계 설정

### Phase 5: 테스트 및 검증
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] API 문서화 (Swagger)

---

## 데이터베이스 설계

### 새로운 엔티티: AnalysisResult

```java
@Entity
@Table(name = "TBL_ANALYSIS_RESULT")
public class AnalysisResult extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    // 연관 관계
    private Long userId;              // 학생 ID
    private Long taskId;              // 과제 ID
    private Long submitId;            // 제출 ID (ClassSubmit 연관)
    private Long classId;             // 수업 ID
    private Long cycleId;             // 사이클 ID

    // 분석 요청 정보
    @Lob
    @Column(columnDefinition = "TEXT")
    private String studentPrompt;     // 학생이 작성한 프롬프트 전체

    @Lob
    @Column(columnDefinition = "TEXT")
    private String studentCode;       // 제출한 코드

    private LocalDateTime requestedAt; // 분석 요청 시간

    // LLM 응답 정보
    @Lob
    @Column(columnDefinition = "TEXT")
    private String llmResponse;       // LLM 전체 응답 텍스트

    private LocalDateTime analyzedAt;  // 분석 완료 시간

    // 분석 결과 (T/F 플래그)
    private Boolean requirementsMet;   // 요구사항 충족 여부
    private Boolean codeQualityPass;   // 코드 품질 통과
    private Boolean hasLogicError;     // 논리 오류 존재
    private Boolean hasSecurityIssue;  // 보안 이슈 존재
    private Boolean needsImprovement;  // 개선 필요

    // 메타 정보
    private String modelVersion;       // 사용한 LLM 모델 (예: gemini-2.5-flash)
    private Integer promptTokens;      // 프롬프트 토큰 수
    private Integer completionTokens;  // 응답 토큰 수
    private Integer totalTokens;       // 전체 토큰 수

    // 추가 정보
    @Column(columnDefinition = "TEXT")
    private String additionalNotes;    // 추가 메모

    // 연관 관계 (선택사항)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMIT_ID", insertable = false, updatable = false)
    private ClassSubmit classSubmit;
}
```

### ERD 관계
```
User (사용자)
  ↓ 1:N
AnalysisResult (AI 분석 결과)
  ↓ N:1
Task (과제)
  ↓ 1:N
ClassSubmit (제출 이력)
```

---

## API 명세

### 1. 분석 결과 저장

#### Endpoint
```
POST /api/v1/analysis-results
```

#### Request Body
```json
{
  "userId": 123,
  "taskId": 456,
  "submitId": 789,
  "classId": 101,
  "cycleId": 202,
  "studentPrompt": "학생이 작성한 전체 프롬프트...",
  "studentCode": "print('Hello World')",
  "llmResponse": "LLM이 생성한 전체 피드백 텍스트...",
  "requirementsMet": true,
  "codeQualityPass": true,
  "hasLogicError": false,
  "hasSecurityIssue": false,
  "needsImprovement": false,
  "modelVersion": "gemini-2.5-flash",
  "promptTokens": 512,
  "completionTokens": 1024,
  "totalTokens": 1536
}
```

#### Response
```json
{
  "status": "success",
  "message": "분석 결과가 저장되었습니다.",
  "data": {
    "analysisId": 999,
    "requestedAt": "2025-12-09T10:30:00",
    "analyzedAt": "2025-12-09T10:30:05"
  }
}
```

---

### 2. 분석 히스토리 조회 (학생용 복습 기능)

#### Endpoint
```
GET /api/v1/analysis-results/user/{userId}/history
```

#### Query Parameters
- `taskId` (optional): 특정 과제 필터링
- `cycleId` (optional): 특정 사이클 필터링
- `page` (default: 0)
- `size` (default: 20)

#### Response
```json
{
  "status": "success",
  "data": {
    "content": [
      {
        "analysisId": 999,
        "taskTitle": "파이썬 기초: Hello World",
        "requestedAt": "2025-12-09T10:30:00",
        "requirementsMet": true,
        "codeQualityPass": true,
        "llmResponseSummary": "코드가 요구사항을 충족하며..."
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "currentPage": 0
  }
}
```

---

### 3. 특정 분석 결과 상세 조회

#### Endpoint
```
GET /api/v1/analysis-results/{analysisId}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "analysisId": 999,
    "userId": 123,
    "taskId": 456,
    "studentPrompt": "전체 프롬프트...",
    "studentCode": "print('Hello World')",
    "llmResponse": "전체 LLM 응답...",
    "requirementsMet": true,
    "codeQualityPass": true,
    "hasLogicError": false,
    "hasSecurityIssue": false,
    "needsImprovement": false,
    "modelVersion": "gemini-2.5-flash",
    "promptTokens": 512,
    "completionTokens": 1024,
    "totalTokens": 1536,
    "requestedAt": "2025-12-09T10:30:00",
    "analyzedAt": "2025-12-09T10:30:05"
  }
}
```

---

### 4. 교수용 통계 API (향후 구현)

#### Endpoint
```
GET /api/v1/analysis-results/statistics
```

#### Query Parameters
- `classId`: 수업 ID
- `cycleId`: 사이클 ID
- `startDate`: 시작일
- `endDate`: 종료일

#### Response
```json
{
  "status": "success",
  "data": {
    "totalAnalyses": 500,
    "requirementsMetRate": 0.85,
    "codeQualityPassRate": 0.72,
    "averageTokenUsage": 1200,
    "topIssues": [
      {
        "issue": "논리 오류",
        "count": 150
      }
    ]
  }
}
```

---

## 구현 체크리스트

### 백엔드 구현

#### 1. 엔티티 계층
- [ ] `AnalysisResult` 엔티티 생성
- [ ] `BaseTimeEntity` 상속 확인
- [ ] 연관 관계 설정 (`User`, `Task`, `ClassSubmit`)
- [ ] 인덱스 추가 (`userId`, `taskId`, `submitId`)

#### 2. Repository 계층
- [ ] `AnalysisResultRepository` 인터페이스 생성
- [ ] 커스텀 쿼리 메서드 정의
  - `findByUserId(Long userId, Pageable pageable)`
  - `findByUserIdAndTaskId(Long userId, Long taskId)`
  - `findBySubmitId(Long submitId)`

#### 3. Service 계층
- [ ] `AnalysisResultService` 인터페이스 정의
- [ ] `AnalysisResultServiceImpl` 구현
- [ ] DTO 클래스 생성
  - `AnalysisResultRequest`
  - `AnalysisResultResponse`
  - `AnalysisHistoryResponse`
- [ ] LLM 응답 파싱 로직 구현 (T/F 추출)

#### 4. Controller 계층
- [ ] `AnalysisResultController` 생성
- [ ] API 엔드포인트 구현
  - `POST /api/v1/analysis-results` (저장)
  - `GET /api/v1/analysis-results/{id}` (조회)
  - `GET /api/v1/analysis-results/user/{userId}/history` (히스토리)
- [ ] 권한 검증 (`@PreAuthorize`)
- [ ] 예외 처리

#### 5. 기존 코드 통합
- [ ] `GeminiServiceImpl` 수정
  - 분석 완료 시 `AnalysisResult` 자동 저장 옵션
  - 또는 별도 저장 API 호출 방식
- [ ] `CodeSubmitService`와 연동
  - 제출 시 `submitId` 전달

#### 6. 테스트
- [ ] `AnalysisResultServiceTest` 작성
- [ ] `AnalysisResultControllerTest` 작성
- [ ] 통합 테스트 시나리오
  1. 분석 요청 → 결과 저장
  2. 히스토리 조회
  3. 중복 저장 방지

#### 7. 문서화
- [ ] Swagger 문서 추가
- [ ] API 사용 가이드 작성

---

### 프론트엔드 구현 (참고사항)

#### 1. 분석 요청 플로우
```javascript
// 1. AI 분석 실행
const analyzeCode = async () => {
  const response = await fetch('/api/v1/gemini/feedback', {
    method: 'POST',
    body: JSON.stringify({
      studentCode: editorCode,
      expectedOutput: task.expectedOutput,
      rubric: task.rubric
    })
  });

  const result = await response.json();
  setAnalysisResult(result.data); // 화면에 표시
};

// 2. 제출 버튼 클릭 시 DB 저장
const submitAnalysis = async () => {
  await fetch('/api/v1/analysis-results', {
    method: 'POST',
    body: JSON.stringify({
      userId: currentUser.id,
      taskId: task.id,
      submitId: currentSubmit.id,
      studentCode: editorCode,
      llmResponse: analysisResult.content,
      // T/F 플래그는 백엔드에서 파싱
    })
  });

  alert('분석 결과가 저장되었습니다!');
};
```

#### 2. 복습 히스토리 화면
```javascript
const AnalysisHistory = () => {
  const [history, setHistory] = useState([]);

  useEffect(() => {
    fetch(`/api/v1/analysis-results/user/${userId}/history`)
      .then(res => res.json())
      .then(data => setHistory(data.data.content));
  }, []);

  return (
    <div>
      {history.map(item => (
        <Card key={item.analysisId}>
          <h3>{item.taskTitle}</h3>
          <p>분석 일시: {item.requestedAt}</p>
          <p>요구사항 충족: {item.requirementsMet ? '✅' : '❌'}</p>
          <Button onClick={() => viewDetail(item.analysisId)}>
            상세보기
          </Button>
        </Card>
      ))}
    </div>
  );
};
```

---

## 주요 파일 경로

### 기존 파일 (참고용)
```
/src/main/java/com/cinemax/
├── infrastructure/gemini/
│   ├── controller/GeminiController.java
│   ├── service/GeminiService.java
│   └── service/impl/GeminiServiceImpl.java
├── domain/
│   ├── classes/entity/ClassSubmit.java
│   ├── feedback/entity/Feedback.java
│   └── task/entity/Task.java
└── core/handler/GlobalExceptionHandler.java
```

### 신규 생성 파일
```
/src/main/java/com/cinemax/domain/analysis/
├── entity/AnalysisResult.java
├── repository/AnalysisResultRepository.java
├── service/AnalysisResultService.java
├── service/impl/AnalysisResultServiceImpl.java
├── controller/AnalysisResultController.java
└── dto/
    ├── AnalysisResultRequest.java
    ├── AnalysisResultResponse.java
    └── AnalysisHistoryResponse.java
```

---

## 구현 시 고려사항

### 1. LLM 응답 파싱 전략

#### Option A: 구조화된 응답 요청
Gemini에게 JSON 형식으로 응답을 요청
```java
String prompt = """
    다음 형식의 JSON으로 응답해주세요:
    {
        "requirementsMet": true/false,
        "codeQualityPass": true/false,
        "hasLogicError": true/false,
        "feedback": "상세 피드백..."
    }
    """;
```

#### Option B: 텍스트 파싱
정규식 또는 키워드 기반으로 T/F 추출
```java
private Boolean parseRequirementsMet(String response) {
    return response.contains("요구사항을 충족") &&
           !response.contains("충족하지 않");
}
```

**추천**: Option A (구조화된 응답)

---

### 2. 성능 최적화

#### 인덱스 전략
```sql
CREATE INDEX idx_analysis_user_task ON TBL_ANALYSIS_RESULT(user_id, task_id);
CREATE INDEX idx_analysis_submit ON TBL_ANALYSIS_RESULT(submit_id);
CREATE INDEX idx_analysis_created_at ON TBL_ANALYSIS_RESULT(created_at);
```

#### 페이징 처리
```java
@GetMapping("/user/{userId}/history")
public ResponseEntity<Page<AnalysisHistoryResponse>> getHistory(
    @PathVariable Long userId,
    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable pageable
) {
    return ResponseEntity.ok(analysisResultService.getUserHistory(userId, pageable));
}
```

---

### 3. 보안 고려사항

#### 권한 검증
```java
@GetMapping("/user/{userId}/history")
@PreAuthorize("hasRole('STUDENT') and #userId == authentication.principal.userId or hasRole('PROFESSOR')")
public ResponseEntity<?> getHistory(@PathVariable Long userId) {
    // 본인 또는 담당 교수만 조회 가능
}
```

#### 민감 정보 보호
- 학생 코드 및 프롬프트는 암호화 고려
- HTTPS 통신 필수

---

### 4. 확장 가능성

#### 추후 추가 가능한 분석 항목
- `performanceScore`: 성능 점수
- `readabilityScore`: 가독성 점수
- `complexityScore`: 복잡도 점수
- `testCoverage`: 테스트 커버리지

#### 다중 LLM 지원
```java
@Enumerated(EnumType.STRING)
private LlmProvider provider; // GEMINI, OPENAI, CLAUDE
```

---

## 구현 완료 사항

✅ **Phase 1: 데이터베이스 설계**
- AnalysisResult 엔티티 생성 완료
- AnalysisResultRepository 생성 완료
- 인덱스 및 연관 관계 설정 완료

✅ **Phase 2: 서비스 레이어**
- AnalysisResultService 인터페이스 정의
- AnalysisResultServiceImpl 구현 완료
- 통계 조회 메서드 구현 완료

✅ **Phase 3: API 레이어**
- AnalysisResultController 생성 완료
- 9개 엔드포인트 구현 완료
- Swagger 문서화 완료

✅ **Phase 4: 기존 코드 통합**
- GeminiService에 `generateStructuredAnalysis` 메서드 추가
- JSON 파싱 로직 구현
- GeminiController에 `/structured-analysis` 엔드포인트 추가

✅ **Phase 5: 예외 처리**
- AnalysisResultNotFoundException 구현
- InvalidAnalysisRequestException 구현
- GlobalExceptionHandler에 예외 핸들러 추가

---

## 생성된 파일 목록

### 엔티티 및 Repository
```
/src/main/java/com/cinemax/domain/analysis/
├── entity/AnalysisResult.java
├── repository/AnalysisResultRepository.java
```

### DTO
```
/src/main/java/com/cinemax/domain/analysis/dto/
├── AnalysisResultRequest.java
├── AnalysisResultResponse.java
├── AnalysisHistoryResponse.java
└── AnalysisStatisticsResponse.java
```

### 서비스
```
/src/main/java/com/cinemax/domain/analysis/service/
├── AnalysisResultService.java
└── impl/AnalysisResultServiceImpl.java
```

### 컨트롤러
```
/src/main/java/com/cinemax/domain/analysis/controller/
└── AnalysisResultController.java
```

### 예외
```
/src/main/java/com/cinemax/domain/analysis/exception/
├── AnalysisResultNotFoundException.java
└── InvalidAnalysisRequestException.java
```

### Gemini 관련 수정
```
/src/main/java/com/cinemax/infrastructure/gemini/
├── dto/response/StructuredAnalysisResponse.java (신규)
├── service/GeminiService.java (수정)
├── service/impl/GeminiServiceImpl.java (수정)
└── controller/GeminiController.java (수정)
```

---

## API 엔드포인트 요약

### 1. 분석 결과 저장
```
POST /api/v1/analysis-results
Content-Type: application/json

Request Body:
{
  "userId": 123,
  "taskId": 456,
  "submitId": 789,
  "studentCode": "print('Hello')",
  "llmResponse": "분석 결과...",
  "requirementsMet": true,
  "codeQualityPass": true,
  ...
}
```

### 2. 구조화된 분석 생성 (Gemini)
```
POST /api/v1/gemini/structured-analysis
Content-Type: application/x-www-form-urlencoded

Parameters:
- studentCode: 학생 코드
- expectedOutput: 기대 출력
- rubric (optional): 채점 기준

Response:
{
  "requirementsMet": true,
  "codeQualityPass": true,
  "hasLogicError": false,
  "feedback": "전체 피드백...",
  "strengths": "잘한 점...",
  "improvements": "개선점...",
  "advice": "학습 조언..."
}
```

### 3. 사용자 히스토리 조회 (복습 기능)
```
GET /api/v1/analysis-results/user/{userId}/history?page=0&size=20
```

### 4. 과제별 통계 조회 (교수용)
```
GET /api/v1/analysis-results/statistics/task/{taskId}

Response:
{
  "totalAnalyses": 100,
  "requirementsMetRate": 0.85,
  "codeQualityPassRate": 0.72,
  "averageTokenUsage": 1200,
  "topIssues": [...]
}
```

---

## 사용 예시

### 프론트엔드 통합 예시

```javascript
// 1. AI 분석 요청 (구조화된 응답)
const analyzeCode = async () => {
  const formData = new URLSearchParams();
  formData.append('studentCode', editorCode);
  formData.append('expectedOutput', task.expectedOutput);
  formData.append('rubric', task.rubric);

  const response = await fetch('/api/v1/gemini/structured-analysis', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: formData
  });

  const result = await response.json();

  // result.data에 StructuredAnalysisResponse 객체
  console.log('요구사항 충족:', result.data.requirementsMet);
  console.log('피드백:', result.data.feedback);

  return result.data;
};

// 2. 분석 결과 DB 저장
const saveAnalysisResult = async (analysisData) => {
  const response = await fetch('/api/v1/analysis-results', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      userId: currentUser.id,
      taskId: task.id,
      submitId: submitId,
      studentCode: editorCode,
      llmResponse: analysisData.feedback,
      requirementsMet: analysisData.requirementsMet,
      codeQualityPass: analysisData.codeQualityPass,
      hasLogicError: analysisData.hasLogicError,
      hasSecurityIssue: analysisData.hasSecurityIssue,
      needsImprovement: analysisData.needsImprovement,
      modelVersion: 'gemini-2.5-flash'
    })
  });

  const result = await response.json();
  console.log('저장된 분석 ID:', result.data.analysisId);
};

// 3. 전체 플로우
const handleAnalyzeAndSave = async () => {
  try {
    // Step 1: AI 분석
    const analysisData = await analyzeCode();

    // Step 2: 화면에 결과 표시
    displayAnalysisResult(analysisData);

    // Step 3: 사용자가 제출 버튼 클릭 시 DB 저장
    await saveAnalysisResult(analysisData);

    alert('분석 결과가 저장되었습니다!');
  } catch (error) {
    console.error('분석 또는 저장 실패:', error);
  }
};
```

---

## 다음 단계

### 1. 데이터베이스 마이그레이션
```sql
-- TBL_ANALYSIS_RESULT 테이블이 자동 생성되도록 설정되어 있으나,
-- 프로덕션 환경에서는 수동 마이그레이션 스크립트 작성 권장

CREATE TABLE TBL_ANALYSIS_RESULT (
    ANALYSIS_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    TASK_ID BIGINT NOT NULL,
    SUBMIT_ID BIGINT,
    CLASS_ID BIGINT,
    CYCLE_ID BIGINT,
    STUDENT_PROMPT TEXT,
    STUDENT_CODE TEXT NOT NULL,
    REQUESTED_AT DATETIME NOT NULL,
    LLM_RESPONSE TEXT NOT NULL,
    ANALYZED_AT DATETIME,
    REQUIREMENTS_MET BOOLEAN,
    CODE_QUALITY_PASS BOOLEAN,
    HAS_LOGIC_ERROR BOOLEAN,
    HAS_SECURITY_ISSUE BOOLEAN,
    NEEDS_IMPROVEMENT BOOLEAN,
    MODEL_VERSION VARCHAR(100),
    PROMPT_TOKENS INT,
    COMPLETION_TOKENS INT,
    TOTAL_TOKENS INT,
    ADDITIONAL_NOTES TEXT,
    CREATE_DT DATETIME NOT NULL,
    UPDATE_DT DATETIME NOT NULL,
    INDEX idx_analysis_user_task (USER_ID, TASK_ID),
    INDEX idx_analysis_submit (SUBMIT_ID),
    INDEX idx_analysis_created (CREATE_DT)
);
```

### 2. 프론트엔드 구현
- "AI 분석 및 제출" 버튼 구현
- 분석 결과 표시 UI 구현
- 복습 히스토리 페이지 구현
- 교수용 통계 대시보드 구현

### 3. 테스트
- 단위 테스트 작성
- 통합 테스트 작성
- API 테스트 (Postman/Swagger)

### 4. 최적화
- 대용량 데이터 처리 시 성능 모니터링
- 캐싱 전략 수립 (Redis)
- 비동기 처리 고려 (Kafka/RabbitMQ)

---

## 문의 및 피드백

구현이 완료되었습니다! 추가 기능이나 개선 사항이 필요하면 언제든지 문의해주세요.