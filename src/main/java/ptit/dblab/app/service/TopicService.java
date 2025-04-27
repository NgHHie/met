package ptit.dblab.app.service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.request.TopicRequest;
import ptit.dblab.app.dto.response.TopicCountResponse;
import ptit.dblab.app.dto.response.TopicDetailResponse;
import ptit.dblab.app.dto.response.TopicResponse;
import ptit.dblab.app.entity.Topic;
import ptit.dblab.app.mapper.TopicMapper;
import ptit.dblab.app.repository.TopicRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService extends BaseService<Topic, TopicRepository> {

    private final TopicMapper topicMapper;

    public TopicService(TopicRepository repository, TopicMapper topicMapper) {
        super(repository);
        this.topicMapper = topicMapper;
    }

    public void createTopic(TopicRequest request) {
        Topic topic = topicMapper.toEntity(request);
        repository.save(topic);
    }

    public Page<TopicResponse> getTopics(Pageable pageable, String direction, String keyword) {
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(sortDirection, "createdAt"));

        Page<Topic> topicPage = this.repository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                Predicate titlePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), "%" + keyword.trim().toLowerCase() + "%"
                );
                predicates.add(titlePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, sortedPageable);

        return topicPage.map(topicMapper::toResponse);
    }

    public TopicDetailResponse getTopicDetail(String topicId) {
        this.repository.incrementViews(topicId);
        return topicMapper.toDetailResponse(findById(topicId));
    }

    public TopicCountResponse getNumberTopicNew() {
        return TopicCountResponse.builder().topicCount(this.repository.countTopicNew()).build();
    }
}
