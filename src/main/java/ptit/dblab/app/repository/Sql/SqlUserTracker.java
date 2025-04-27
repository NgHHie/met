package ptit.dblab.app.repository.Sql;

public class SqlUserTracker {
    public final static String GET_LOG_CONTEST_DETAIL_BY_USER = """
            SELECT
                u.id AS "userId",
                u.user_code as "userCode",
                u.first_name as "firstName",
            		u.last_name as "lastName",
                ut.action_type as "actionType",
                COUNT(*) AS  "actionCount"
            FROM
                user_tracker ut
            JOIN
                user_entity u ON ut.user_id = u.id
            WHERE ut.contest_id = :contestId and ut.user_id = :userId
            GROUP BY
                u.id, u.user_code, u.first_name,u.last_name, ut.action_type
            ORDER BY
                u.id, ut.action_type;
            """;
}
