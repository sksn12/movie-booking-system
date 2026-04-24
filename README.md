
# 🎬 GUI 기반 영화 예매 시스템 (Movie Booking System)

Java Swing을 활용한 사용자 친화적 인터페이스와 **3-Tier Architecture**를 적용한 영화 예매 시스템입니다. 
<br>데이터 정합성과 시스템 성능을 고려하여 설계되었습니다.

---

## 주요 기능
* **로그인 시스템**: 회원 정보를 바탕으로 한 사용자 인증 
* **영화 조회 및 예매**: 상영관별 시간표 조회, 다중 좌석 선택, 예매 번호 발급 
* **예매 내역 관리**: 관람 예정 및 지난 관람 내역 조회, 실시간 예매 취소 기능 
* **데이터 필터링**: 현재 시각을 기준으로 상영 일자 및 시간대별 예약 가능 여부 검증

---

## Tech Stack
* **Language**: Java 11
* **Library**: Java Swing (GUI), Lombok
* **Architecture**: 3-Tier Architecture (Presentation, Business, Data Layer)
* **Storage**: File System (.txt) 기반 데이터 관리
* **Script**: Python (테스트용 더미 데이터 생성 자동화) 

---

## 시스템 아키텍처
본 프로젝트는 코드의 책임을 명확히 하고 유지보수성을 높이기 위해 **3-Tier Architecture**를 기반으로 설계되었습니다.<br>

<img width="1116" height="457" alt="image" src="https://github.com/user-attachments/assets/7aa882f6-f3c9-40cb-a628-32f405a1ce9c" />
<br>

* **Presentation Layer**: Java Swing 기반의 직관적인 UI 제공
* **Business Layer**: 서비스 로직 처리 및 유효성 검증 (`Service` 계층)
* **Data Layer**: 데이터 액세스(`DAO`) 및 인메모리 저장소(`DataRepository`) 관리

---

## 핵심 기술적 해결책

### 1. 성능 최적화: In-Memory Caching
기존의 빈번한 파일 I/O로 인한 성능 저하를 해결하기 위해 DataRepository(Singleton) 패턴을 도입했습니다. 
* 프로그램 시작 시 데이터를 일괄 로드하여 메모리에 저장
* 조회 시 메모리 데이터를 활용하고, 변경(예매/취소) 발생 시에만 파일과 동기화하여 효율성 극대화 

### 2. 동시성 문제 해결
다중 사용자의 동시 접근 상황에서 데이터 무결성을 보장하기 위해 다음 기술을 적용했습니다. 
* `synchronized` 키워드를 통한 예매/취소 로직의 원자성 확보
* `ConcurrentHashMap` 및 `synchronizedList`를 사용하여 컬렉션 차원의 Thread-safe 보장 

### 3. 구조적 개선 및 예외 처리
* **SessionManager**: 데이터 저장소와 로그인 세션의 책임을 분리하여 보안성 향상
* **Custom Exceptions**: 상황별 예외(좌석 중복, 로그인 실패 등)를 세분화하여 명확한 에러 핸들링 구현

---

## 프로젝트 구조
```text
src
 └── com.project
      ├── main
      │    └── Main.java                        # 프로그램 시작, 데이터 초기 로드 
      ├── common
      │    ├── FilePath.java                    # TXT 파일 경로 상수 
      │    ├── FileUtil.java                    # 파일 Read/Write 처리
      │    └── DataRepository.java              # In-Memory 데이터 저장소
      ├── member
      │    ├── MemberDTO.java                   # 회원 정보 객체 
      │    ├── MemberDAO.java                   # member.txt 데이터 동기화
      │    └── MemberService.java               # 로그인 비즈니스 로직 
      ├── movie
      │    ├── MovieDTO.java                    # 영화 정보 객체 
      │    ├── MovieDAO.java                    # movie 데이터 동기화
      │    └── MovieService.java                # 영화 조회/필터링 로직
      ├── book
      │    ├── BookDTO.java                     # 예매 정보 객체
      │    ├── BookDAO.java                     # book.txt 데이터 동기화 
      │    └── BookService.java                 # 예매/조회/취소 로직 
      ├── exception
      │    ├── BookCancelException.java         # 예매 취소 예외 
      │    ├── InvalidInputException.java       # 잘못된 입력 예외 
      │    ├── LoginFailedException.java        # 로그인 실패 예외
      │    ├── MovieNotSelectableException.java # 영화 선택 불가 예외
      │    ├── PastMovieBookingException.java   # 지난 영화 예매 예외 
      │    └── SeatAlreadyBookedException.java  # 좌석 중복 예매 예외
      └── resource                              # 데이터 저장 (.txt)
```
---
## 시연

<img width="1408" height="974" alt="스크린샷 2026-04-24 101819" src="https://github.com/user-attachments/assets/bb0236fc-dbd8-4512-a0ac-ee63cf40ca4a" />
<img width="1412" height="968" alt="스크린샷 2026-04-24 101937" src="https://github.com/user-attachments/assets/08c80ba9-faa1-423e-adfc-422f9dd25c56" />
<img width="1403" height="969" alt="스크린샷 2026-04-24 102003" src="https://github.com/user-attachments/assets/728dd05d-112d-44ca-bc6d-7ded98b44d7c" />
<img width="1408" height="969" alt="스크린샷 2026-04-24 102108" src="https://github.com/user-attachments/assets/761c68b5-8e2c-4f81-b002-a702085eb258" />
<img width="1408" height="971" alt="스크린샷 2026-04-24 102126" src="https://github.com/user-attachments/assets/e6594bfa-96bf-4665-a8c3-f901303660cb" />
<img width="1402" height="966" alt="스크린샷 2026-04-24 102207" src="https://github.com/user-attachments/assets/25171fc1-2790-4ded-8475-7592927cd239" />
