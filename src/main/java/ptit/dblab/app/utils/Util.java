package ptit.dblab.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import ptit.dblab.app.dto.response.ConstraintResponse;
import ptit.dblab.app.enumerate.ConstraintType;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.utils.JsonUtil;
import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.repository.SubmitContestExamRepository;
import ptit.dblab.app.repository.SubmitHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.entity.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Util {

	private final JsonUtil jsonUtil;
	private final SubmitHistoryRepository submitHistoryRepository;
	private final SubmitContestExamRepository submitContestExamRepository;
	private final ContextUtil contextUtil;

	
	public SubmitResponse createResponse(int statusCode, Object result, double timeExec, String typeQuery) {
	    SubmitResponse response = new SubmitResponse();
	    response.setStatus(statusCode);
	    response.setResult(result);
	    response.setTimeExec(timeExec);
	    response.setTypeQuery(typeQuery);
	    return response;
	}
	
	public SubmitResponse createResponse(int statusCode, Object result) {
	    SubmitResponse response = new SubmitResponse();
	    response.setStatus(statusCode);
	    response.setResult(result);
	    return response;
	}
	
	public int getExecuteTime(long startTime) {
		long duration = System.currentTimeMillis() - startTime;
		return (int) duration;
	}
	
	public TableDetail containTableId(List<TableDetail> tables, String tableId) {
		if(Objects.isNull(tables) || tables.isEmpty()) return null;
		for(TableDetail table : tables) {
			if(table.getTableCreated().getId().equals(tableId)) return table;
		}
		return null;
	}

	public TableDetail containTableName(List<TableDetail> tables,String tableName) {
		for(TableDetail table : tables) {
			if(table.getTableCreated().getName().equalsIgnoreCase(tableName)) return table;
		}
		return null;
	}

	public boolean compareListMaps(List<Map<String, Object>> answers, List<Map<String, Object>> subs) {
		if (answers == null || subs == null) {
			return false;
		}

		if (answers.size() != subs.size()) {
			return false;
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			for (int i = 0; i < answers.size(); i++) {
				Map<String, Object> answerMap = answers.get(i);
				Map<String, Object> subMap = subs.get(i);

				// Chuyển đổi map thành chuỗi JSON với các khóa được sắp xếp
				String answerJson = jsonUtil.convertObjectToString(answerMap);
				String subJson = jsonUtil.convertObjectToString(subMap);

				// Tạo giá trị băm cho từng chuỗi JSON
				String answerHash = bytesToHex(digest.digest(answerJson.getBytes(StandardCharsets.UTF_8)));;
				String subHash = bytesToHex(digest.digest(subJson.getBytes(StandardCharsets.UTF_8)));;
				System.out.println("case "+i+" done!");
				// So sánh giá trị băm
				if (!answerHash.equals(subHash)) {
					return false;
				}
			}
			return true;

//			Map<String, Integer> countMap1 = getCountMap(answers);
//			Map<String, Integer> countMap2 = getCountMap(subs);
//			return countMap1.equals(countMap2);

		} catch (JsonProcessingException e) {
			log.error("Error processing JSON", e);
			return false;
		} catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	private Map<String, Integer> getCountMap(List<Map<String, Object>> list) throws JsonProcessingException, NoSuchAlgorithmException {
		Map<String, Integer> countMap = new HashMap<>();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		for (Map<String, Object> map : list) {
			// Chuyển đổi map thành chuỗi JSON với các khóa được sắp xếp
			String jsonString = jsonUtil.convertObjectToString(map);
			String hash = bytesToHex(digest.digest(jsonString.getBytes(StandardCharsets.UTF_8)));
			countMap.put(hash, countMap.getOrDefault(jsonString, 0) + 1);
		}

		return countMap;
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

//	private boolean compareMaps(Map<String, Integer> result, Map<String, Integer> sub) {
//		if (result.size() != sub.size()) {
//			return false;
//		}
//		for (Map.Entry<String, Integer> entry : result.entrySet()) {
//			String key = entry.getKey();
//			System.out.println(key);
//			Integer value1 = entry.getValue();
//			Integer value2 = sub.get(key);
//			if(Objects.isNull(value1) || Objects.isNull(value2)) {
//				return false;
//			}
//			if(!value1.equals(value2)) {
//				return false;
//			}
//		}
//		return true;
//	}

	public SubmitResponse createResponseScrored(SubmitRequest request, String ip) {
		SubmitResponse response = new SubmitResponse();
		response.setStatus(ErrorCode.PROCCESSING.getCode());
		LocalDateTime now = LocalDateTime.now();
		response.setTimeSubmit(now);

		if(!request.isSubmitContest()) {
			SubmitHistory submitHistory = new SubmitHistory();
			submitHistory.setTimeSubmit(now);
			submitHistory.setUser(User.builder().id(contextUtil.getUserId()).build());
			Question question = new Question();
			question.setId(request.getQuestionId());
			submitHistory.setQuerySub(request.getSql().trim());
			submitHistory.setQuestion(question);
			submitHistory.setDatabase(TypeDatabase.builder().id(request.getTypeDatabaseId()).build());
			submitHistory.setIp(ip);
			submitHistory.setIsRetry(false);
			submitHistoryRepository.save(submitHistory);
			response.setSubmitId(submitHistory.getId());
		} else {
			SubmitContestExam submitHistory = new SubmitContestExam();
			submitHistory.setTimeSubmit(now);
			submitHistory.setUser(User.builder().id(contextUtil.getUserId()).build());
			QuestionContest question = new QuestionContest();
			question.setId(request.getQuestionContestId());
			submitHistory.setQuerySub(request.getSql().trim());
			submitHistory.setQuestionContest(question);
			submitHistory.setDatabase(TypeDatabase.builder().id(request.getTypeDatabaseId()).build());
			submitHistory.setIp(ip);
			submitHistory.setIsRetry(false);
			submitContestExamRepository.save(submitHistory);
			response.setSubmitId(submitHistory.getId());
		}


		return response;
	}

	public List<ConstraintResponse> convertToConstraints(String constraints) {
		return jsonUtil.fromJson(new TypeReference<List<ConstraintResponse>>() {},constraints);
	}

	public String validateConstraints(String sqlQuery, List<ConstraintResponse> constraints) {
		String queryLower = sqlQuery.toLowerCase();


		for (ConstraintResponse constraint : constraints) {
			String keyword = constraint.getKeyword().toLowerCase();
			int expectedTimes = constraint.getTimes();
			int actualCount = countOccurrences(queryLower, keyword);

			if (ConstraintType.INCLUDE.name().equalsIgnoreCase(constraint.getType())) {
				if (actualCount < expectedTimes) {
					return String.format("SQL is not allowed: must include '%s' at least %d time(s)", keyword, expectedTimes);
				}
			} else if (ConstraintType.EXCLUDE.name().equalsIgnoreCase(constraint.getType())) {
				if (actualCount >= expectedTimes) {
					return String.format("SQL is not allowed: must not include '%s' %d time(s) or more", keyword, expectedTimes);
				}
			}
		}

		return "VALID";
	}

	private int countOccurrences(String text, String keyword) {
		int count = 0, index = 0;
		while ((index = text.indexOf(keyword, index)) != -1) {
			count++;
			index += keyword.length();
		}
		return count;
	}
}
