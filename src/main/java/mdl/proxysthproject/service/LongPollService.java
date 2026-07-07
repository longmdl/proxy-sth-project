package mdl.proxysthproject.service;

import mdl.proxysthproject.model.NfcResultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LongPollService {
    
    @Value("${nfc.longpoll.timeout-seconds:30}")
    private long timeoutSeconds;

    // Registry of waiting clients: Ticket ID -> DeferredResult
    private final Map<String, DeferredResult<NfcResultResponse>> registry = new ConcurrentHashMap<>();

    public DeferredResult<NfcResultResponse> createDeferredResult(String ticketId) {
        // If there's an existing one, we could complete it with a timeout to avoid leaks,
        // or just replace it. Here we replace it but complete the old one first if present.
        DeferredResult<NfcResultResponse> existing = registry.get(ticketId);
        if (existing != null) {
            existing.setResult(new NfcResultResponse(ticketId, null, "Superseded by new poll", null));
        }

        DeferredResult<NfcResultResponse> deferredResult = new DeferredResult<>(timeoutSeconds * 1000L);
        
        deferredResult.onTimeout(() -> {
            registry.remove(ticketId);
            // Return a specific response indicating the client should poll again
            deferredResult.setResult(new NfcResultResponse(ticketId, null, "POLL_TIMEOUT", null));
        });

        deferredResult.onCompletion(() -> registry.remove(ticketId));
        
        registry.put(ticketId, deferredResult);
        return deferredResult;
    }

    public void resolve(String ticketId, NfcResultResponse response) {
        DeferredResult<NfcResultResponse> deferredResult = registry.remove(ticketId);
        if (deferredResult != null) {
            deferredResult.setResult(response);
        }
    }
}
