package ptit.dblab.app.schedule;

import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.repository.ContestRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Getter
@Slf4j
public class ContestSchedule {
    private final ContestRepository contestRepository;

    private final List<Contest> cachedContests = new CopyOnWriteArrayList<>();

    public ContestSchedule(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
        refreshContest();
    }


    public synchronized void refreshContest() {
        LocalDate today = LocalDate.now();
        List<Contest> contestsToUpdate = contestRepository.findListContestToday(today);
        log.info("\n\n======== loading {} contests from database =======",contestsToUpdate.size());
        cachedContests.clear();
        cachedContests.addAll(contestsToUpdate);
    }

//    @Scheduled(cron = "${scheduling.contest-cron}") // Every 1 minute
    @Transactional
    public void updateContestStatuses() {
        LocalDateTime now = LocalDateTime.now();
        log.info("== check contest open at : {}", now);
        if (now.getMinute() == 0 && now.getSecond() == 0) {
            refreshContest();
        }
        List<Contest> contests = getCachedContests();
        log.info("============ start scheduling contests number contest handle: {} ============",contests.size());
        boolean isUpdate = false;
        for (Contest contest : contests) {
            LocalDateTime startDateTime = contest.getStartDateTime();
            LocalDateTime endDateTime = contest.getEndDateTime();

            if (startDateTime != null && endDateTime != null) {
                if (now.isAfter(startDateTime) && now.isBefore(endDateTime) && contest.getStatus() != ContestStatus.OPEN) {
                    contest.setStatus(ContestStatus.OPEN);
                    log.info("=========== OPEN CONTEST {} success", Objects.nonNull(contest.getName()) ? contest.getName() : "null");
                    isUpdate = true;
                } else if (now.isAfter(endDateTime) && contest.getStatus() != ContestStatus.CLOSE) {
                    contest.setStatus(ContestStatus.CLOSE);
                    isUpdate = true;
                    log.info("=========== CLOSE CONTEST {} success", Objects.nonNull(contest.getName()) ? contest.getName() : "null");
                }
            }
        }

        if(isUpdate) {
            contestRepository.saveAll(contests);
            log.info("====== update contests status success ======");
        }
    }
}
