# 🍎 FreshAuctionApp
<p align="center">
  <img src="https://github.com/user-attachments/assets/ddc64b61-0ee3-4a1a-a3c7-c14f43a2a832" width="30%">
</p>

<br>

## 📝 1. Introduction
FreshAuctionApp은 전국 농산물 도매시장의 실시간 경매 낙찰 가격을 조회하는 안드로이드 앱입니다.

단순히 데이터를 띄우는 것을 넘어, 공공데이터 API 연동 시 발생하는 **'잦은 서버 장애 및 타임아웃' 문제**를 해결하는 데 집중한 프로젝트입니다.

API 호출이 실패하더라도 사용자가 빈 화면을 보지 않도록, 로컬 DB(Room)에 저장된 마지막 성공 데이터를 불러오는 Fallback 로직을 구현해 서비스 안정성을 높였습니다.

<p align="center">
<img src="https://github.com/user-attachments/assets/1655da57-6102-4c40-818f-f47d567b69b6" align="center" width="22%"/>
<img src="https://github.com/user-attachments/assets/6c7e42f8-cad7-4389-9760-d55508464d61" align="center" width="22%"/>
<img src="https://github.com/user-attachments/assets/360d6509-d0b7-4156-afc8-59746a4d8b5e"align="center" width="22%" />
</p>
  
<br>

## 🛠️ 2. Tech Stack

| 분류 | 기술 | 도입 목적 |
| :--- | :--- | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/> | Coroutines를 활용한 비동기 API 호출 |
| **Architecture** | <img src="https://img.shields.io/badge/MVVM-005C84?style=flat-square"/> <img src="https://img.shields.io/badge/Layered_Architecture-005C84?style=flat-square"/> | UI 렌더링과 비즈니스 로직, 데이터 통신의 명확한 분리 |
| **Network** | <img src="https://img.shields.io/badge/Retrofit2-86B81B?style=flat-square"/> <img src="https://img.shields.io/badge/OkHttp3-86B81B?style=flat-square"/> | 공공데이터 API 연동 및 네트워크 타임아웃 예외 처리 |
| **Local DB** | <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white"/> | API 장애 시 복원할 마지막 성공 데이터(스냅샷) 로컬 저장 |
| **Data / UI** | <img src="https://img.shields.io/badge/Paging3-3DDC84?style=flat-square&logo=android&logoColor=white"/> | 대량의 경매 리스트 스크롤 시 메모리 과부하 방지 |
  
<br>

## 🏗️ 3. Architecture
단방향 데이터 흐름(UDF) 기반의 MVVM 패턴과, 네트워크 장애 시 로컬 DB(LKG)로 즉시 우회(Fail-Fast)하는 장애 격리 파이프라인 구조입니다
<p align="center">
  <img src="https://github.com/user-attachments/assets/9ca8db0b-4fe0-40be-839b-0ce00edcd801" width="80%">
</p>


