package ptit.dblab.app.utils;

import ptit.dblab.app.repository.*;
import ptit.dblab.app.sequenceTable.ClassRoomSequence;
import ptit.dblab.app.sequenceTable.ContestCodeSequence;
import ptit.dblab.app.sequenceTable.QuestionCodeSequence;
import ptit.dblab.app.sequenceTable.UserCodeSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ptit.dblab.app.repository.*;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class SequenceUtil {
    private static final int MAX_USER_PREFIX_LENGTH = 6;
    private static final int MAX_QUESTION_PREFIX = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final QuestionRepository questionRepository;
    private final QuestionCodeSequenceRepository questionCodeSequenceRepository;
    private final UserCodeSequenceRepository userCodeSequenceRepository;
    private final UserRepository userRepository;
    private final ContestCodeSequenceRepository contestCodeSequenceRepository;
    private final ClassRoomCodeSequenceRepository classRoomCodeSequenceRepository;

    public String generateNewQuestionCode() {
        QuestionCodeSequence sequence = new QuestionCodeSequence();
        questionCodeSequenceRepository.save(sequence);
        Long number = sequence.getId();
        if (number == null) {
            return "SQL1";
        }
        return "SQL" + number;
    }

    public String generateContestCode() {
        ContestCodeSequence sequence = new ContestCodeSequence();
        contestCodeSequenceRepository.save(sequence);
        Long number = sequence.getId();
        if (number == null) {
            return "CT001";
        }
        return String.format("CT%03d", number);
    }

    public String generateClassRoomCode() {
        ClassRoomSequence sequence = new ClassRoomSequence();
        classRoomCodeSequenceRepository.save(sequence);
        Long number = sequence.getId();
        if (number == null) {
            return "Class001";
        }
        return String.format("Class%03d", number);
    }

    public String generateUserCode() {
        UserCodeSequence sequence = new UserCodeSequence();
        userCodeSequenceRepository.save(sequence);
        Long number = sequence.getId();
        if (number == null) {
            return "user001";
        }
        return String.format("user%03d", number);
    }

    public String generatePrefixQuestionCode() {
        boolean isDone = false;
        String prefixCode = "";
        while (!isDone) {
            prefixCode = generateRandomCode(MAX_QUESTION_PREFIX);
            if(!questionRepository.existsByPrefixCode(prefixCode)) {
                isDone = true;
            }
        }
        return prefixCode;
    }

    public String generateSessionPrefix() {
        String uuidPart = UUID.randomUUID().toString().substring(0, 5);

        String timestampPart = Long.toString(Instant.now().toEpochMilli(), 36);
        timestampPart = timestampPart.substring(timestampPart.length() - 4);

        String randomPart = generateRandomCode(3);
        return (uuidPart + timestampPart + randomPart).toLowerCase();
    }

    public String generateUserPrefix() {
        boolean isDone = false;
        String prefixCode = "";
        while (!isDone) {
            System.out.println("generating prefixcode for user...");
            prefixCode = generateRandomCode(MAX_USER_PREFIX_LENGTH);
            if(!userRepository.existsByUserPrefix(prefixCode)) {
                isDone = true;
            }
        }
        return prefixCode;

    }

    public String generateRandomCode(int max_lenght) {
        Random random = new Random();
        StringBuilder code = new StringBuilder(max_lenght);
        for (int i = 0; i < max_lenght; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    public String createPrefix(String userCode,String qcode) {
        return userCode+qcode;
    }
}
