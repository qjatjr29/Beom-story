# 🚀 Beomstory

## 🎥 짧은 시연 영상 (예정)
> 📌 기능별 시연 영상 1  
> 📌 기능별 시연 영상 2

---

## ✔️ 프로젝트 개요

평소의 일상이나 여행 추억들을 기록해 추억하기 위함

### 🔗 링크
[🚀 서비스](https://vercel.)  
[🔗 프로젝트 노션 보기](https://www.notion.so/)

### ⌛️ 프로젝트 기간
`2025.02 ~ ing`

---

## 🛠 프로젝트 구조

### 🧱 ERD 

```mermaid
erDiagram 
    users ||--o{ story : ""
    users ||--o{ place : ""
    story ||--o{ place : ""

    users {
        BIGINT id PK
        VARCHAR email
        VARCHAR nickname
        VARCHAR password
        VARCHAR profile_url
    }

    story {
        BIGINT id PK
        BIGINT author_id FK
        VARCHAR title
        VARCHAR description
        VARCHAR category
        VARCHAR status
        TIMESTAMP start_date
        TIMESTAMP end_date
    }

    place {
        BIGINT id PK
        BIGINT story_id FK
        BIGINT author_id FK
        VARCHAR name
        VARCHAR description
        VARCHAR address
        VARCHAR image_url
    }
```

### ⭐️ 전체 Flow
![전체](https://github.com/user-attachments/assets/c3a5fd23-404f-4257-b856-09ef2c35c867)

## 

