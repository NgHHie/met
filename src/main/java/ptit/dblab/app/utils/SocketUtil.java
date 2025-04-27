package ptit.dblab.app.utils;

import ptit.dblab.app.dto.response.SubmitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketUtil {
    private final String TOPIC = "/topic/submit/";
    private final SimpMessagingTemplate messagingTemplate;

    public void sendResultSub(SubmitResponse response, String destination) {
        log.info("*********** send message to user {} *************",destination);
        messagingTemplate.convertAndSend(TOPIC+destination, response);
    }
}
