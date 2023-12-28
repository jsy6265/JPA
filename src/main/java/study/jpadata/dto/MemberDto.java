package study.jpadata.dto;

import lombok.Data;

@Data //엔터티에는 Data 쓰면안됨(getter setter 등 다있음)
public class MemberDto {

    private  Long id;
    private String username;
    private String teamnaem;

    public MemberDto(Long id, String username, String teamnaem) {
        this.id = id;
        this.username = username;
        this.teamnaem = teamnaem;
    }
}
