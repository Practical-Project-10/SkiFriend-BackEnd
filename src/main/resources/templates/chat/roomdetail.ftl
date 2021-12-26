<#--<!doctype html>-->
<#--<html lang="en">-->
<#--<head>-->
<#--    <title>Websocket ChatRoom</title>-->
<#--    <!-- Required meta tags &ndash;&gt;-->
<#--    <meta charset="utf-8">-->
<#--    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">-->

<#--    <!-- Bootstrap CSS &ndash;&gt;-->
<#--    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">-->
<#--    <style>-->
<#--        [v-cloak] {-->
<#--            display: none;-->
<#--        }-->
<#--    </style>-->
<#--</head>-->
<#--<body>-->
<#--<div class="container" id="app" v-cloak>-->
<#--    <div>-->
<#--        <h2>{{room.name}}</h2>-->
<#--    </div>-->
<#--    <div class="input-group">-->
<#--        <div class="input-group-prepend">-->
<#--            <label class="input-group-text">내용</label>-->
<#--        </div>-->
<#--        <input type="text" class="form-control" v-model="message" @keyup.enter="sendMessage">-->
<#--        <div class="input-group-append">-->
<#--            <button class="btn btn-primary" type="button" @click="sendMessage">보내기</button>-->
<#--        </div>-->
<#--    </div>-->
<#--    <ul class="list-group">-->
<#--        <li class="list-group-item" v-for="message in messages">-->
<#--            {{message.sender}} - {{message.message}}</a>-->
<#--        </li>-->
<#--    </ul>-->
<#--    <div></div>-->
<#--</div>-->
<#--<!-- JavaScript &ndash;&gt;-->
<#--<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>-->
<#--<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>-->
<#--<script src="/webjars/bootstrap/4.3.1/dist/js/bootstrap.min.js"></script>-->
<#--<script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>-->
<#--<script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>-->
<#--<script>-->
<#--    // websocket & stomp initialize-->
<#--    var sock = new SockJS("/ws-stomp");-->
<#--    var ws = Stomp.over(sock);-->
<#--    // vue.js-->
<#--    var vm = new Vue({-->
<#--        el: '#app',-->
<#--        data: {-->
<#--            roomId: '',-->
<#--            room: {},-->
<#--            sender: '',-->
<#--            message: '',-->
<#--            messages: [],-->
<#--            token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJFWFBJUkVEX0RBVEUiOjE2NDA3MDQ0MDIsImlzcyI6InNwYXJ0YSIsIlVTRVJfTkFNRSI6ImJlb21pbjEyMyJ9._wr_gQD3QKeFfn-G3-CNbLStscY-1tZrFowpZWc2VcU'-->
<#--        },-->
<#--        created() {-->
<#--            this.roomId = localStorage.getItem('wschat.roomId');-->
<#--            // this.sender = localStorage.getItem('wschat.sender');-->
<#--            this.findRoom();-->
<#--        },-->
<#--        methods: {-->
<#--            findRoom: function() {-->
<#--                axios.get('/chat/room/'+this.roomId).then(response => { this.room = response.data; });-->
<#--            },-->
<#--            sendMessage: function() {-->
<#--                ws.send("/pub/chat/message",{"token":this.token},JSON.stringify({type:'TALK', roomId:this.roomId, sender:this.sender, message:this.message}));-->
<#--                this.message = '';-->
<#--            },-->
<#--            recvMessage: function(recv) {-->
<#--                this.messages.unshift({"type":recv.type,"sender":recv.type=='ENTER'?'[알림]':recv.sender,"message":recv.message})-->
<#--            },-->
<#--            getPreviousMessages: function() {-->
<#--                axios.get('/chat/message/' + this.roomId).then(response => {-->
<#--                    this.messages = [...response.data, ...this.messages]-->
<#--                });-->
<#--            }-->
<#--        }-->
<#--    });-->
<#--    // pub/sub event-->
<#--    ws.connect({"token":vm.$data.token}, function(frame) {-->
<#--        ws.subscribe("/sub/chat/room/"+vm.$data.roomId, (message) => {-->
<#--            var recv = JSON.parse(message.body);-->
<#--            vm.recvMessage(recv);-->
<#--            console.log({ recv });-->
<#--        });-->
<#--        ws.send("/pub/chat/message",{token:vm.$data.token} ,JSON.stringify({type:'ENTER', roomId:vm.$data.roomId, sender:vm.$data.sender}));-->
<#--        vm.getPreviousMessages();-->
<#--    }, function(error) {-->
<#--        alert("error "+error);-->
<#--    });-->
<#--</script>-->
<#--</body>-->
<#--</html>-->
<!doctype html>
<html lang="en">
<head>
    <title>Websocket ChatRoom</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="container" id="app" v-cloak>
    <div class="row">
        <div class="col-md-6">
            <h4>{{roomName}} <span class="badge badge-info badge-pill">{{userCount}}</span></h4>
        </div>
        <div class="col-md-6 text-right">
            <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>
            <a class="btn btn-info btn-sm" href="/chat/room">채팅방 나가기</a>
        </div>
    </div>
    <div class="input-group">
        <div class="input-group-prepend">
            <label class="input-group-text">내용</label>
        </div>
        <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage('TALK')">
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">보내기</button>
        </div>
    </div>
    <ul class="list-group">
        <li class="list-group-item" v-for="message in messages">
            {{message.sender}} - {{message.message}}</a>
        </li>
    </ul>
</div>
<!-- JavaScript -->
<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
<script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
<script>
    // websocket & stomp initialize
    var sock = new SockJS("/ws-stomp");
    var ws = Stomp.over(sock);
    // vue.js
    var vm = new Vue({
        el: '#app',
        data: {
            roomId: '',
            roomName: '',
            message: '',
            messages: [],
            token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJFWFBJUkVEX0RBVEUiOjE2NDA3NjAwODAsImlzcyI6InNwYXJ0YSIsIlVTRVJfTkFNRSI6ImJlb21pbjEyMyJ9.hgSHheTqu0LCJcdz8bUSS7byB6JyQjSdilhOnvKwqUg',
            userCount: 0
        },
        created() {
            this.roomId = localStorage.getItem('wschat.roomId');
            this.roomName = localStorage.getItem('wschat.roomName');
            var _this = this;
            // axios.get('/chat/user').then(response => {
            //     _this.token = response.data.token;
            // callback function
            // });

            ws.connect({"token":_this.token}, function(frame) {
                ws.subscribe("/sub/chat/room/"+_this.roomId, function(message) {
                    var recv = JSON.parse(message.body);
                    _this.recvMessage(recv);
                });
            }, function(error) {
                alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
                location.href="/chat/room";
            });
        },
        methods: {
            sendMessage: function(type) {
                ws.send("/pub/chat/message", {"token":this.token}, JSON.stringify({type:type, roomId:this.roomId, message:this.message}));
                this.message = '';
            },
            recvMessage: function(recv) {
                this.userCount = recv.userCount;
                this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message})
            }
        }
    });
</script>
</body>
</html>