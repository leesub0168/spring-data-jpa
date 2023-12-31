package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member saveMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        //then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getUsername(), findMember.getUsername());
        assertEquals(member, findMember);
    }

    @Test
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        //then
        assertEquals(findMember1, member1);
        assertEquals(findMember2, member2);

        List<Member> all = memberJpaRepository.findAll();
        assertEquals(2, all.size());

        long count = memberJpaRepository.count();
        assertEquals(2, count);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long afterDelete = memberJpaRepository.count();
        assertEquals(0, afterDelete);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("member2", 15);

        //when
        assertEquals(members.get(0).getUsername(), member2.getUsername());
        assertEquals(members.get(0), member2);
    }

    @Test
    public void testNamedQuery() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when
        List<Member> result = memberJpaRepository.findByUsername("member2");

        //then
        assertEquals(result.get(0).getUsername(), member2.getUsername());
        assertEquals(result.get(0), member2);
    }

    @Test
    public void 페이징() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        memberJpaRepository.save(new Member("member6", 10));
        memberJpaRepository.save(new Member("member7", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> result = memberJpaRepository.findByPage(age, offset, limit);
        Long totalCount = memberJpaRepository.totalCount(age);

        //then
        assertEquals(3, result.size());
        assertEquals(7, totalCount);
    }
    
    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));
        memberJpaRepository.save(new Member("member6", 22));
        memberJpaRepository.save(new Member("member7", 15));
        
        //when
        int count = memberJpaRepository.bulkAgePlus(20);

        //then
        assertEquals(4, count);
    }
}