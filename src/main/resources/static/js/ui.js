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
      }
};