const RequesterFlow = {
    // Biến trạng thái (State)
    currentEkyc: null,
    currentTicketId: null,
    userId: "USER_" + Math.floor(Math.random() * 1000),
    userName: "Nguyen Van Requester",

    async startEkyc() {
        const res = await fetch('/api/ekyc/start', { method: 'POST' });
        this.currentEkyc = await res.json();
        UIService.logTo('ekycResult', 'eKYC started: ' + JSON.stringify(this.currentEkyc));
    },

    async attemptNfc() {
        if (!this.currentEkyc) {
            UIService.showToast("Vui lòng Bắt đầu eKYC trước khi quét!", "warning", "requesterPane");
            return;
        }
        const deviceType = document.getElementById('requesterDeviceType').value;
        const simulateSuccess = deviceType === "GOOD";

        const res = await fetch('/api/nfc/attempt', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                transactionId: this.currentEkyc.transactionId,
                deviceSupported: deviceType !== "NO_NFC",
                simulateSuccess: simulateSuccess
            })
        });
        const data = await res.json();
        
        if (data.fallbackToHelper) {
            UIService.logTo('nfcAttemptResult', data.message || 'Chuyển sang NFC Hộ.', true);
            document.getElementById('helperRegistration').style.display = 'block';
        } else if (!data.success) {
            UIService.logTo('nfcAttemptResult', data.message || 'Lỗi quét NFC. Vui lòng thử lại.', true);
        } else {
            UIService.logTo('nfcAttemptResult', 'Quét NFC Thành công! Không cần hỗ trợ.');
            document.getElementById('helperRegistration').style.display = 'none';
        }
    },

    async validateHelper() {
        const phone = document.getElementById('helperPhoneInput').value;
        const res = await fetch('/api/helper/validate-phone', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phone })
        });
        const data = await res.json();
        if (res.ok) {
            UIService.logTo('helperValidateResult', 'SĐT hợp lệ.');
            document.getElementById('maskedHelperName').innerText = data.maskedName;
            document.getElementById('tncConfirm').style.display = 'block';
        } else {
            UIService.logTo('helperValidateResult', data.error, true);
            document.getElementById('tncConfirm').style.display = 'none';
        }
    },

    async createTicket() {
        if (!document.getElementById('tncCheckbox').checked) {
            UIService.showToast("Bạn cần đồng ý với Điều khoản và Thỏa thuận.", "warning", "requesterPane");                
            return;
        }
        const helperPhone = document.getElementById('helperPhoneInput').value;
        
        const res = await fetch('/api/nfc-tickets', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                requesterId: this.userId,
                requesterName: this.userName,
                helperPhone: helperPhone,
                journeyType: "NTB"
            })
        });
        const ticket = await res.json();
        this.currentTicketId = ticket.id;
        
        UIService.logTo('longPollStatus', `Đã tạo phiếu! ${ticket.id}. Đang chờ xác nhận từ người hỗ trợ...`);
        document.getElementById('resendBtn').style.display = 'block';
        
        this.pollForResult(this.currentTicketId);
    },

    async pollForResult(ticketId) {
        try {
            const res = await fetch(`/api/nfc-tickets/${ticketId}/await`);
            if (res.status === 400 || res.status === 500) {
                 const err = await res.json();
                 UIService.logTo('longPollStatus', 'Lỗi: ' + JSON.stringify(err), true);
                 return;
            }
            const data = await res.json();
            
            if (data.message === "POLL_TIMEOUT") {
                UIService.logTo('longPollStatus', 'Vẫn đang đợi phản hồi... (Tự động tải lại)');
                this.pollForResult(ticketId); 
            } else if (data.status === "COMPLETED") {
                UIService.logTo('longPollStatus', 'Thành công! Payload: ' + JSON.stringify(data.payload));
            } else if (data.status === "DECLINED" || data.status === "EXPIRED" || data.status === "MATCH_FAILED") {
                UIService.logTo('longPollStatus', `Thất bại: ${data.status} - ${data.message}`, true);
            } else {
                UIService.logTo('longPollStatus', 'Trạng thái không xác định: ' + JSON.stringify(data));
            }
        } catch (e) {
            UIService.logTo('longPollStatus', 'Mất kết nối mạng, đang thử lại...', true);
            setTimeout(() => this.pollForResult(ticketId), 2000);
        }
    },

    async resendOtt() {
        if (!this.currentTicketId) return;
        const res = await fetch(`/api/nfc-tickets/${this.currentTicketId}/resend`, { method: 'POST' });
        if (res.ok) {
            UIService.logTo('longPollStatus', 'Đã gửi lại thông báo thành công!');
        }
    }
};

// ==========================================
// 2. MODULE: NGƯỜI HỖ TRỢ (HELPER)
// ==========================================
const HelperFlow = {
    // Biến trạng thái
    activeTicketId: null,

    async fetchRequests() {
        const phone = document.getElementById('myPhoneInput').value;
        const res = await fetch(`/api/helper/requests/${phone}`);
        const tickets = await res.json();
        
        let html = '';
        tickets.forEach(t => {
            if (t.status === "SENT" || t.status === "CREATED") {
                html += `<div class="inbox-card">
                    <p>Yêu cầu từ: <strong>${t.requesterName}</strong></p>
                    <p>Thời gian: ${new Date(t.createdAt).toLocaleTimeString()}</p>
                    <button onclick="HelperFlow.selectTicket('${t.id}', '${t.requesterName}')">Mở yêu cầu</button>
                </div>`;
            }
        });
        document.getElementById('inboxList').innerHTML = html || '<p style="text-align:center; color:#888; font-size: 0.9rem; margin-top: 20px;"><i>Không có yêu cầu nào đang chờ.</i></p>';
    },

    selectTicket(id, rName) {
        this.activeTicketId = id;
        document.getElementById('ticketAction').style.display = 'block';
        document.getElementById('reqName').innerText = rName;
        document.getElementById('activeTicketId').innerText = id;
        document.getElementById('helperScanResult').innerHTML = '';
    },

    async declineTicket() {
        const phone = document.getElementById('myPhoneInput').value;
        const res = await fetch(`/api/nfc-tickets/${this.activeTicketId}/decline`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ helperPhone: phone })
        });
        if (res.ok) {
            UIService.logTo('helperScanResult', 'Đã từ chối yêu cầu.');
            document.getElementById('ticketAction').style.display = 'none';
            this.fetchRequests();
        }
    },

    async submitNfcScan() {
        const phone = document.getElementById('myPhoneInput').value;
        const deviceType = document.getElementById('helperDeviceType').value;
        
        if (deviceType === "NO_NFC") {
            UIService.logTo('helperScanResult', 'Thiết bị không hỗ trợ NFC. Vui lòng đóng yêu cầu hoặc Từ chối.', true);
            return;
        }
        
        const forceFail = deviceType === "SHODDY";
        const payload = {
            helperPhone: phone, idNumber: "079090123456", fullName: "Nguyen Van Requester",
            dob: "01/01/1990", expiry: "01/01/2030", portraitHash: "abcd1234efgh5678", forceMatchFail: forceFail
        };

        const res = await fetch(`/api/nfc-tickets/${this.activeTicketId}/nfc-scan`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            UIService.logTo('helperScanResult', 'Quét NFC thành công và đã gửi dữ liệu!');
            document.getElementById('ticketAction').style.display = 'none';
            this.fetchRequests();
        } else {
            const data = await res.json();
            UIService.logTo('helperScanResult', 'Lỗi: ' + data.error, true);
        }
    }
};

// ==========================================
// 3. GẮN SỰ KIỆN KHI TRANG TẢI XONG (INIT)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // Bắt phím Enter cho Form Requester
    document.getElementById('helperPhoneInput').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault(); 
            RequesterFlow.validateHelper();
        }
    });

    // Bắt phím Enter cho Form Helper
    document.getElementById('myPhoneInput').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault(); 
            HelperFlow.fetchRequests();
        }
    });
});