package study.jpadata.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.jpadata.entity.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryReository {

    private final EntityManager em;

    List<Member> findAllMembers(){
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
