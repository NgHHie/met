package ptit.dblab.app.service;

import ptit.dblab.app.config.CheatConfig;
import ptit.dblab.app.dto.request.UserTrackerRequest;
import ptit.dblab.app.dto.response.CheatUserResponse;
import ptit.dblab.app.dto.response.TrackerDetailResponse;
import ptit.dblab.app.dto.response.UserTrackerResponse;
import ptit.dblab.app.entity.CheatUser;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.entity.UserTracker;
import ptit.dblab.app.interfaceProjection.UserIpCountProjection;
import ptit.dblab.app.interfaceProjection.UserTrackerProjection;
import ptit.dblab.app.mapper.CheatUserMapper;
import ptit.dblab.app.mapper.UserTrackerMapper;
import ptit.dblab.app.repository.CheatUserRepository;
import ptit.dblab.app.repository.SubmitContestExamRepository;
import ptit.dblab.app.repository.UserTrackerRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserTrackerService {
    private static final Logger log = LoggerFactory.getLogger(UserTrackerService.class);
    private final UserTrackerRepository userTrackerRepository;
    private final CheatUserRepository cheatUserRepository;
    private final UserTrackerMapper userTrackerMapper;
    private final CheatConfig cheatConfig;
    private final CheatUserMapper cheatUserMapper;
    private final SubmitContestExamRepository submitContestExamRepository;


    public void saveLogTracker(UserTrackerRequest request) {
        UserTracker userTracker = userTrackerMapper.toEntity(request);
        userTracker.setUser(User.builder().id(request.getUserId()).build());
        userTrackerRepository.save(userTracker);
        if(!cheatUserRepository.existsByUserIdAndContestId(request.getUserId(), request.getContestId())) {
            handleCheckCheat(request.getUserId(), request.getContestId());
        }
        log.info("save log tracker done!");
    }

    public Page<UserTrackerResponse> getUserTrackers(Pageable pageable, String contestId,String keyword) {
        Page<UserTracker> userTrackerPage = userTrackerRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(contestId != null) {
                predicates.add(criteriaBuilder.equal(root.get("contestId"),contestId));
            }
            if(keyword != null) {
                Join<Object, Object> userJoin = root.join("user", JoinType.INNER);
                Predicate firstNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("firstName")), "%" + keyword.toLowerCase() + "%"
                );
                Predicate lastNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("lastName")), "%" + keyword.toLowerCase() + "%"
                );
                Predicate usernamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(userJoin.get("userCode")), "%" + keyword.toLowerCase() + "%"
                );
                predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate, usernamePredicate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
          }, pageable
        );
        return userTrackerPage.map(userTrackerMapper::toResponse);
    }

    public Page<CheatUserResponse> getCheatUser(Pageable pageable,String contestId) {
        Page<CheatUser> cheatUserPage = cheatUserRepository.findByContestIdOrderByCreatedAtDesc(pageable,contestId);
        return cheatUserPage.map(cheatUserMapper::toResponse);
    }

    public TrackerDetailResponse getTrackerDetails(String userId, String contestId) {
        return TrackerDetailResponse.builder()
                .actions(userTrackerRepository.getUserTrackerLogDetail(userId, contestId))
                .countIp(submitContestExamRepository.getUserIpCount(contestId,userId))
                .build();
    }

    public void handleCheckCheat(String userId, String contestId) {
        List<UserTrackerProjection> logs = userTrackerRepository.getUserTrackerLogDetail(userId,contestId);

        if (!logs.isEmpty()) {
            log.info("Cheat configuration: {}", cheatConfig.toString());
            boolean isCheat = false;

            for (UserTrackerProjection logDetail : logs) {
                switch (logDetail.getActionType()) {

                    case "TAB_RETURN":
                        if (logDetail.getActionCount() > cheatConfig.getMaxTimeReturnTab()) {
                            isCheat = true;
                        }
                        break;
                    case "COPY":
                        if (logDetail.getActionCount() > cheatConfig.getMaxTimeCopy()) {
                            isCheat = true;
                        }
                        break;
                    case "PASTE":
                        if (logDetail.getActionCount() > cheatConfig.getMaxTimePaste()) {
                            isCheat = true;
                        }
                        break;
                    case "TAB_SWITCH":
                        if (logDetail.getActionCount() > cheatConfig.getMaxTimeSwitchTab()) {
                            isCheat = true;
                        }
                        break;
                    default:
                        log.info("Unknown action type: {}", logDetail.getActionType());
                }

                if (isCheat) {
                    break;
                }
            }
            UserIpCountProjection userIpCount = submitContestExamRepository.getUserIpCount(contestId,userId);
            if(Objects.nonNull(userIpCount)) {
                if(userIpCount.getIpCount() > 1) {
                    isCheat = true;
                }
            }

            // Save cheat user if detected
            if (isCheat) {
                log.info("===> Check cheat: User ID {} is cheating in contest {}", userId, contestId);
                CheatUser cheatUser = CheatUser.builder()
                        .user(User.builder().userPrefix(userId).build())
                        .contestId(contestId)
                        .build();
                cheatUserRepository.save(cheatUser);
            }
        }
    }

}
