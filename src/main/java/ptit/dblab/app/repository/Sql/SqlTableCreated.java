package ptit.dblab.app.repository.Sql;

public class SqlTableCreated {
    public static final String GET_LIST_TABLECREATED_BY_QUESTION_DETAIL_ID = """
            SELECT tc.* FROM question_detail as qd
            JOIN table_detail as td ON qd.id = td.question_detail_id
            JOIN table_created as tc ON td.table_created_id = tc.id
            WHERE qd.id = :questionDetailId
            """;
}
