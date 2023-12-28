package study.jpadata.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.jpadata.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext //스프링 컨테이너가 JPA에있는 영속성 컨텍스트를 가져옴
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    //순수 Jpa Paging
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc ", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset) //어디서 부터 가져올것인지?(시작점)
                .setMaxResults(limit) //어디까지 가져올 것인지(끝점) selete결과의 offset번째 부터 limite번쨰 까지 가져온다
                .getResultList();
    }

    //토탈 카운트(몇 번째 페이지인지)
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    //벌크 업데이트 유저 나이 수정
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1"
                        + " where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
