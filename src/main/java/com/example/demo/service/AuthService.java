package com.example.demo.service;

import com.example.demo.member.entity.Member;
import com.example.demo.member.entity.MemberManagement; // ★ 추가됨
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.member.repository.MemberManagementRepository; // ★ 추가됨
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final MemberManagementRepository memberManagementRepository;

    private final PasswordEncoder passwordEncoder;

    // JWT 비밀키 (서버 껐다 켜면 토큰 만료되게 임시로 자동생성)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 1. 회원가입 (Member + MemberManagement 동시 저장)
    public void signup(String id, String password, String name, String phone, String address) {
        // (1) 아이디 중복 체크
        if (memberRepository.existsById(id)) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // (2) 회원(Member) 생성 및 저장
        Member member = Member.builder()
                .id(id)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phone(phone)
                .address(address)
                .build();

        memberRepository.save(member);

        // (3) 회원관리(MemberManagement) 테이블에도 자동 저장
        // 무조건 1번 관리자(DataInit으로 만든 사람)가 담당한다고 가정
        Employee defaultAdmin = employeeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("기본 관리자(1번)가 없습니다. DataInit을 확인하세요."));

        MemberManagement management = MemberManagement.builder()
                .member(member)
                .employee(defaultAdmin)
                .registrationDate(LocalDate.now()) // 오늘 날짜
                .build();

        memberManagementRepository.save(management); // 이제 빨간 줄이 사라질 겁니다!
    }

    // 2. 일반 회원 로그인
    public String login(String id, String password) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("가입되지 않은 회원입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        return createToken(member.getId(), "USER");
    }

    // 3. 관리자 로그인
    public String adminLogin(Long employeeId, String password) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사원번호입니다."));

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        return createToken(String.valueOf(employee.getId()), "ADMIN");
    }

    // 4. 관리자 추가 (테스트용)
    public Long signupAdmin(String password, String name, String phone, String address) {
        Employee employee = Employee.builder()
                .password(passwordEncoder.encode(password))
                .name(name)
                .phone(phone)
                .address(address)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        return savedEmployee.getId();
    }

    // 토큰 생성기
    private String createToken(String id, String role) {
        return Jwts.builder()
                .setSubject(id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
                .signWith(key)
                .compact();
    }

    // 토큰 검사기 (extractRole)
    public String extractRole(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (Exception e) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
    }
}