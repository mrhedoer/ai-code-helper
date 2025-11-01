package com.hejunhao.aicodehelper;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    
    @Transactional
    void deleteByConversationId(String conversationId);
}
