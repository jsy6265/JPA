package study.jpadata.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpadata.dto.MemberDto;
import study.jpadata.entity.Member;
import study.jpadata.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("A");
        Member savedMember = memberRepository.save(member);

        //있을수도 있고 없을수도 있다?
        Optional<Member> findMember = memberRepository.findById(savedMember.getId());
        Member find = null;
        if(findMember.isPresent()){
             find = findMember.get();
        }

        assertThat(find.getId()).isEqualTo(member.getId());
        assertThat(find.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        //업데이트
        findMember1.setUsername("member3");
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long count2 = memberRepository.count();
        assertThat(count2).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        System.out.println(result.size());
        System.out.println(result.get(0).getAge());
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);

    }

    @Test
    public void testNamedQuerty(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuerty(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();

       for(String i : result){
           System.out.println("i = " + i);
       }
    }

    @Test
    public void findMemberDto(){

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();

        for(MemberDto i : result){
            System.out.println("i = " + i);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for(Member i : result){
            System.out.println("i = " + i);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        
        //없는 데이터 조회시 null이 아닌 empty 컬렉션 반환?
        List<Member> finduser1 = memberRepository.findListByUsername("AAA");
        
        //없는 데이터 조회시 null 반환
        //Spring Data Jpa가 익셉션을 감싸서 null 반환함
        Member finduser2 = memberRepository.findMemberByUsername("AAA");

        //데이터가 있을지 없을지 모를떄는 옵셔널 쓰기(옵셔널은 없을수 있다는걸 가정하고 사용)
        //단건 조회에서 두개이상의 값이 있다면 오류발생
        Optional<Member> finduser3 = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging(){

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));
        memberRepository.save(new Member("member8", 10));

        int age = 10;
        //페이징 조건
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //Page로 반환타입을 받으면 totalCount 쿼리 알아서 날림
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        //엔터티 Dto로 변환
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        //페이지안에 컨텐츠 꺼내기
        List<Member> content = page.getContent();

        //Page에서 토탈카운트 가져오기
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(8);
        //현재 몇번째 페이지 인지
        assertThat(page.getNumber()).isEqualTo(0);
        //전체페이지 개수
        assertThat(page.getTotalPages()).isEqualTo(3);
        //현재 페이지가 첫번째인지
        assertThat(page.isFirst()).isTrue();
        //다음 페이지가 있는지
        assertThat(page.hasNext()).isTrue();

        for (Member member: content) {
            System.out.println("memeber = " + member);
        }

        System.out.println(totalElements);
    }

    @Test
    public void pagingSlice(){

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));
        memberRepository.save(new Member("member8", 10));

        int age = 10;
        //페이징 조건
        //Slice는 3개를 요청하면 +1해서 4개 얻어옴
        //전체 페이지수 안가져옴
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //Page로 반환타입을 받으면 totalCount 쿼리 알아서 날림
        //Slice가 Page보다 상위타입
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        //then
        //페이지안에 컨텐츠 꺼내기
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        //현재 몇번째 페이지 인지
        assertThat(page.getNumber()).isEqualTo(0);
        //현재 페이지가 첫번째인지
        assertThat(page.isFirst()).isTrue();
        //다음 페이지가 있는지
        assertThat(page.hasNext()).isTrue();

        for (Member member: content) {
            System.out.println("memeber = " + member);
        }
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 12));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int result = memberRepository.bulkAgePlus(20);

//        em.flush();
//        em.clear();

        //then
        assertThat(result).isEqualTo(2);
    }

    //feach join
    @Test
    public void findMemberLazy(){
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //select Member
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member m : members) {
            //이 시점에서 m의 Team은 가짜객체를 가지고있다
            System.out.println("member" + m);
            //Team은 FetchType이 Lazy라 호출할때 다시찾음
            System.out.println("member.team" +m.getTeam().getName());
        }
    }

    //feach join
    @Test
    public void findMemberLazyEntityGraph(){
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        for (Member m : members) {
            System.out.println("member" + m);
            System.out.println("member.team" +m.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        //readonly 쿼리?
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//
//        //JPA에서 변경감지해 update쿼리가 발생
//        em.flush();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        //readonly가 켜져있어서 스냅샷을 안만듬?
        //flush해도 업데이트 안됨 = 변경감지 체크 안함
        findMember.setUsername("member2");
        
        em.flush();
    }

    @Test
    public void lock(){
        //readonly 쿼리?
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom(){
        //사용자 정의 리포지토리
        //1. 커스텀 인터페이스 생성 후 메서스 작성
        //2. 1번의 인터페이스 구현체 생성 후 메서스 오버라이딩(클래스 명을  JPA인터페이스 + Impl로 생성)
        //3. JPA인터페이스에서 커스텀인터페이스 상속
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void JpaEventBaceEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member);

        Thread.sleep(100);
        
        member.setUsername("member2");
        
        em.flush();
        em.clear();
        
        //when
        Member member1 = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("생성일 : " + member1.getCreateDate());
        System.out.println("수정일 : " +member1.getLastModifiedDate());
        System.out.println("생성자 : " + member1.getCreateBy());
        System.out.println("수정자 : " +member1.getLastModifiedBy());
    }
}