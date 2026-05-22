# CivilEase

CivilEase는 학생과 관리자 간의 민원 처리를 돕는 Java 프로그램입니다.
## 개발 환경
- **Java SDK**: JDK 25.0.2 (OpenJDK Temurin)
- **Database**: [MySQL 9.7 LTS](https://dev.mysql.com/downloads/mysql/), [MySQL WorkBench 8.0 CE](https://dev.mysql.com/downloads/workbench/)
- **IDE**: Eclipse 2026-03 (4.39.0)
- **library**: [JDBC mysql-connector-j-9.7.0](https://dev.mysql.com/downloads/connector/j/)

## 구동 방법

### 1. 소스 코드 가져오기
터미널(또는 Git Bash)에서 아래 명령어를 입력하여 프로젝트를 클론합니다.
```bash
git clone https://github.com/pouch22/CivilEase.git
```
git이 없다면 ZIP파일로 다운받아 Eclipse에서 열어도 가능합니다.
- file -> import -> General -> Projects form Folder or Archive -> import source 경로 지정 -> Finish


### 2. 데이터베이스 설정 (MySQL)
1. MySQL에 접속합니다.
2. `docs/design/schema.sql` 파일의 내용을 복사하여 실행하거나, 아래 명령어로 스키마를 생성합니다.
   ```sql
   -- 프로젝트 루트의 docs/design/schema.sql 참고
   SOURCE [파일경로]/schema.sql;
   ```
3. `src/com/civilease/util/DBConnection.java` 파일에서 본인의 MySQL ID(root)와 비밀번호로 수정합니다.

### 3. 프로젝트 라이브러리 추가 (Eclipse 기준)
이 프로젝트는 MySQL 연결을 위해 JDBC 드라이버가 필요합니다.
1. Eclipse에서 프로젝트를 Import 합니다.
2. 프로젝트 우클릭 -> **Build Path** -> **Configure Build Path** 선택
3. **Libraries** 탭 -> **Classpath** 선택 -> **Add JARs** 클릭
4. 프로젝트 내의 `lib/mysql-connector-j-9.7.0.jar` 파일을 선택하고 적용합니다.

### 4. 실행
- `src/com/civilease/view/LoginFrame.java` 파일을 실행하여 프로그램을 시작합니다.

## 초기 계정 정보
- **관리자**: `admin` / `admin123`
- **학생**: `20240001` / `020505`

---

