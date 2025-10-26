#Program Introduction
A single program which is used for PWSN care.It records health circumstances of every PSWN and sent data to family member, which is good at knowing the situation of PWSN.
# Eighty-two
TEAM MEMBERS:
YUXUAN CHEN 1386587 YUXUCHEN8@student.unimelb.edu.au  - Product Owner & Frontend Development
Yuanmeng Yi 1395583 yuanmeng.yi@student.unimelb.edu.au - Scrum Master & Full stack
Yutong he 1368919 yutongh8@student.unimelb.edu.au -Backend Development
Songzhu Li 1378824 songzhul@student.unimelb.edu.au -Backend Development
Tongyu Yang 1382810 tongyu.yang1@student.unimelb.edu.au -Frontend Development 

# Eighty-two — Minimal Frontend/Backend Skeleton
- Backend: Spring Boot (JDK 21+), provides a health check endpoint and CORS support  
- Frontend: React + Vite (Node.js 18+), reads backend URL from `.env` and calls `/api/health`  

This version does not include database or JPA dependencies, making it ideal for early-stage API and frontend-backend integration.  
Database integration (MySQL / JPA) can be added later with minimal changes.


## Quick Start
### Backend (Spring Boot)
**Requirements:**
- JDK 21+
- Maven Wrapper (included)

**Run:**
`cd backend
export SENDGRID_API_KEY=dummy_key
./mvnw spring-boot:run`

**Test endpoint:**
GET http://localhost:8080/api/health
→ Response: OK

### Frontend (Vue3 + Vite)
**Requirements:**
- Node.js 18+
- npm / yarn / pnpm

**Run:**
`cd frontend
npm install
cp .env.example .env   # update VITE_API_BASE to match your backend URL if needed
npm run dev`

**Visit**:
http://localhost:5174/

### Environment Variables：
**Frontend .env.example**
VITE_API_BASE=http://localhost:8080
**.env is ignored in .gitignore and should not be committed**
**Modify .env locally to point to different backend endpoints*


### .gitignore Overview
**frontend/.gitignore**
node_modules
dist
.env
**backend/.gitignore**
target
.env
.DS_Store

### Future Expansion (MySQL / JPA Integration)
**1. Remove from backend/src/main/resources/application.properties**
**2. Add dependencies in pom.xml**
**3. Configure datasource in application.properties:**
**4. Create @Entity classes and Repository interfaces.**
