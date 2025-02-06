# AnonymousCIS - board-service
## 1. URL 경로
![image](https://github.com/user-attachments/assets/6011af4f-6e07-4c74-8455-4debff5ef868)

### 회원
- 게시판
  - bid를 총한 해당하는 게시글 목록을 볼 수 있음


- 게시글
  - C : 글쓰기 권한이 있는 게시글일 경우
  - R : 글보기 권한이 있는 게시글일 경우
  - U : 관리자 혹은 본인이 작성한 게시글일 경우
  - D : 관리자 혹은 본인이 작성한 게시글일 경우


- 댓글
  - C : 댓글 작성 권한이 있는 경우
  - R : 댓글 상태가 BLOCK이 아닌 경우
  - U : 관리자 혹은 본인이 작성한 댓글일 경우
  - D : 관리자 혹은 본인이 작성한 댓글일 경우

- 비회원의 경우
  - 본인 인증을 위해 비회원 비밀번호를 사용함.

### 관리자
#### 게시판 설정 관련
- 게시판 설정 등록 및 수정
- 게시판 설정 조회


#### 게시판 관련
- 게시판 단일 혹은 목록 일괄 삭제


#### 게시글 관련
- 게시글 일괄 혹은 목록 일괄 삭제
  - 게시글 BLOCK 시 MEMBER 도메인으로 정보 전달


#### 댓글 관련
- 댓글 단일 혹은 목록 일괄 삭제

## 2. 서비스
### 회원 및 관리자 공통 기능

- 게시판 권한
  - 관리자면 모두 가능 / 비밀글이라면 관리자와 글 작성자만 조회 가능 / 글 작성자와 관리자만 글 수정 및 삭제 가능
  - 등 권한과 관련하여 올바른 권한일 때만 기능이 구현되도록 함.



- 게시판 목록(게시판 단일 조회)
    - bid로 해당하는 목록을 출력(자유게시판, 공지게시판 등)


- 게시글 등록


- 게시글 상세보기(게시글 단일 조회)


- 게시글 조회수 업데이트


- 게시글 수정


- 게시글 삭제
  - 단, 회원이 삭제할 시 DB에서는 삭제되지 않음.
  - 관리자가 삭제할 때만 DB에서 삭제


- 댓글 등록
  - 댓글을 등록할 경우 해당 게시글의 댓글의 수 업데이트


- 댓글 수정


- 댓글 목록


- 댓글 삭제
  - 단, 회원이 삭제할 시 DB에서는 삭제되지 않음.
  - 관리자가 삭제할 때만 DB에서 삭제


### 비회원일 경우
- 비회원 비밀번호를 통한 검증
  - 수정, 삭제 시 인증을 위함.


### 관리자만 가능
- 게시판 설정 등록 및 수정


- 게시판 설정 목록 조회


- 게시판 단일 | 목록 일괄 삭제
  - DB상에서 실제로 삭제


- 게시글 단일 | 목록 일괄 삭제
  - DB 상에서 실제로 삭제


- 게시글 및 댓글을 BLOCK 처리.
  - BLOCK 처리가 된 게시글과 댓글은 관리자만 조회가 가능함.
  - BLOCK 처리가 된 글 혹은 댓글의 정보를 Member 도메인에게 전달
    - 해당하는 Member를 BLOCK 처리


## 3. 데이터베이스 벤다이어그램



## 4. 스웨거 API
https://cis-board-service.jinilog.com/apidocs.html