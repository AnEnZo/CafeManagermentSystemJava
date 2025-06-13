const tokenRaw = sessionStorage.getItem("jwtToken");


let token = tokenRaw.startsWith("Bearer ") ? tokenRaw : "Bearer " + tokenRaw;
if (!tokenRaw) {
  alert("You are not logged in!");
  window.location.href = "login.html";
}


const payload = parseJwt(token);
const displayname = payload?.displayName || payload?.sub ;

document.getElementById("user-info").textContent = "Logged in as: " + displayname;


const notificationAudio = document.getElementById("notificationSound");

const socket = new SockJS("/ws");
const stompClient = Stomp.over(socket);

stompClient.connect({ Authorization: token }, function (frame) {
    console.log("Connected: " + frame);

    stompClient.subscribe("/user/queue/messages", function (message) {
        const chatMessage = JSON.parse(message.body);

        // 1. Phát âm thanh thông báo
                if (notificationAudio) {
                    // Nếu đang tạm dừng (pause), đảm bảo set time = 0 để phát lại từ đầu
                    notificationAudio.currentTime = 0;
                    notificationAudio.play().catch(err => {
                        // Một số trình duyệt chặn autoplay nếu chưa có tương tác
                        console.warn("Không thể tự động phát âm thanh:", err);
                    });
                }

        const box = document.getElementById("chat-box");
        const msg = document.createElement("div");
        const nameToShow = chatMessage.senderDisplayName || chatMessage.senderUsername;
        msg.textContent = `${nameToShow}: ${chatMessage.content}`;
        box.appendChild(msg);

        // Scroll xuống dưới mỗi khi có tin nhắn mới
        box.scrollTop = box.scrollHeight;
    });
});

function sendMessageToUser(toUser, messageText) {
    if (!toUser || !messageText) {
        alert("Receiver and message must not be empty!");
        return;
    }
    stompClient.send("/app/chat", {}, JSON.stringify({
        receiverUsername: toUser,
        content: messageText
    }));
}

function handleSendMessage() {
    const toUser = document.getElementById("receiverUsername").value;
    const messageText = document.getElementById("messageText").value;
    sendMessageToUser(toUser, messageText);
}

function parseJwt(token) {
            try {
                const base64Payload = token.split('.')[1];
                const payload = atob(base64Payload);
                const utf8Payload = decodeURIComponent(escape(payload)); // ✅ fix UTF-8 decode
                return JSON.parse(utf8Payload);
            } catch (e) {
                return null;
      }
}