package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        //then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getUsername(), findMember.getUsername());
        assertEquals(member, findMember);
    }
    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertEquals(findMember1, member1);
        assertEquals(findMember2, member2);

        List<Member> all = memberRepository.findAll();
        assertEquals(2, all.size());

        long count = memberRepository.count();
        assertEquals(2, count);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long afterDelete = memberRepository.count();
        assertEquals(0, afterDelete);
    }
    
    @Test
    public void findByUsername() throws Exception {
        //given
        Member member1 = new Member("member1");
        memberRepository.save(member1);
        
        //when
        List<Member> members = memberRepository.findByUsername("member1");

        //then
        assertEquals(1, members.size());
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member2", 15);

        //then
        assertEquals(members.get(0).getUsername(), member2.getUsername());
        assertEquals(members.get(0), member2);
    }

    @Test
    public void findByUsernameAndAgeIsNull() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> member11 = memberRepository.findByUsernameAndTeamIsNull("member1");

        //then
        assertEquals(1,member11.size());
    }

    @Test
    public void findTop3HelloBy() throws Exception {
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void 리포지토리_메소드에_쿼리_정의() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> findMembers = memberRepository.findUser("member1", 10);

        //then
        assertEquals(findMembers.get(0).getUsername(), member1.getUsername());
        assertEquals(findMembers.get(0), member1);
    }

    @Test
    public void findUsernameList() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<String> usernameList = memberRepository.findUsernameList();

        //then
        assertEquals(2, usernameList.size());
    }

    @Test
    public void findMemberDto() throws Exception {
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("member1", 10);
        member1.changeTeam(team);
        memberRepository.save(member1);

        //when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        //then
        assertEquals(1, memberDto.size());
        assertEquals(memberDto.get(0).getId(), member1.getId());
        assertEquals(memberDto.get(0).getTeamName(), member1.getTeam().getName());
        assertEquals(memberDto.get(0).getUsername(), member1.getUsername());
    }

    @Test
    public void findByNames() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findByNames(Arrays.asList("member1", "member2"));

        //then
        assertEquals(2, result.size());
    }

    @Test
    public void returnType() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result1 = memberRepository.findListByUsername("member1");
        Member result2 = memberRepository.findMemberByUsername("member1");
        Optional<Member> result3 = memberRepository.findOptionalByUsername("member1");

        //then
        assertEquals(1, result1.size());
        assertEquals(result2.getUsername(), member1.getUsername());
        assertEquals(result3.get().getUsername(), member1.getUsername());
    }

    @Test
    public void 페이징() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        /** Page의 Map 기능으로 엔티티를 Dto로 변환할 수 있음 */
        Page<MemberDto> memberDtos = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = page.getContent();

//        //then
        assertEquals(3, page.getNumberOfElements());
        assertEquals(7, page.getTotalElements());
        assertEquals(0, page.getNumber());
        assertEquals(3, page.getTotalPages());
        assertTrue(page.isFirst());
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        memberRepository.save(new Member("member6", 22));
        memberRepository.save(new Member("member7", 15));

        //when
        int count = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");

        //then
        assertEquals(4, count);
        assertEquals(41, result.get(0).getAge());
    }

    @Test
    public void findFetchJoin() throws Exception {
        //given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        Team teamB = new Team("teamB");
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10);
        member1.changeTeam(teamA);
        memberRepository.save(member1);

        Member member2 = new Member("member2", 10);
        member2.changeTeam(teamB);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        //then
        em.flush();
    }

    @Test
    public void lock() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> findMember = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void customRepository() throws Exception {
        //given
        List<Member> result = memberRepository.findMemberCustom();

        //when

        //then
    }
}