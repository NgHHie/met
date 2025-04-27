package ptit.dblab.app.repository.Sql;

public class SqlUser {
    public static final String QUERY_SEARCH_USER = """
            SELECT *
            FROM user_entity
            WHERE (first_name ILIKE CONCAT('%', :keyword, '%')
               OR last_name ILIKE CONCAT('%', :keyword, '%')
               OR username ILIKE CONCAT('%', :keyword, '%')) AND role != 'ADMIN' AND role != 'TEACHER';
            """;
    public static final String QUERY_GET_LIST_USER = """
                SELECT * FROM user_entity u WHERE u.role != 'ADMIN'
                AND (
                     :keyword IS NULL
                      OR :keyword = ''
                      OR (u.first_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.last_name ILIKE CONCAT('%', :keyword, '%')
                      OR u.username ILIKE CONCAT('%', :keyword, '%'))
                ) AND (
                    :role IS NULL or u.role = :role
                )
            """;
}
