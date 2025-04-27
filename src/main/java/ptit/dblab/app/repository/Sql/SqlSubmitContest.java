package ptit.dblab.app.repository.Sql;

public class SqlSubmitContest {
    public static final String COMPETE_QUESTION_CONTEST_BY_USER = """
            SELECT 
                    sh.question_contest_id as questionId,
                    COALESCE(
                        MAX(CASE WHEN sh.status = 'AC' THEN 'AC' END),  
                        MAX(CASE WHEN sh.status = 'WA' THEN 'WA' END),  
                        MAX(CASE WHEN sh.status = 'TLE' THEN 'TLE' END), 
                        MAX(CASE WHEN sh.status = 'CE' THEN 'CE' END),  
                        'not_attempted' 
                    ) AS status,
                    CASE 
                        WHEN COUNT(sh.id) > 0 THEN 'done'
                        ELSE 'not_done'
                    END AS completed
                FROM 
                    submit_contest_exam sh
                WHERE 
                    sh.user_id = :userId
                    AND sh.question_contest_id IN (:questionIds)
                GROUP BY 
                    sh.question_contest_id
                ORDER BY 
                    sh.question_contest_id
            """;
    public static final String USER_STATSICT_SUBMIT_BY_CONTEST_ID = """
            SELECT\s
                u.id,
                u.user_code AS userCode,\s
                CONCAT(u.last_name, ' ', u.first_name) AS fullName,\s
                COUNT(rs.question_contest_id) AS numQuestionDone, \s
                COUNT(CASE WHEN rs.status = 'AC' THEN rs.question_contest_id ELSE NULL END) AS totalSubmitAc,\s
                COALESCE(SUM(rs.point), 0) AS totalPoints,
                COALESCE(MAX(rs.total_submissions), 0) AS totalSubmit \s
            FROM user_contest uc
            JOIN user_entity u ON u.id = uc.user_id
            JOIN question_contest qc ON qc.contest_id = uc.contest_id
            LEFT JOIN (
                SELECT\s
                    sh.user_id,\s
                    sh.question_contest_id,\s
                    sh.point,\s
                    sh.status,
                    ROW_NUMBER() OVER (
                        PARTITION BY sh.user_id, sh.question_contest_id\s
                        ORDER BY CASE WHEN sh.status = 'AC' THEN 1 ELSE 2 END, sh.point DESC, sh.time_submit DESC
                    ) AS rn,
                    COUNT(*) OVER (PARTITION BY sh.user_id) AS total_submissions\s
                FROM submit_contest_exam sh
            ) rs ON rs.user_id = u.id AND rs.question_contest_id = qc.id
            WHERE uc.contest_id = :contestId and (rs.rn = 1 OR rs.rn IS NULL)
            AND (:keyword IS NULL
                      OR u.first_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.last_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.user_code ILIKE CONCAT('%', :keyword, '%')
                      OR u.username ILIKE CONCAT('%', :keyword, '%'))
            GROUP BY u.user_code, u.id, u.first_name, u.last_name, uc.contest_id
            ORDER BY totalPoints DESC, numQuestionDone DESC;
            """;

    public final static String GET_STAT_QUESTION_SUBMIT_CONTEST_BY_USER_ID = """
            SELECT
                uc.user_id as userId,
                qc.id AS questionContestId,
                q.question_code AS questionCode,
                COUNT(sh.id) AS numTries,
                COALESCE(MAX(sh.test_pass), 0) AS maxTestPass,
                COALESCE(MAX(sh.total_test), 0) AS totalTest,
                CASE
                    WHEN COUNT(sh.id) = 0 THEN 'NA'
                    WHEN MAX(CASE WHEN sh.status = 'AC' THEN 1 ELSE 0 END) = 1 THEN 'AC'
                    ELSE 'WA'
                END AS finalStatus
            FROM user_contest uc
            JOIN question_contest qc ON qc.contest_id = uc.contest_id
            JOIN question q ON qc.question_id = q.id
            LEFT JOIN submit_contest_exam sh ON sh.user_id = uc.user_id AND sh.question_contest_id = qc.id
            WHERE uc.contest_id = :contestId
                AND uc.user_id = :userId
            GROUP BY uc.user_id, qc.id, q.question_code
            ORDER BY qc.created_at;
            """;

    public final static String GET_STAT_USER_DETAIL_IN_CONTEST = """
                        SELECT
                            u.user_code AS userCode,
                            CONCAT(u.last_name, ' ', u.first_name) AS fullName,
                            COUNT(DISTINCT s.question_contest_id) AS numQuestionDone,
                            COUNT(s.id) AS totalSubmit,
                            COUNT(DISTINCT CASE WHEN s.status = 'AC' THEN s.question_contest_id END) AS totalCorrectQuestions,
                            SUM(CASE WHEN s.status = 'AC' THEN 1 ELSE 0 END) AS totalSubmitAc,
                            SUM(CASE WHEN s.status = 'WA' THEN 1 ELSE 0 END) AS totalSubmitWa,
                            SUM(CASE WHEN s.status = 'TLE' THEN 1 ELSE 0 END) AS totalSubmitTle,
                            SUM(CASE WHEN s.status = 'CE' THEN 1 ELSE 0 END) AS totalSubmitCe
                        FROM
                            submit_contest_exam s
                        JOIN
                            user_entity u ON s.user_id = u.id
            			JOIN question_contest qc ON qc.id = s.question_contest_id
                        WHERE
                            u.id = :userId and qc.contest_id = :contestId
                        GROUP BY
                            u.user_code, u.first_name, u.last_name;
            """;
    public final static String GET_TOTAL_POINT_USER_CONTEST = """
            SELECT COALESCE(SUM(max_points), 0) AS totalPoints
            FROM (
                   SELECT
                      MAX(s.point) AS max_points
                   FROM
                      submit_contest_exam s
            			 JOIN question_contest qc ON qc.id = s.question_contest_id
                   WHERE s.user_id = :userId and qc.contest_id = :contestId
                   GROUP BY
                         s.question_contest_id
                  ) as question_points
            """;

    public final static String GET_NUMBER_QUESTION_CONTEST_BY_LEVEL = """
            SELECT q."level", COUNT(DISTINCT sh.question_contest_id) AS totalQuestions
                        FROM submit_contest_exam sh
                        JOIN question_contest qc ON sh.question_contest_id = qc.id
            						JOIN question q ON q.id = qc.question_id
                        WHERE sh.user_id = :userId AND qc.contest_id = :contestId
                        GROUP BY q."level";
            """;

    public final static String GET_LIST_QUESTION_CONTEST_USER_SUB = """
            SELECT q.id AS questionId, sh.question_contest_id as questionContestId, q.question_code AS questionCode, q.title AS title,
            CASE WHEN SUM(CASE WHEN sh.status = 'AC' THEN 1 ELSE 0 END) > 0 THEN 1 ELSE 0 END AS isCorrect
            FROM submit_contest_exam sh
            JOIN question_contest qc ON qc.id = sh.question_contest_id
            JOIN question q ON qc.question_id = q.id
            WHERE sh.user_id = :userId AND qc.contest_id = :contestId
            GROUP BY q.id,sh.question_contest_id, q.question_code, q.title;
            """;

    public final static String GET_SUBMIT_CONTEST_RETRY = """
            select s.id,query_sub as "querySub",user_id as "userId",question_contest_id as "questionContestId",qc.question_id as "questionId",database_id as "databaseId"
            from submit_contest_exam s
            JOIN question_contest qc on qc.id = s.question_contest_id
            WHERE s.status is NULL OR is_retry = TRUE LIMIT :batchSize
            """;

    public final static String QUESTION_CONTEST_STAT = """
            SELECT
                qc.id,
            	q.question_code AS questionCode,
            	q.title AS title,
            	COUNT(DISTINCT sh.user_id) AS totalUser,
            	COUNT(DISTINCT CASE WHEN sh.status = 'AC' THEN sh.user_id END) AS totalSubmitCorrect,
            	COUNT(CASE WHEN sh.status = 'AC' THEN 1 END) AS totalSubmitAc,
            	COUNT(sh.id) AS totalSubmissions
            FROM
               question_contest qc
            JOIN question q ON q.id = qc.question_id
            LEFT JOIN
               submit_contest_exam sh ON qc.id = sh.question_contest_id
            WHERE
               q.enable = true and qc.contest_id = :contestId
               AND (:keyword IS NULL OR
                    q.title ILIKE CONCAT('%', :keyword, '%') OR
                    q.question_code ILIKE CONCAT('%', :keyword, '%'))
            GROUP BY
               qc.id,q.id, q.question_code, q.title;
            """;

    public final static String COUNT_IP_USER = """
            SELECT
                user_id as "userId",
                COUNT(DISTINCT ip) AS "ipCount"
            FROM
                submit_contest_exam sc
            JOIN question_contest qc on qc."id" = sc.question_contest_id
            WHERE qc.contest_id = :contestId AND sc.user_id = :userId
            GROUP BY
                user_id
            ORDER BY
                user_id;
            """;

    public final static String GET_STAT_RANGE_NUM_QUESTION_AC_CONTEST = """
            WITH user_question_counts AS (
                SELECT
                    uc.user_id,
                    uc.contest_id,
                    COUNT(DISTINCT s.question_contest_id) AS num_passed_questions\s
                FROM
                    user_contest uc
                JOIN
                    question_contest qc ON qc.contest_id = uc.contest_id 
                LEFT JOIN
                    submit_contest_exam s ON uc.user_id = s.user_id AND s.status = 'AC'
                    AND s.question_contest_id = qc.id
                WHERE
                    uc.contest_id = :contestId
                GROUP BY
                    uc.user_id, uc.contest_id
            ),
            user_ranges AS (
                SELECT
                    user_id,
                    contest_id,
                    CASE
                        WHEN num_passed_questions = 0 THEN '0'
                        WHEN num_passed_questions BETWEEN 1 AND 2 THEN '1-2'
                        WHEN num_passed_questions BETWEEN 3 AND 4 THEN '3-4'
                        WHEN num_passed_questions BETWEEN 5 AND 6 THEN '5-6'
                        WHEN num_passed_questions BETWEEN 7 AND 8 THEN '7-8'
                        WHEN num_passed_questions BETWEEN 9 AND 10 THEN '9-10'
                        WHEN num_passed_questions BETWEEN 11 AND 12 THEN '11-12'
                        WHEN num_passed_questions >= 13 THEN '13+'
                    END AS num_passed_questions_range
                FROM
                    user_question_counts
            )
            SELECT
                all_ranges.num_passed_questions_range as "range",
                COUNT(DISTINCT ur.user_id) AS "numUser"
            FROM
                (
                    VALUES
                        ('0'),
                        ('1-2'),
                        ('3-4'),
                        ('5-6'),
                        ('7-8'),
                        ('9-10'),
                        ('11-12'),
                        ('13+')
                ) AS all_ranges(num_passed_questions_range)
            LEFT JOIN user_ranges ur
                ON ur.num_passed_questions_range = all_ranges.num_passed_questions_range
            GROUP BY
                all_ranges.num_passed_questions_range
            ORDER BY
                CASE
                    WHEN all_ranges.num_passed_questions_range = '0' THEN 1
                    WHEN all_ranges.num_passed_questions_range = '1-2' THEN 2
                    WHEN all_ranges.num_passed_questions_range = '3-4' THEN 3
                    WHEN all_ranges.num_passed_questions_range = '5-6' THEN 4
                    WHEN all_ranges.num_passed_questions_range = '7-8' THEN 5
                    WHEN all_ranges.num_passed_questions_range = '9-10' THEN 6
                    WHEN all_ranges.num_passed_questions_range = '11-12' THEN 7
                    ELSE 8
                END;
            """;
}
