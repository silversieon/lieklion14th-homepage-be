# 서경대학교 멋쟁이사자처럼 14기 홈페이지 레포지토리입니다.


1. [Intro](#intro)
2. [System Architecture](#system_architecture)
3. [ERD](#erd)
4. [Package Structure](#package_structure)
5. [Assigned Tasks](#assigned_tasks)
6. [API Documentation](#api_documentation)

---

## Intro
![image](https://github.com/user-attachments/assets/5bda78f9-a15c-48c9-894f-96a97c830165)
![image](https://github.com/user-attachments/assets/6bf51202-d3a0-46dc-bc1b-23bd6e398d9f)
![image](https://github.com/user-attachments/assets/9d0f3532-daf9-48d6-b150-66523b74ecbd)

> [서경대 멋사 홈페이지 바로가기](https://skulikelion.com/)

> **기간**: 2026.01 ~ 2025.02
>

> **팀 구성**: PO 4명, FE 3명, BE 3명
>

> **역할**: 백엔드 개발, 인프라 관리
>

### 목적

- 서경대학교 멋쟁이사자처럼 동아리에 대한 정보를 제공하고, 동아리에 지원하고자 하는 학생들에게 지원 기능을 제공하는 서비스입니다.
- 동아리 핵심 정보 제공부터 지원 전 과정을 지원자와 관리자 양측 모두가 활용할 수 있도록 개발했습니다.


---

## System_Architecture
![image](https://github.com/user-attachments/assets/18729cb4-d2ac-4dfd-b15b-0c06187cfe8b)

---

## ERD
![image](https://github.com/user-attachments/assets/b15d19d2-d7af-42f8-959d-0a024e1bed62)


---

## Package_Structure

- 도메인 계층형 혼합 패키지 구조를 통해서 프로젝트 전체 구조를 쉽게 파악하고, 협업에 용이하도록 구성했습니다.

```
com.skunivlikelion.homepage
├── domain
│   ├── auth
│   ├── application
│       ├── form
│       ├── question
│       ├── record
│       └── result
│   ├── interview
│       ├── booking
│       └── schedule
│   ├── project
│   ├── semester
│   └── user
├── global
│   ├── annotation
│   ├── aspect
│   ├── cache
│   ├── common
│   ├── config
│       └── property
│   ├── exception
│   ├── filter
│   ├── page
│   ├── s3
│   └── security
│       └── jwt

```

---

## Assigned_Tasks

| Feature  | BE assignee                                        |
|----------|----------------------------------------------------|
| 회원 관련 기능 | [@silversieon](https://github.com/silversieon)     |
| 인증 기능    | @silversieon                                       |
| 구성원 관련 기능 | @silversieon                                       |
| 프로젝트 기능  | @silversieon                                       |
| 면접 일정 기능 | [@shinchaerin79](https://github.com/shinchaerin79) |
| 면접 예약 기능 | @shinchaerin79                                     |
| 회원 지원 기능 | [@naooung](https://github.com/naooung)             |
| 지원 일정 기능 | @naooung                                           |
| 지원서 관리 기능 | @naooung                                           |
| 프로젝트 기능  | [@dahyun0423](https://github.com/dahyun0423), @silversieon     |

---

## API_Documentation
> 자세한 API 및 역할 분담 확인하기 >
> [Notion API 명세서](https://zest-property-53e.notion.site/14-API-386be78085748067be9df0e8526a54f9?pvs=74)
