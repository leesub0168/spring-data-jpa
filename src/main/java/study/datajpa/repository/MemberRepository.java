package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    /**
     * 쿼리 조건이 좀 복잡해지거나, 쿼리 메소드가 길어지는 경우, 이 방법을 사용해서 이름은 단순하게 주고 쿼리를 정의하는걸 추천함
     **/

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);       /** 결과값이 없으면 빈 컬렉션을 반환해줌 NullPoint 발생 방지 */
    Member findMemberByUsername(String username);           /** JPA는 결과가 없으면 NoResultException을 던지지만, Spring Data JPA는 결과값이 없으면 Null 반환 */
    Optional<Member> findOptionalByUsername(String username);    /** 결과가 1개 이상이면 IncorrectResultSizeDataAccessException 발생 */

    /**
     * Page -> totalCount 까지 포함하여 결과 반환
     * Slice -> totalCount 포함X. limit + 1로 화면에서 스크롤 레이지 로딩방식을 쓸때 사용.
     * Page는 Slice를 상속받는 관계이다.
     *
     * 페이징시 결과값을 그냥 List로 받는것도 가능. 단 totalCount 는 포함 X
     * 쿼리가 복잡해질 경우, count용 쿼리를 따로 설정할 수 있음.
     * */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
}
