package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun count(): Long = memberRepository.count() // 단순 위임

    fun join(username: String, password: String?, nickname: String): Member =
        join(username, password, nickname, null)

    fun join(username: String, password: String?, nickname: String, profileImgUrl: String?): Member {
        // 아이디 중복 체크: 존재하면 409 에러
        memberRepository.findByUsername(username).ifPresent {
            throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
        }

        val encodedPassword = if (!password.isNullOrBlank()) passwordEncoder.encode(password) else null

        // 엔티티 생성 후 저장
        val member = Member(username, encodedPassword, nickname, profileImgUrl)
        return memberRepository.save(member)
    }

    fun findByUsername(username: String): Optional<Member> = memberRepository.findByUsername(username)

    fun findByApiKey(apiKey: String): Optional<Member> = memberRepository.findByApiKey(apiKey)

    fun genAccessToken(member: Member): String = authTokenService.genAccessToken(member)

    fun payload(accessToken: String): Map<String, Any>? = authTokenService.payload(accessToken)

    fun findById(id: Int): Optional<Member> = memberRepository.findById(id)

    fun findAll(): List<Member> = memberRepository.findAll()

    fun checkPassword(member: Member, rawPassword: String) {
        val hashed = member.password

        if (!passwordEncoder.matches(rawPassword, hashed))
            throw ServiceException("401-1", "비밀번호가 일치하지 않습니다.")
    }

    fun modifyOrJoin(username: String, password: String?, nickname: String, profileImgUrl: String?): RsData<Member> {
        val existing = findByUsername(username).orElse(null)

        if (existing == null) {
            val joined = join(username, password, nickname, profileImgUrl)
            return RsData("201-1", "회원가입이 완료되었습니다.", joined)
        }

        modify(existing, nickname, profileImgUrl)
        return RsData("200-1", "회원 정보가 수정되었습니다.", existing)
    }

    fun modify(member: Member, nickname: String, profileImgUrl: String?) {
        member.modify(nickname, profileImgUrl)
    }
}