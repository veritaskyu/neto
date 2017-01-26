# Neto

1. 부하 분산
2. 실시간 Scale out
3. failover
4. 소켓 통신 및 웹소켓 지원

위와 같은 요건을 충족시키는 분산 채팅 서버를 만들기 위해 고군분투하는 개인 프로젝트 입니다.


## 구성

### session-server
클라이언트가 접속할 서버를 할당/관리하기 위한 세션 서버 프로그램 입니다.

### channel-server
클라이언트가 직접 접속하여 메시지를 주고 받도록 하는 채널 서버 프로그램 입니다.

### neto-common
공통으로 사용하는 클래스 모음입니다.
Binary, JSON, Websocket 메시지를 인코딩/디코딩 하는 코덱 클래스 및 각 메시지 클래스를 포함하고 있습니다.


## 설명

1. 채널 서버의 노드는 Zookeeper로 관리되며 채널 서버의 정보와 클라이언트 접속 정보는 Redis로 관리됩니다.
2. 세션 서버는 Zookeeper/Redis 정보를 토대로 클라이언트에게 채널 서버를 할당합니다.
3. 세션 서버는 채널 서버의 부하 분산을 위해 Consistent Hashing 기법을 사용하여 채널 서버를 클라이언트에게 할당합니다.
4. 클라이언트는 http request를 통해 세션 서버로부터 접속할 채널 서버를 할당 받습니다. (ex. http://localhost:12345/channels)


### TODO LIST

통신 관련
- 소켓과 웹소켓 둘다 지원 가능하도록 ProtocolUnificationHandler class 개선
(현재는 NetoJsonToMessageDecoder/NetoMessageToJsonEncoder로 연결 되어 있음)
- JSON 메시지 이외에 Binary 메시지 개선
- 파일 전송 기능 추가

읽음 건수 데이터 관련
- 메시지 아이디에 대한 읽음 건수 저장
- 메시지 창에서 메시지의 읽음 횟수 출력
- 몇 명의 사용자가 메시지를 읽었는지 조회
- 메시지가 삭제되면 메시지 읽음 건수도 삭제
- 사용자 번호, 방 번호로 hash를 구성
- 메시지 번호를 필드로 하여 건수 저장
- 메시지 삭제 시 읽음 건수 삭제


(서버 설정에 개인 정보가 노출되어 저장소를 삭제 후 다시 개설하였습니다)