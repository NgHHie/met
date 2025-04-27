package ptit.dblab.app.repository.Sql;

public class SqlQuestion {
    public static final String GET_NUMBER_QUESTION_PRACTICE = """
            select count(id) FROM question
            """;

    public static final String NUMBER_QUESTION_BY_LEVEL = """
            SELECT q."level", COUNT(DISTINCT sh.question_id) AS totalQuestions
            FROM submit_history sh
            JOIN question q ON sh.question_id = q.id
            WHERE sh.user_id = :userId
            GROUP BY q."level";
            """;

    public static final String QUESTION_STAT = """
            SELECT
                q.id,
                q.question_code AS questionCode,
                q.title AS title,
                COUNT(DISTINCT sh.user_id) AS totalUser,
                COUNT(DISTINCT CASE WHEN sh.status = 'AC' THEN sh.user_id END) AS totalSubmitCorrect,
            		COUNT(CASE WHEN sh.status = 'AC' THEN 1 END) AS totalSubmitAc,
                COUNT(sh.id) AS totalSubmissions
            FROM
                question q
            LEFT JOIN
                submit_history sh ON q.id = sh.question_id
            WHERE
                q.enable = true
                AND (:keyword IS NULL OR
                     q.title ILIKE CONCAT('%', :keyword, '%') OR
                     q.question_code ILIKE CONCAT('%', :keyword, '%'))
            GROUP BY
                q.id, q.question_code, q.title;
            """;

    public static final String QUESTION_SUB_INFO = """
            SELECT
                COUNT(*) AS totalSubmissions,
                COUNT(CASE WHEN status = 'AC' THEN 1 END) AS totalSubmitAc
            FROM
                submit_history
            WHERE
                question_id = :questionId;
            """;

    public static final String COUNT_ALL_QUESTION_BY_LEVEL_STAT = """
            SELECT
                level,
                COUNT(*) AS totalQuestions
            FROM
                question
            GROUP BY
                level
            ORDER BY
                level;
            """;

    public static final String GET_LIST_QUESTION_BY_USER_ID = """
            SELECT q.*
            FROM question q
            WHERE q.created_by = :userId or q.is_share = true
              AND (:keyword IS NULL OR LOWER(q.title) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(q.question_code) LIKE CONCAT('%', LOWER(:keyword), '%'))
              AND (:level IS NULL OR q.level = :level)
              AND (:typeQuestion IS NULL OR q.type = :typeQuestion)
              AND (
                :typeDatabaseId IS NULL
                OR EXISTS (
                  SELECT 1
                  FROM question_detail qd
                  WHERE qd.question_id = q.id
                    AND qd.type_database_id = :typeDatabaseId
                )
              )
              AND q.is_deleted = false
            """;

    public static final String GET_LIST_USER_SUBMIT_IN_QUESTION = """
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
            FROM submit_history sh
            JOIN user_entity u ON u.id = sh.user_id
            WHERE sh.question_id = :questionId
            GROUP BY sh.user_id, u.user_code, u.first_name, u.last_name;
            """;
    public static final String GET_COUNT_USER_SUBMIT_IN_QUESTION =
            "SELECT COUNT(DISTINCT sh.user_id) " +
                    "FROM submit_history sh " +
                    "JOIN user_entity u ON u.id = sh.user_id " +
                    "WHERE sh.question_id = :questionId";
}
