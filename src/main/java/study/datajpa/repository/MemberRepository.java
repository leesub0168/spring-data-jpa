package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

//    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findByUsernameAndTeamIsNull(String username);

    List<Member> findTop3HelloBy();

//    @Query(name = "Member.findByUsername") // 생략가능
    List<Member> findByUsername(@Param("username") String username);
    // 우선순위 : NamedQuery를 먼저 찾고, 없으면 쿼리 메소드로 생성

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    /** 쿼리 조건이 좀 복잡해지거나, 쿼리 메소드가 길어지는 경우, 이 방법을 사용해서 이름은 단순하게 주고 쿼리를 정의하는걸 추천함 **/

}
