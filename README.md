아래는 기존 README 구조에 **팀원 소개 및 담당 파트 섹션**을 자연스럽게 추가한 버전입니다.
이름은 예시로 넣었으니 실제 팀원 정보로 수정해서 사용하시면 됩니다.

---

# 📚 Library Platform Backend

Spring Boot 기반으로 개발된 **도서관 관리 시스템 백엔드 데모 프로젝트**입니다.

## ✨ 주요 기능

* 사용자 / 관리자 인증 (JWT 기반)
* 도서 관리 (CRUD)
* 도서 재고 관리
* 도서 대여 및 반납 기능
* 도서 검색 및 페이지네이션
* OpenAI 기반 **AI 표지 이미지 생성 기능**

---

## 🗄️ ERD

🗄️ ERD 다이어그램
![ERD 다이어그램]<img width="667" height="552" alt="KakaoTalk_20251204_151813173" src="https://github.com/user-attachments/assets/6cd5c53d-cf0c-44df-9589-ebd3f79cb677" />

---


## 👨‍💻 팀원 및 역할

| 이름  | 역할                | 담당 기능                                    |
| --- | ----------------- | ---------------------------------------- |
| 김지윤 | Backend Developer | JWT 인증, 로그인/회원가입, Spring Security, 예외 처리 |
| 김채환 | Backend Developer | 도서 대여/반납, Loan 정책 및 API 개발, OpenAI 표지 생성, 이미지 저장 처리               |
| 오흥찬 | Backend Developer | 도서목록, 검색기능, 상세페이지 개발, H2 데이터베이스 스키마구성, API 명세 작성         |
| 황태민 | Backend Developer      | OpenAI 표지 생성, 이미지 저장 처리, Base64 변환       |



---

## 🔧 기술 스택

* Java 17
* Spring Boot 3.4.0
* Spring Data JPA
* Spring Security
* H2 Database
* Lombok
* JWT (JSON Web Token)
* OpenAI Java API

---

## 🚀 실행 방법

1. **레포지토리 클론**

   ```bash
   git clone https://github.com/your-username/library-platform-backend.git
   ```

2. **프로젝트 디렉토리 이동**

   ```bash
   cd library-platform-backend
   ```

3. **빌드**

   ```bash
   ./gradlew build
   ```

4. **실행**

   ```bash
   java -jar build/libs/demo-0.0.1-SNAPSHOT.jar
   ```

실행 후 서비스는 `http://localhost:8080` 에서 확인할 수 있습니다.

---

## 🔗 API Endpoints

### 인증

| Method | Endpoint      | 설명       |
| ------ | ------------- | -------- |
| POST   | /auth/signup  | 사용자 회원가입 |
| POST   | /admin/signup | 관리자 회원가입 |
| POST   | /auth/login   | 사용자 로그인  |
| POST   | /admin/login  | 관리자 로그인  |

### 도서(Book)

| Method | Endpoint            | 설명                                   |
| ------ | ------------------- | ------------------------------------ |
| GET    | /api/books          | 전체 도서 목록 조회 (page, sort, keyword 지원) |
| GET    | /api/books/search   | 도서 검색 (keyword)                      |
| GET    | /api/books/{bookId} | 개별 도서 상세 조회                          |

### 관리자용 도서 관리

| Method | Endpoint                    | 설명       |
| ------ | --------------------------- | -------- |
| POST   | /admin/books                | 도서 등록    |
| PATCH  | /admin/books/{bookId}       | 도서 수정    |
| DELETE | /admin/books/{bookId}       | 도서 삭제    |
| POST   | /admin/books/{bookId}/stock | 도서 재고 수정 |

### 대여(Loan)

| Method | Endpoint                   | 설명               |
| ------ | -------------------------- | ---------------- |
| POST   | /api/loans                 | 도서 대여            |
| GET    | /api/loans/my              | 로그인 사용자 대여 목록 조회 |
| PATCH  | /api/loans/{loanId}/return | 도서 반납            |

### AI 이미지 생성

| Method | Endpoint             | 설명               |
| ------ | -------------------- | ---------------- |
| POST   | /api/cover           | AI 표지 이미지 생성     |
| POST   | /api/images/generate | OpenAI 이미지 생성 요청 |

---

## 📌 개선 예정 기능

* Redis 기반 캐싱
* S3 파일 업로드
