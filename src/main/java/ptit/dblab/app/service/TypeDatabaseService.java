package ptit.dblab.app.service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.response.TypeDatabaseResponse;
import ptit.dblab.app.entity.TypeDatabase;
import ptit.dblab.app.mapper.TypeDatabaseMapper;
import ptit.dblab.app.repository.TypeDatabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeDatabaseService extends BaseService<TypeDatabase, TypeDatabaseRepository> {

    private final TypeDatabaseMapper typeDatabaseMapper;

    public TypeDatabaseService(TypeDatabaseRepository repository, TypeDatabaseMapper typeDatabaseMapper) {
        super(repository);
        this.typeDatabaseMapper = typeDatabaseMapper;
    }

    public TypeDatabase getTypeDatabase(String id) {
        return this.findById(id);
    }

    public List<TypeDatabaseResponse> getAllTypeDatabase() {
        return typeDatabaseMapper.toResponseList(findAll());
    }
}
