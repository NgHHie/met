package ptit.dblab.app.repository;

import java.util.List;

import ptit.dblab.app.interfaceProjection.TableCreatedInfProjection;
import ptit.dblab.app.repository.Sql.SqlTableCreated;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.TableCreated;

import jakarta.transaction.Transactional;

@Repository
public interface TableCreateRepository extends BaseRepository<TableCreated> {

    @Query(
            value = """
        SELECT * 
        FROM table_created 
        WHERE 
            (created_by = :userId OR is_public = true) 
            AND type_database_id = :typeDatabaseId 
        ORDER BY created_at DESC
        """,
            nativeQuery = true
    )
    List<TableCreated> getListTableCreatedByUserId(@Param("userId") String userId, String typeDatabaseId);
	
	@Modifying
    @Transactional
    @Query(value = "DELETE FROM table_created WHERE name = :name AND prefix = :prefix AND type_database_id = :typeDatabaseId", nativeQuery = true)
    void deleteByNameAndPrefix(@Param("name") String name, @Param("prefix") String prefix, String typeDatabaseId);

    @Query("SELECT t FROM TableCreated t WHERE t.id IN :ids")
    List<TableCreated> findByIdIn(@Param("ids") List<String> ids);

    List<TableCreated> findByTypeDatabaseId(@Param("typeDatabaseId") String typeDatabaseId);

    @Query(value = """
            select tbc.id as id,qd.sql_query as queryCreate FROM table_created as tbc
            JOIN table_detail as tbd ON tbc.id = tbd.table_created_id
            JOIN question_detail as qd ON qd.id = tbd.question_detail_id
            WHERE tbc.type_database_id = :typeDatabaseId
                        """, nativeQuery = true)
    List<TableCreatedInfProjection> getTableInfo(String typeDatabaseId);

    @Query(value = SqlTableCreated.GET_LIST_TABLECREATED_BY_QUESTION_DETAIL_ID,nativeQuery = true)
    List<TableCreated> findTableCreatedByQuestionDetailId(@Param("questionDetailId") String questionDetailId);
}
