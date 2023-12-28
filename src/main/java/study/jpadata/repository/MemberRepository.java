package study.jpadata.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.jpadata.dto.MemberDto;
import study.jpadata.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    //구현체 없는데 어케 동작함?
    //스프링이 인터페이스를 보고 구현체를 만들어서 인젝션 걸어줌

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername") //없어도 됨 실행 순서 NameQuery > 구현체 순이라 엔터티에 . 붙인 형태로 미리 찾아봄(순서 변경 가능)
    List<Member> findByUsername(@Param("username") String username);

    //jpql 인터페이스에 바로 생성가능
    //복잡한 jpql 생성 가능, 메서드 명 간략화
    //애플리케이션 로딩 시점에 파싱 후 오류발생
    //쿼리 DSL? 동적쿼리
    @Query("select m from Member m where m.username = :username and m.age = :age") 
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    
    //단순값, Dto 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //dto 조회시 new operation 다써줘야됨(패키지 명)
    @Query("select new study.jpadata.dto.MemberDto (m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);
    
    //반화 타입 종류 (Spring Data Jpa는 반환타입을 유연하게 설정 가능하다)
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    //paging
    //Pageable = 페이징 조건
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count (m) from Member m")  //카운트 쿼리 분리
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //bulk
    //jpa bulk성 업데이트 주의사항
    //jpa는 영속성 컨테이너가 엔티티를 관리하지만 벌크연산은 그걸 무시하고 DB에 떄려박음
    //나이가 40인 멤버를 벌크 업데이트로 41로 바꾼 후 멤버를 다시 불러오면 jpa영속성 컨테이너 안에서 가져오기 때문에 나이가 40으로 나옴
    //실제 DB에는 나이가 41로 되어있음
    //이 문제를 해결하기 위해 entityManager에 flush(남아있는 변경사항이 DB에 반영)와 clear를 통해 영속성 컨테이너를 날려줘야됨
    @Modifying(clearAutomatically = true) // = executeUpdate 이게 있어야 getSingleResult 호출 안함, clearAutomatically 쿼리 실행 후 clear과정 자동으로 실행
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //JPQL로 멤버 조회시 팀도 같이 조회하기
    @Query("select m from Member m left join fetch m.team") //Member를 조회할 때 Team도 같이 가져옴
    List<Member> findMemberFetchJoin();

    //EntityGraph로 멤버 조회시 팀도 같이조회
    //내부적으로는 fecth join사용
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //메서드 명으로 EntityGraph사용
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //JPA Hint & Lock

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //select for update 데이터베이스에 셀렉트 할 때 다른애들 손못대게 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
