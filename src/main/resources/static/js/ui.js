const UIService = {
    // Render Log Text vào Container
      logTo: function(elementId, msg, isError = false) {
            const el = document.getElementById(elementId);
            if(!el) return;
            const className = isError ? "error" : "success";
            el.innerHTML = `<div class="log ${className}">${new Date().toLocaleTimeString()} - ${msg}</div>`;
      },
      
      // Hiển thị Toast Notification rơi từ trên xuống
      showToast: function(message, type, paneId) {
            const pane = document.getElementById(paneId);
            if (!pane) return;

            const toast = document.createElement('div');
            toast.className = `custom-toast ${type}`;
            toast.innerText = message;
            pane.appendChild(toast);

            setTimeout(() => { toast.classList.add('show'); }, 10);

            setTimeout(() => {
                  toast.classList.remove('show');
                  setTimeout(() => { pane.removeChild(toast); }, 500); 
            }, 3000);
      },

      startCountdown: function(buttonId, displayId, storageKey, durationSeconds) {
            const btn = document.getElementById(buttonId);
            if (!btn) return;
            
            // Lưu thời điểm kết thúc (miligiây) vào trình duyệt
            const targetTime = Date.now() + durationSeconds * 1000;
            localStorage.setItem(storageKey, targetTime);
            
            this.resumeCountdown(buttonId, displayId, storageKey);
      },

      // Phục hồi đồng hồ (dùng khi load lại trang hoặc chạy tiếp đếm ngược)
      resumeCountdown: function(buttonId, displayId, storageKey) {
            const btn = document.getElementById(buttonId);
            const display = document.getElementById(displayId);
            const targetTime = localStorage.getItem(storageKey);

            if (!targetTime || !btn || !display) return;

            const updateTimer = () => {
                  const now = Date.now();
                  const diff = Math.floor((targetTime - now) / 1000);

                  // Nếu đã hết giờ
                  if (diff <= 0) {
                        btn.disabled = false;
                        btn.style.opacity = '1';
                        btn.style.cursor = 'pointer';
                        display.style.display = 'none';
                        display.innerHTML = '';
                        localStorage.removeItem(storageKey);
                        return;
                  }

                  // Khóa nút
                  btn.disabled = true;
                  btn.style.opacity = '0.6';
                  btn.style.cursor = 'not-allowed';
                  display.style.display = 'block';

                  // Tính toán Giờ : Phút : Giây
                  const h = Math.floor(diff / 3600).toString().padStart(2, '0');
                  const m = Math.floor((diff % 3600) / 60).toString().padStart(2, '0');
                  const s = (diff % 60).toString().padStart(2, '0');
                  
                  if (h === '00') {
                        display.innerHTML = `<span style="color:var(--error-text); font-weight:bold;">Gửi lại sau: ${m}:${s}</span>`;
                  } else {
                        display.innerHTML = `<span style="color:var(--error-text); font-weight:bold;">Gửi lại sau: ${h}:${m}:${s}</span>`;
                  }
                  
                  setTimeout(updateTimer, 1000); // Lặp lại sau 1 giây
            };

            updateTimer();
      }
};