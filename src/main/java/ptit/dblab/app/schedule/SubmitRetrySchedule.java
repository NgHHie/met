package ptit.dblab.app.schedule;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.interfaceProjection.SubmitContestProjection;
import ptit.dblab.app.interfaceProjection.SubmitPracticeProjection;
import ptit.dblab.app.repository.SubmitContestExamRepository;
import ptit.dblab.app.repository.SubmitHistoryRepository;
import ptit.dblab.app.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitRetrySchedule {

    @Value("${retry-submit.batch-size:100}")
    private int batchSize;
    private final SubmitHistoryRepository submitHistoryRepository;
    private final SubmitContestExamRepository submitContestExamRepository;
    private final SubmitService submitService;

//    @Scheduled(cron = "${scheduling.submit-practice}")
    public void RetrySubmitPractice() {
        try {
            log.info("============= START CHECK RETRY ERROR SUBMIT PRACTICE ===============");
            List<SubmitPracticeProjection> submitHistories = submitHistoryRepository.getSubmitErrorOrRetry(batchSize);
            if(submitHistories.isEmpty()) {
                log.info("== No submit practice to retry");
                return;
            }
            log.info("found {} submit practice to retry", submitHistories.size());
            for(SubmitPracticeProjection submitHistory : submitHistories) {
                SubmitRequest request = new SubmitRequest();
                request.setSql(submitHistory.getQuerySub());
                request.setQuestionId(submitHistory.getQuestionId());
                request.setSubmitContest(false);
                request.setTypeDatabaseId(submitHistory.getDatabaseId());
                submitService.sendRetrySubmit(request,submitHistory.getId(),submitHistory.getUserId());
            }
            log.info("==== retry done for {} submit practice ===", submitHistories.size());
        } catch (Exception e) {
            log.error("failed to retry submit cause: {}", e.getMessage());
        }
    }

//    @Scheduled(cron = "${scheduling.submit-contest}")
    public void RetrySubmitContest() {
        try {
            log.info("============= START CHECK RETRY ERROR SUBMIT CONTEST ===============");
            List<SubmitContestProjection> submitHistories = submitContestExamRepository.getSubmitErrorOrRetry(batchSize);
            if(submitHistories.isEmpty()) {
                log.info("=== No submit contest to retry");
                return;
            }
            log.info("found {} submit contest to retry", submitHistories.size());
            for(SubmitContestProjection submitHistory : submitHistories) {
                SubmitRequest request = new SubmitRequest();
                request.setSql(submitHistory.getQuerySub());
                request.setQuestionId(submitHistory.getQuestionId());
                request.setQuestionContestId(submitHistory.getQuestionContestId());
                request.setSubmitContest(true);
                request.setTypeDatabaseId(submitHistory.getDatabaseId());
                submitService.sendRetrySubmit(request,submitHistory.getId(),submitHistory.getUserId());
            }
            log.info("==== retry done for {} submit contest ===", submitHistories.size());
        } catch (Exception e) {
            log.error("failed to retry submit cause: {}", e.getMessage());
        }
    }
}
