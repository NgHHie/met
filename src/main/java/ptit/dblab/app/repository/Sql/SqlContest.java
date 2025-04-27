package ptit.dblab.app.repository.Sql;

public class SqlContest {
    public static final String GET_CONTEST_ID_BY_QUESTION_CONTEST = """
            select q.contest_id as contestId from question_contest as q where q.id = :questionContestId limit 1
            """;

    public static final String FIND_LIST_CONTEST_BY_USER_ID = """
            SELECT *
            FROM contest c
            WHERE (:userId IS NULL OR c.created_by = :userId)
              AND (:status IS NULL OR c.status = :status)
              AND (:keyword IS NULL OR c."name" ILIKE CONCAT('%', :keyword, '%'))
            ORDER BY
              CASE
                WHEN c.status = 'OPEN' AND c.mode = 'EXAM' THEN 1
                WHEN c.status = 'OPEN' THEN 2
                WHEN c.status = 'SCHEDULED' THEN 3
                WHEN c.status = 'CLOSE' THEN 4
                ELSE 5
              END,
              c.created_at DESC;
            """;

    public static final String GET_LIST_USER_DONE_QUESTION_CONTEST = """
            SELECT DISTINCT
                sh.user_id AS "id",
                u.user_code AS "userCode",
                u.first_name AS "firstName",
                u.last_name AS "lastName",
                CASE
                    WHEN MAX(CASE WHEN sh.status = 'AC' THEN 1 ELSE 0 END) = 1
                    THEN 'AC'
                    ELSE 'WA'
                END AS "status"
            FROM submit_contest_exam sh
            JOIN user_entity u ON u.id = sh.user_id
            WHERE sh.question_contest_id = :questionId
            GROUP BY sh.user_id, u.user_code, u.first_name, u.last_name;
            """;
    public final static String GET_COUNT_USER_SUBMIT_QUESTION_CONTEST = """
            SELECT COUNT(DISTINCT sh.user_id)
            FROM submit_contest_exam sh
            WHERE sh.question_contest_id = :questionId
            """;
}
