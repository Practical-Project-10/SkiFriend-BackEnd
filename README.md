<p align="center"><img src="https://user-images.githubusercontent.com/57797592/151089943-5fdc4edd-f643-4c6d-b00f-de6fb2101102.png" /></p>
<h4 align="center">📆 2021.12.13 ~ 2021.12.17</h4>
<br>

<h3 align="center"><b>👨🏻‍🤝‍👨🏻 Members 👨🏻‍🤝‍👨🏻</b></h3>
<br>
<table align="center">
    <tr>
        <td align="center">
        <a href="https://beomin-sd.tistory.com/"><img src="https://img.shields.io/badge/이현범-2DDC88?style=flat&logo=로고&logoColor=black"/></a>
        </td>
        <td align="center">
        <a href="https://diddl.tistory.com/"><img src="https://img.shields.io/badge/양성은-000AFF?style=flat&logo=로고&logoColor=white"/></a>
        </td>
        <td align="center">
        <a href=""><img src="https://img.shields.io/badge/최석영-D77EE9?style=flat&logo=로고&logoColor=white"/></a>
        </td>
    </tr>
    <tr>
        <th width="15%" align="center">:spider_web: BACK-END
        </th>
        <th width="15%" align="center">:spider_web: BACK-END
        </th>
        <th width="15%" align="center">:spider_web: BACK-END 
        </th>
    </tr>
</table>
<br>

---

<h3><b>🎫 프로젝트 소개 🎫</b></h3>
스키프랜드(SkiFriend)
마땅한 이동수단이 없어 고민하는 스키인들을 위한 
카풀, 커뮤니티 서비스

<br><br>
<h3><b>📣 팀 블로그 📣</b></h3>
https://power-bowler-c76.notion.site/8-13-10-0c3ad4dd39b34a2b8501e8bac3c63d19

<br><br>
<h3><b>🎞 프로젝트 발표영상 🎞</b></h3>
https://www.youtube.com/watch?v=UvTk7JV03cs&t=4s

<br><br>
<h3><b>🎞 프로젝트 시연영상 🎞</b></h3>
https://youtu.be/1j_bk71_Eaw

<br>

---

<br>
<h3 align="center"><b>🛠 Tech Stack 🛠</b></h3>
<p align="center">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/Springboot-47?style=for-the-badge&logo=Springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white">
<img src="https://img.shields.io/badge/Redis-FC5230?style=for-the-badge&logo=Redis&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">
<img src="https://img.shields.io/badge/TravisCI-FC5230?style=for-the-badge&logo=TravisCI&logoColor=white">
<img src="https://img.shields.io/badge/CodeDepoly-1F497D?style=for-the-badge&logo=CodeDepoly&logoColor=white">
<img src="https://img.shields.io/badge/S3-FC5230?style=for-the-badge&logo=S3&logoColor=white">
<img src="https://img.shields.io/badge/Nginx-7DB249?style=for-the-badge&logo=Nginx&logoColor=white">
<img src="https://img.shields.io/badge/Ffmpeg-47?style=for-the-badge&logo=Ffmpeg&logoColor=white">

<br><br>
<h3 align="center"><b>📊 Back-End Architecture 📊</b></h3>
<p align="center"><img src="https://user-images.githubusercontent.com/57797592/151089797-b13eb41b-2dec-442b-972c-3354b4b92923.png" /></p>

<br><br>
<h3 align="center"><b>📢 Entity Relationship Diagram 📢</b></h3>
https://drawsql.app/sout/diagrams/copy-of-db-2
<p align="center"><img src="https://user-images.githubusercontent.com/57797592/150979510-53af4f2c-f80a-4731-a219-83957324f485.png" /></p>

<br><br>
<h3 align="center"><b>🏷 API Table 🏷</b></h3>
<p align="center"><img src="https://user-images.githubusercontent.com/57797592/150988559-408d5f15-3124-4483-b5c0-12ebd1f2160e.png" /></p>
<b>상세 API Table Share Link : </b> https://power-bowler-c76.notion.site/API-0cc1aba76dd54ffd87f9f40f6877c5a3
<br><br><br>

---

<h3 align="center"><b>✏ Trouble Shooting & 기술적 고민들 ✏</b></h3>
<br>
<details>
    <summary>
        <b>회원 탈퇴 시, 해당 유저의 연관 정보들의 처리</b>
    </summary>
해결 : 유저 연관 관계를 Id만 가지고 있는 식으로 약한 결합으로 만들어 준 뒤, 서비스 단에서 예외처리를 해주었다.
<br><b>자세히 보기 : https://power-bowler-c76.notion.site/Trouble-Shooting-e589ceeee9534c7894c0c655891e0785</b>
</details>

<details>
    <summary>
        <b>사용자가 웹 페이지에 접속 중일 때, 실시간 알림 전송하기</b>
    </summary>
해결 : 로그인 시, 웹 소켓을 연결하여 참여 중인 채팅 방에서 메세지가 오거나, 새로운 채팅 방이 생성되었을 경우 알림이 가도록 구현
<br><b>자세히 보기 : https://beomin-sd.tistory.com/380</b>
</details>

<details>
    <summary>
        <b>회원 가입 & 로그인 방식 변경</b>
    </summary>
해결 : 기존의 복잡한 회원가입 과정에서 소셜 로그인을 도입하여 가입 과정을 대폭 축소시켰다.
<br><b>자세히 보기 : https://diddl.tistory.com/157</b>
</details>

<details>
    <summary>
        <b>채팅방에 유저 두 명이 모두 존재할 때, 메세지 읽음 처리가 안되는 예외 발생</b>
    </summary>
해결 : 세션을 이용한 in, out 상태를 저장하여 in일 시, 메세지를 바로 읽음 상태로 변경
<br><b>자세히 보기 : https://indecisive-viscount-244.notion.site/9b752db72c7242ec83d632ce97b1dcbc</b>
</details>

<details>
    <summary>
        <b>카풀 게시글의 설정한 시간이 지났을 경우의 게시글 상태 처리</b>
    </summary>
해결 : 스케쥴러를 사용해 15분 마다 현재 시간과 설정 시간을 체크하여, 자동으로 카풀 게시글을 모집완료 상태로 변경.
<br><b>자세히 보기 : https://power-bowler-c76.notion.site/Trouble-Shooting-b3fa7f60e4704bd38287a78dd9376202</b>
</details>

<details>
    <summary>
        <b>SkiResort 테이블 생성</b>
    </summary>
해결 : 원래 SkiResort의 경우 Enum을 활용한 유효성 검사만 해주었지만 스키장에 해당하는 정보들이 생김으로써 관리의 편의성을 위해 테이블을 생성함
<br><b>자세히 보기 : https://power-bowler-c76.notion.site/Trouble-Shooting-7c750abab120437f88c6326e0006204b</b>
</details>

<details>
    <summary>
        <b>채팅방 생성 시, 이미 나온 채팅방에 다시 접근하려고 했을 때의 예외처리</b>
    </summary>
해결 : 상대방의 ChatUserInfo가 존재할 때, 사용자에게 이미 나온 채팅방이라는 알림을 줄 수 있도록 함
<br><b>자세히 보기 : https://indecisive-viscount-244.notion.site/2a33bbdc642c4a40bea6e6301288ee3f</b>
</details>

<details>
    <summary>
        <b>상대방이 채팅 나갔을 때 또는 회원 탈퇴를 했을 때, 채팅 내용은 남아있지만 더 이상 채팅은 불가하도록 예외처리</b>
    </summary>
해결 : Chatroom 테이블에 active 컬럼을 추가하여 true일 경우만 채팅을 보낼 수 있도록 예외처리를 해줌
<br><b>자세히 보기 : https://beomin-sd.tistory.com/381</b>
</details>

<details>
    <summary>
        <b>쇼츠 기능의 동영상 썸네일 처리</b>
    </summary>
해결 : 1초 대의 프레임을 가져와서 썸네일 이미지로 저장한 후, 동영상과 함께 프론트로 전달
<br><b>자세히 보기 : https://beomin-sd.tistory.com/382</b>
</details>
