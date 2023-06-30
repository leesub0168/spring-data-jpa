package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

}