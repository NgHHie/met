package ptit.dblab.app.repository.Sql;

public class SqlUserContest {
    public static final String CHECK_JOIN_CONTEST = """
            SELECT c.id AS contestId,
                   CASE
                       WHEN uc.user_id IS NOT NULL THEN 1
                       ELSE 0
                   END AS joined
            FROM contest c
            LEFT JOIN user_contest uc
                   ON c.id = uc.contest_id
                   AND uc.user_id = :userId
            WHERE c.id IN (:contestIds);
            """;
}
