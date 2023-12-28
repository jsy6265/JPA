package study.jpadata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//JpaEvent 방식 순수 JPA
@MappedSuperclass //진짜 상속관계가 아닌 속성을 테이블에서 같이 쓰게하는 방식?
@Getter
public class JpaBaceEntity {

    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @PrePersist //insert 쿼리 실행 전 실행?
    public void perPersist(){
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }

    @PreUpdate //update 쿼리 샐행 전 실행
    public void preUpdate(){
        LocalDateTime now = LocalDateTime.now();
        updateDate = now;
    }
}
