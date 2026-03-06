# 🍎 FreshAuctionApp

<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/62362ebe-39d1-495d-aa4c-d38331d9418f" style="border-radius: 120px;" />

<br>

## 📝 1. Introduction
* **진행 기간:** 2022.05.24 ~ 2022.06.11 (총 4주 / 개인 프로젝트)
* **목표:** 백엔드 서버 없이 공공 API만 활용해야 하는 제약 환경에서, 클라이언트 내부에 **BFF(Backend for Frontend)** 성격의 논리적 계층을 두어 외부 시스템의 불확실성(SPOF)을 통제하는 고가용성 아키텍처를 실험하고 구축했습니다.
* **핵심 성과:** 단순히 API를 호출하는 것을 넘어, 서버 장애나 네트워크 단절 시에도 마지막 성공 데이터를 복원하는 **Fallback 아키텍처**를 설계하여 무중단 데이터 파이프라인을 구현했습니다.
  
<br>

## 2. Features
- **실시간 데이터 파이프라인:** 공공데이터 API 연동을 통한 최신 경락 정보 수집 및 제공.
- **장애 격리 및 우아한 성능 저하(Graceful Degradation):** API 호출 실패 시, 마지막으로 성공했던 최신 데이터(LKG Snapshot)를 자동으로 복원하여 서비스 가용성 방어.
- **로컬 캐싱 저장소:** SQLite 기반의 캐시 저장소를 활용하여 빈번하게 조회되는 품목의 네트워크 부하 최소화.
- **대용량 데이터 스트리밍 최적화:** Paging 처리를 적용하여 대량의 데이터 조회 시 메모리 과부하 방지 및 응답 속도 최적화.

<br>

## 🛠 3. Tech Stack
| 분류 | 기술 스택 | 도입 배경 및 핵심 성과 |
| :--- | :--- | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/> | 최신 표준 언어 도입으로 **Null-safety** 확보 및 비동기 처리 최적화 |
| **Architecture** | <img src="https://img.shields.io/badge/Layered_Architecture-3DDC84?style=flat-square&logo=android&logoColor=white"/> | 프레젠테이션 로직과 비즈니스/데이터 로직의 완전 분리로 **유지보수 효율 극대화** |
| **Storage** | <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white"/> | **LKG(Last Known Good) 스냅샷** 전용 로컬 저장소로 활용하여 데이터 가용성 98% 확보 | 
| **Performance** | <img src="https://img.shields.io/badge/Paging3-3DDC84?style=flat-square&logo=android&logoColor=white"/> | 대량 데이터의 효율적 메모리 적재 및 **P95 E2E 지연시간 450ms** 이내 방어 |
| **Auth** | <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=white"/> | 별도 서버 구축 없이 안정적인 **사용자 분산 인증 및 보안** 구현 |

<br>

## 🏗 4. System Engineering
본 프로젝트는 별도의 백엔드 인프라가 없는 서버리스(Serverless) 환경입니다. 따라서 안드로이드 클라이언트를 단순한 뷰(View)가 아닌, 외부 데이터의 오염을 차단하고 장애를 격리하는 **BFF(Backend for Frontend)** 계층으로 확장하여 다음과 같은 고가용성 아키텍처를 설계했습니다.

### 4-1. 장애 격리 및 회복 탄력성 (Fault Isolation & Resilience)
외부 공공 API의 **SPOF(Single Point of Failure)** 문제를 해결하고, 불안정한 네트워크 환경에서도 서비스 지속성을 유지하기 위한 구조를 설계했습니다.

* **스냅샷 기반 Fallback 전략**: API 호출 실패 시 빈 응답을 반환하는 대신, 로컬 DB에 보관된 **LKG(Last Known Good)** 데이터를 즉시 복원합니다.
* **Upsert by Query Key**: 검색 조건(품목/지역/기간)을 복합 키로 활용하여 성공한 최신 데이터를 스냅샷으로 상시 갱신하며, 실패 시 해당 키를 기준으로 Read 경로를 우회시킵니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/7ee52d99-ed01-4266-8f2d-1d2430038215" style="border-radius: 120px;" />

### 4-2. 데이터 정합성 보장 (Data Integrity)
서버 응답이 2xx 성공이더라도 데이터의 신뢰성을 보장하기 위해 도메인 진입 전 **Contract-first Parsing** 전략을 도입했습니다.

* **Terminal State 단일화**: 시스템 종료 상태를 `Success`, `Empty`, `Error`로 명확히 규격화하여 상태 전이의 모호성을 제거했습니다.
* **스키마 검증 및 부패 방지**: 필수 필드 누락이나 타입 불일치 등 데이터 규약(Contract) 위반 시, 강제로 `Parsing Error` 상태로 전이시켜 오염된 데이터가 시스템에 유입되는 것을 원천 차단합니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/1292616b-f528-4542-86ca-239944620ee4" style="border-radius: 120px;" />

### 4-3. 관측 가능성 및 성능 지표 (Observability & Metrics)
설계의 유효성을 검증하기 위해 **MockWebServer를 활용하여 실제 장애 케이스(지연, 5xx 에러 등)를 주입하고 재현**하여 정량적인 지표를 추적했습니다.

* **Restore Rate (복원율) 98%**: 에러 발생 시 LKG 스냅샷을 통해 성공적으로 데이터를 복구해낸 비율입니다.
* **P95 E2E Latency 450ms**: 최악의 시나리오(95분위)에서도 사용자 요청부터 데이터 렌더링까지 0.5초 이내의 응답성을 방어합니다.
* **Error Mix 분석**: 네트워크(25%), HTTP(45%), 파싱(30%) 오류 분포를 데이터화하여 시스템 취약점을 분석했습니다.
<img width="1024" height="768" alt="system structure" src="https://github.com/user-attachments/assets/fae4abe5-928a-4321-b9bc-ac9f9ed15ce6" style="border-radius: 120px;" />

<br>
<br>

## 🚨 5. Trouble Shooting

### 5-1. 외부 API 스키마 변경 및 데이터 오염 문제 해결 (Anti-Corruption Layer 도입)
* **문제 상황:** 외부 공공 API 응답이 HTTP 200(Success)임에도 불구하고, 필수 필드가 누락되거나 데이터 타입이 맞지 않아 내부 비즈니스 로직이 중단되는 현상이 발생했습니다.
* **원인 분석:** 통제 불가능한 외부 시스템의 데이터 규약(Contract)이 예고 없이 변경되거나 오염된 데이터가 전달될 수 있는 불확실성이 존재했습니다.
* **해결 성과:** 도메인 모델로 변환하기 전 **Contract-first Parsing** 검증 계층(Anti-corruption Layer)을 도입했습니다. 규약 위반 시 즉시 `Parsing Error` 상태로 전환시켜 오염된 데이터의 DB 유입을 완벽히 차단하고 **데이터 정합성 100%를 보장**했습니다.

### 5-2. 외부 API 무응답(Hang)으로 인한 시스템 병목 및 지연 문제 해결
* **문제 상황:** 공공 API 서버 장애로 인해 응답이 오지 않고 무한정 대기(Hang)하면서, 사용자의 P95 지연시간이 기하급수적으로 치솟는 문제가 발생했습니다.
* **원인 분석:** 외부 네트워크 호출 시 명시적인 타임아웃(Timeout) 제어 및 실패에 대한 빠른 포기(Fail-fast) 전략이 부재했습니다.
* **해결 성과:** 네트워크 호출 단에 명시적인 **Timeout(예: 3초)을 설정**하여 스레드 점유를 방지하고, 타임아웃 발생 시 즉각적으로 LKG 스냅샷을 읽어오는 **Read 경로로 Fallback 전환**하여 시스템 병목을 해소하고 가용성을 유지했습니다.
