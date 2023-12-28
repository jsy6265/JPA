package study.jpadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery( //NamedQuery 장점 : 애플리케이션 실행 시점에 퀴리문을 파싱시켜보고 잘못된 쿼리면 오류발생
        name="Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member extends BaceEntity {

    @Id //식별자
    @GeneratedValue //PK값을 JAP가 알아서 순차적으로 넣어줌
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //멤버는 하나의 팀을 가질 수 있다 //JPA에서 모든 연관관계는 Lazy로 셋팅 //지연로딩? 객체 조회시 가짜 값을 가지고 있다가 필요하면 불러옴
    @JoinColumn(name = "team_id") //포링키 이름?
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
