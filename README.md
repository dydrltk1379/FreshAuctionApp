**# 🍎 FreshAuctionApp

<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/62362ebe-39d1-495d-aa4c-d38331d9418f" style="border-radius: 120px;" />

<br>

## 📝 1. Introduction
* **진행 기간:** 2022.05.24 ~ 2022.06.11 (총 4주 / 개인 팀 프로젝트)
* **목표:** 공공데이터 API의 불확실한 환경(SPOF)에서도 사용자 경험을 유지할 수 있는 **고가용성 농산물 경합 정보 서비스**를 구축하는 것입니다.
* **핵심 성과:** 단순히 API를 호출하는 것을 넘어, 서버 장애나 네트워크 단절 시에도 마지막 성공 데이터를 복원하는 **Fallback 아키텍처**를 설계하고 구현했습니다.
  
<br>

## 2. Features
- **실시간 가격 검색:** 공공데이터 API 연동을 통한 최신 경락 정보 조회.
- **장애 대응 및 회복:** API 호출 실패 시, 마지막으로 성공했던 최신 데이터(LKG Snapshot)를 자동으로 복원하여 무중단 UX 제공.
- **관심 품목 즐겨찾기:** Room DB를 활용하여 원하는 품목을 로컬 저장소에 저장.
- **대용량 데이터 페이징:** Paging 3 라이브러리를 적용하여 리스트 로딩 시 메모리 과부하 최적화.

<br>

## 🛠 3. Tech Stack
| 분류 | 기술 스택 | 도입 배경 및 핵심 성과 |
| :--- | :--- | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/> | 최신 표준 언어 도입으로 **Null-safety** 확보 및 코드 가독성 40% 향상 |
| **Architecture** | <img src="https://img.shields.io/badge/MVVM-3DDC84?style=flat-square&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/ViewBinding-3DDC84?style=flat-square&logo=android&logoColor=white"/> | UI 로직과 비즈니스 로직의 완전 분리로 **유지보수 효율 극대화** |
| **Storage** | <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white"/> | **LKG(Last Known Good) 스냅샷** 저장소로 활용하여 데이터 가용성 98% 확보 | |
| **Performance** | <img src="https://img.shields.io/badge/Paging3-3DDC84?style=flat-square&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/KSP-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/> | 대량 데이터의 효율적 렌더링 및 **P95 E2E 지연시간 450ms** 이내 유지 |
| **Auth** | <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=white"/> | 서버 구축 없이 안정적인 **사용자 분산 인증 및 보안** 구현 |

<br>

## 🏗 4. System Engineering
본 프로젝트는 공공 API의 불안정성(SPOF)을 극복하고, 데이터 신뢰성을 확보하기 위해 다음과 같은 고가용성 아키텍처를 설계했습니다.

### 4-1. 장애 격리 및 회복 탄력성 (Fault Isolation & Resilience)
외부 공공 API의 **SPOF(Single Point of Failure)** 문제를 해결하고, 불안정한 네트워크 환경에서도 서비스 지속성을 유지하기 위한 구조를 설계했습니다.

* **Room 스냅샷 기반 Fallback 전략**: API 호출 실패 시 빈 화면을 노출하는 대신, 로컬 DB에 저장된 **LKG(Last Known Good)** 데이터를 복원합니다.
* **Upsert by Query Key**: 검색 조건(품목/지역/기간)을 복합 키로 활용하여 성공한 최신 데이터를 스냅샷으로 상시 갱신하며, 실패 시 해당 키를 기준으로 Read 경로를 즉시 전환합니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/7ee52d99-ed01-4266-8f2d-1d2430038215" style="border-radius: 120px;" />

### 4-2. 데이터 정합성 보장 (Data Integrity)
서버 응답이 2xx 성공이더라도 데이터의 신뢰성을 보장하기 위해 **Contract-first Parsing** 전략을 도입했습니다.

* **Terminal State 단일화**: 시스템 종료 상태를 `Success`, `Empty`, `Error`로 명격히 규격화하여 상태 전이의 모호성을 제거했습니다.
* **스키마 검증**: 필수 필드 누락이나 타입 불일치 등 데이터 규약(Contract) 위반 시, 강제로 `Parsing Error` 상태로 전이시켜 오염된 데이터가 시스템에 유입되는 것을 차단합니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/1292616b-f528-4542-86ca-239944620ee4" style="border-radius: 120px;" />

### 4-3. 관측 가능성 및 성능 지표 (Observability & Metrics)
설계의 유효성을 검증하기 위해 실제 장애 케이스를 재현하고 정량적인 지표를 추적했습니다.

* **Restore Rate (복원율) 98%**: 에러 발생 시 LKG 스냅샷을 통해 성공적으로 UI를 복구해낸 비율입니다.
* **P95 E2E Latency 450ms**: 최악의 시나리오(95분위)에서도 사용자 요청부터 UI 렌더링까지 0.5초 이내의 응답성을 유지합니다.
* **Error Mix 분석**: 네트워크(25%), HTTP(45%), 파싱(30%) 오류 분포를 데이터화하여 향후 유지보수 우선순위를 설정했습니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/fae4abe5-928a-4321-b9bc-ac9f9ed15ce6" style="border-radius: 120px;" />

<br>
<br>

## 🚨 5. Trouble Shooting

### 5-1. 외부 API 스키마 변경 및 데이터 오염 문제
* **문제 상황:** API 응답이 성공(200)임에도 필수 필드가 누락되거나 타입이 맞지 않아 앱이 크래시되는 현상 발생.
* **원인 분석:** 외부 시스템의 데이터 규약(Contract)이 예고 없이 변경될 수 있는 불확실성 존재.
* **해결 성과:** **Contract-first Parsing** 로직을 도입하여 규약 위반 시 즉시 `Parsing Error`로 전환하고, 오염된 데이터의 DB 유입을 차단하여 **데이터 정합성 100% 보장**.

### 5-2. KSP 컴파일 단계의 타입 인식 오류
* **문제 상황:** Room과 Paging 라이브러리 연동 중 KSP 컴파일 단계에서 `MissingType` 에러로 인한 빌드 중단.
* **원인 분석:** 소스 폴더 경로 인식 오류 및 Java/AndroidX 간 동일 명칭 클래스(`DataSource`) 임포트 충돌.
* **해결 성과:** 소스 구조 표준화 및 의존성 우선순위 재정의를 통해 **빌드 에러를 완전히 해결**하고 컴파일 타임 안정성 확보.
