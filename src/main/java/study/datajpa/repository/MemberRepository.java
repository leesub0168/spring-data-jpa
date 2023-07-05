package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

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

    /**
     * 벌크성 업데이트 쿼리는 영속성 컨텍스트를 무시하고 쿼리를 날리기 때문에,
     * 같은 트랜잭션에서 추가적인 조회등을 하는 경우, 영속성 컨텍스트를 초기화 하고 수행해야한다.
     * 마이바티스등 부가적인 걸 사용해서 쿼리를 날리는 경우도, 영속성 컨텍스트에서 인식 하지 못하기 때문에 초기화하는 과정 필요
     * @Modifying(clearAutomatically = true) 해당 옵션을 사용하면 쿼리가 나간후에 자동으로 초기화 해줌.
     * */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph("Member.all")
//    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);
}
