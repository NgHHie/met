package ptit.dblab.app.repository.Sql;

public class SqlSubmitHistory {
    public static final String GET_SUBMIT_HIS_BY_USER_AND_QUESTION = """
            SELECT * FROM submit_history WHERE user_id = :userId AND question_id = :questionId
            """;
    public static final String GET_SUBMIT_HIS_BY_USER= """
            SELECT * FROM submit_history WHERE user_id = :userId
            """;
    public static final String STAT_USER_SUBMIT = """
            SELECT 
              u.id,
              u.user_code AS userCode, 
              CONCAT(u.first_name, ' ', u.last_name) AS fullName, 
              COUNT(rs.question_id) AS numQuestionDone,  
              COUNT(CASE WHEN rs.status = 'AC' THEN rs.question_id ELSE NULL END) AS totalSubmitAc, 
              SUM(rs.point) AS totalPoints,
              MAX(rs.total_submissions) AS totalSubmit  
           FROM (
               SELECT 
                  sh.user_id, 
                  sh.question_id, 
                  sh.point, 
                  sh.status,
                  ROW_NUMBER() OVER (
                      PARTITION BY sh.user_id, sh.question_id 
                      ORDER BY CASE WHEN sh.status = 'AC' THEN 1 ELSE 2 END, sh.point DESC, sh.time_submit DESC
                  ) AS rn,
                  COUNT(*) OVER (PARTITION BY sh.user_id) AS total_submissions 
               FROM submit_history sh
           ) rs
           JOIN user_entity u ON u.id = rs.user_id
           WHERE rs.rn = 1 
            AND
            (:classRoomId IS NULL OR u.id IN (SELECT user_id FROM user_class_room WHERE class_room_id = :classRoomId))
            AND (
                     :keyword IS NULL
                      OR :keyword = ''
                      OR (u.first_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.last_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.username ILIKE CONCAT('%', :keyword, '%')
                      OR u.user_code ILIKE CONCAT('%', :keyword, '%'))
                )
           GROUP BY u.user_code,u.id, u.first_name, u.last_name
           ORDER BY totalPoints DESC, numQuestionDone DESC
            """;
    public static final String COMPETE_QUESTION_BY_USER = """
            SELECT 
                    sh.question_id,
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
                    submit_history sh
                WHERE 
                    sh.user_id = :userId
                    AND sh.question_id IN (:questionIds)
                GROUP BY 
                    sh.question_id
                ORDER BY 
                    sh.question_id
            """;

    public static final String USER_DETAIL_STAT_SUBMIT = """
            SELECT
                u.user_code AS userCode,
                CONCAT(u.last_name, ' ', u.first_name) AS fullName,
                COUNT(DISTINCT s.question_id) AS numQuestionDone,
                COUNT(s.id) AS totalSubmit,

                COUNT(DISTINCT CASE WHEN s.status = 'AC' THEN s.question_id END) AS totalCorrectQuestions,
                SUM(CASE WHEN s.status = 'AC' THEN 1 ELSE 0 END) AS totalSubmitAc,
                SUM(CASE WHEN s.status = 'WA' THEN 1 ELSE 0 END) AS totalSubmitWa,
                SUM(CASE WHEN s.status = 'TLE' THEN 1 ELSE 0 END) AS totalSubmitTle,
                SUM(CASE WHEN s.status = 'CE' THEN 1 ELSE 0 END) AS totalSubmitCe
            FROM
                submit_history s
            JOIN
                user_entity u ON s.user_id = u.id
            WHERE
                u.id = :userId
            GROUP BY
                u.user_code, u.first_name, u.last_name;
            """;

    public final static String GET_TOTAL_POINT_USER = """
                SELECT
                    SUM(max_points) AS totalPoints
                FROM (
                    SELECT
                        MAX(s.point) AS max_points
                    FROM
                        submit_history s
                		WHERE s.user_id = :userId
                    GROUP BY
                        s.question_id
                ) as question_points
            """;

    public static final String GET_NUMBER_USER_JOIN_PRACTICE = """
            select count(DISTINCT user_id) FROM submit_history
            """;

    public static final String GET_QUESTION_USER_SUB = """
            SELECT q.id AS questionId, q.question_code AS questionCode, q.title AS title,
                                      CASE WHEN SUM(CASE WHEN sh.status = 'AC' THEN 1 ELSE 0 END) > 0 THEN 1 ELSE 0 END AS isCorrect
                                      FROM submit_history sh
                                      JOIN question q ON sh.question_id = q.id
                                      WHERE sh.user_id = :userId
                                      GROUP BY q.id, q.question_code, q.title;
            """;
    public static final String TOP_USER_SUBMIT_PRACTICE = """
            SELECT
            u.id,
            u.user_code AS userCode,
            u.avatar,
            CONCAT(u.first_name, ' ', u.last_name) AS fullName,
            COUNT(rs.question_id) AS numQuestionDone,
            SUM(rs.point) AS totalPoints
                       FROM (
                           SELECT
                              sh.user_id,
                              sh.question_id,
                              sh.point,
                              sh.status,
                              ROW_NUMBER() OVER (
                                  PARTITION BY sh.user_id, sh.question_id
                                  ORDER BY CASE WHEN sh.status = 'AC' THEN 1 ELSE 2 END, sh.point DESC, sh.time_submit DESC
                              ) AS rn,
                              COUNT(*) OVER (PARTITION BY sh.user_id) AS total_submissions
                           FROM submit_history sh
                       ) rs
                       JOIN user_entity u ON u.id = rs.user_id
                       WHERE rs.rn = 1
                       GROUP BY u.user_code,u.id, u.first_name, u.last_name,u.avatar
                       ORDER BY totalPoints DESC, numQuestionDone DESC
            """;

    public final static String GET_USER_JOINED = """
            SELECT
                u.id AS userId,
            		u.user_code as userCode,
                u.username AS userName,
                u.first_name AS firstName,
                u.last_name AS lastName
            FROM
                submit_history s
            JOIN
                user_entity u ON s.user_id = u.id
            WHERE :keyword IS NULL
                      OR (u.first_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.last_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.username ILIKE CONCAT('%', :keyword, '%'))
            GROUP BY
                u.id, u.user_code,u.username, u.first_name, u.last_name;
            """;

    public static final String GET_USER_SUB_COUNT = """
            SELECT
                DATE(time_submit) AS submit_date,
                user_id AS user_id,
                COUNT(*) AS total_submit
            FROM
                submit_history
            WHERE
                DATE(time_submit) BETWEEN\s
                    CASE
                        WHEN :intervalType = 'week' THEN CURRENT_DATE - INTERVAL '7 days'
                        WHEN :intervalType = 'month' THEN CURRENT_DATE - INTERVAL '1 month'
                        ELSE CURRENT_DATE - INTERVAL '7 days'
                    END
                    AND CURRENT_DATE
                AND user_id = :userId
            GROUP BY
                submit_date, user_id
            ORDER BY
                submit_date, user_id;
            """;
    public static final String GET_TOTAL_SUBMIT_BY_INTERVAL = """
        SELECT
            submit_date AS submitDate,
            COUNT(*) AS totalSubmit
        FROM (
            SELECT
                CASE
                    WHEN :intervalType = 'week' THEN DATE_TRUNC('week', time_submit)
                    WHEN :intervalType = 'month' THEN DATE_TRUNC('month', time_submit)
                    WHEN :intervalType = 'year' THEN DATE_TRUNC('year', time_submit)
                    ELSE DATE(time_submit)
                END AS submit_date
            FROM (
                SELECT time_submit
                FROM submit_history
                WHERE time_submit >= CASE
                    WHEN :intervalType = 'day' THEN CURRENT_DATE - INTERVAL '7 days'
                    WHEN :intervalType = 'week' THEN CURRENT_DATE - INTERVAL '3 weeks'
                    WHEN :intervalType = 'month' THEN DATE_TRUNC('year', CURRENT_DATE)
                    WHEN :intervalType = 'year' THEN DATE_TRUNC('year', CURRENT_DATE - INTERVAL '1 year')
                    ELSE CURRENT_DATE - INTERVAL '7 days'
                END
                AND time_submit < CURRENT_DATE + INTERVAL '1 day'
        
                UNION ALL
        
                SELECT time_submit
                FROM submit_contest_exam
                WHERE time_submit >= CASE
                    WHEN :intervalType = 'day' THEN CURRENT_DATE - INTERVAL '7 days'
                    WHEN :intervalType = 'week' THEN CURRENT_DATE - INTERVAL '3 weeks'
                    WHEN :intervalType = 'month' THEN DATE_TRUNC('year', CURRENT_DATE)
                    WHEN :intervalType = 'year' THEN DATE_TRUNC('year', CURRENT_DATE - INTERVAL '1 year')
                    ELSE CURRENT_DATE - INTERVAL '7 days'
                END
                AND time_submit < CURRENT_DATE + INTERVAL '1 day'
            ) combined_tables
        ) subquery
        GROUP BY
            submit_date
        ORDER BY
            submit_date;
        """;

    public final static String GET_SUBMIT_PRACTICE_RETRY = """
            select id,query_sub as querySub,user_id as userId,question_id as questionId,database_id as databaseId from submit_history s
            WHERE s.status is NULL OR is_retry = TRUE LIMIT :batchSize
            """;
}
