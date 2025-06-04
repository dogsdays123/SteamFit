package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.UserByService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/firstView")
public class FirstViewController {

    private final UserByService userByService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserByRepository userByRepository;

    @ModelAttribute
    public void Profile(UserByDTO userByDTO, Model model, Authentication auth, HttpServletRequest request) {
        if (auth == null) {
            log.info("aaaaaa 인증정보 없음");
            model.addAttribute("userBy", null);
        } else {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;

            // token.getPrincipal()이 MemberSecurityDTO 타입이라면, 이를 MemberSecurityDTO로 캐스팅
            UserBySecurityDTO principal = (UserBySecurityDTO) token.getPrincipal();
            String username = principal.getUId(); // MemberSecurityDTO에서 사용자 이름 가져오기

            // 일반 로그인 사용자 정보 가져오기
            userByDTO = userByService.readOne(username);
            log.info("##### 일반 로그인 사용자 정보: " + userByDTO);
        }
    }

    @GetMapping("/login")
    public void login() {
        log.info("login");
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("userByDTO") UserByDTO userByDTO,
                       @ModelAttribute("supplierDTO") SupplierDTO supplierDTO,
                       Model model, RedirectAttributes redirectAttributes) {

        log.info("join");
        log.info("%%%%" + userByDTO);

        //html에서 수정하기 귀찮음
        if (userByDTO.getUserJob().contains("협력회사") && userByDTO.getUserType().equals("our")) {
            String userJob = userByDTO.getUserJob().replace(",협력회사", "");
            userByDTO.setUserJob(userJob);
        }

        if (userByDTO.getUserJob().contains("협력회사") && userByDTO.getUserType().equals("other")) {
            String userJob = "협력회사";
            userByDTO.setUserJob(userJob);
        }

        try {
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            userByService.join(userByDTO, supplierDTO);
        } catch (UserByService.MidExistException e) {
            redirectAttributes.addFlashAttribute("error", "uId");
            return "redirect:join";
        }
        redirectAttributes.addFlashAttribute("result", "success");
        model.addAttribute("userDTO", userByDTO);

        return "redirect:login";
    }

    @PostMapping("/checkId")
    @ResponseBody
    public Map<String, Object> checkId(@RequestParam("uId") String uId, Model model) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 여부 체크
        if (userByService.readOne(uId) != null) {
            response.put("isAvailable", false); // 아이디가 이미 존재하는 경우
            model.addAttribute("checkId", false);
        } else {
            response.put("isAvailable", true);  // 아이디가 사용 가능한 경우
            model.addAttribute("checkId", true);
        }

        log.info("Id체크" + uId);

        return response; // JSON 형식으로 반환
    }

    @PostMapping("/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("uEmail") String uEmail, Model model) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 여부 체크
        if (userByService.readOneForEmail(uEmail) != null) {
            response.put("isAvailable", false); // 아이디가 이미 존재하는 경우
            model.addAttribute("checkEmail", false);
        } else {
            response.put("isAvailable", true);  // 아이디가 사용 가능한 경우
            model.addAttribute("checkEmail", true);
        }

        log.info("email체크" + uEmail);

        return response; // JSON 형식으로 반환
    }

    @PostMapping("/checkType")
    @ResponseBody
    public Map<String, Object> checkType(@RequestParam("userType") String userType, Model model) {
        Map<String, Object> response = new HashMap<>();

        model.addAttribute("userType", userType);

        if (userType.equals("our")) {
            log.info("userType체크" + userType);
            response.put("isAvailable", true);
        } else {
            log.info("userType체크" + userType);
            response.put("isAvailable", false);
        }

        return response; // JSON 형식으로 반환
    }

    @PostMapping("/forgot/checkEmail")
    @ResponseBody
    public Map<String, Object> forgotCheckEmail(@RequestParam("checkEmail") String email) {
        Map<String, Object> result = new HashMap<>();

        boolean exists = userByService.checkEmailExists(email); // 이메일 존재 여부 확인
        String msg = exists ? "이메일이 존재합니다." : "가입된 이메일이 아닙니다.";

        result.put("exists", exists);  // 이메일 존재 여부
        result.put("msg", msg);  // 메시지

        return result; // JSON 형태로 응답
    }

    @PostMapping("/forgot/sendResetLink")
    @ResponseBody
    public Map<String, String> forgotSendResetLink(@RequestParam("checkEmail") String email) {
        Map<String, String> result = new HashMap<>();

        String msg = userByService.changeUserProfile(email); // ex: "임시 비밀번호를 전송했습니다."
        result.put("msg", msg);

        return result; // JSON 형태로 응답
    }

    @PostMapping("/find")
    public String find(@ModelAttribute("userByDTO") UserByDTO userByDTO, Model model, RedirectAttributes redirectAttributes) {

        return "redirect:login";
    }

    @GetMapping("/loading")
    public void loading() {

    }

    @PostMapping("/admin")
    @ResponseBody
    public String generate() throws UserByService.MidExistException {
        if (userByRepository.findAdmin() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 생성된 데이터입니다.");
        }
        testRegister();
        testRegisterUnit();
        return "firstView/login";
    }

    public void testRegister() {
        UserBy user = UserBy.builder()
                .uId("Admin")
                .uPassword(passwordEncoder.encode("1234"))
                .uName("관리자")
                .uEmail("Admin@admin.admin")
                .roleSet(Set.of(MemberRole.ADMIN))
                .userJob("관리자")
                .status("관리자")
                .uPhone("01000000000")
                .build();

        SupplierDTO supplierDTO = SupplierDTO.builder()
                .sName("admin")
                .sRegNum("12345678")
                .sManager("admin")
                .sStatus("관리자")
                .build();

        userByService.registerAdmin(user, supplierDTO);
    }

    public void testRegisterUnit() throws UserByService.MidExistException {
        String tester[] = {"", "", ""};
        int regNum = 100;
        for (int i = 0; i < regNum; i++) {
            if (i < regNum / 2) {
                tester[0] = "생산부서";
                tester[1] = "our";
                UserBy user = UserBy.builder()
                        .uId("testUnit" + i)
                        .uPassword(passwordEncoder.encode("1234"))
                        .uName("테스터" + i)
                        .userType(tester[1])
                        .userJob(tester[0])
                        .uEmail("Admin" + i + "@admin.admin")
                        .roleSet(Set.of(MemberRole.USER))
                        .uPhone("01000000000")
                        .build();

                UserByDTO userDTO = modelMapper.map(user, UserByDTO.class);
                userByService.join(userDTO, null);
            } else {
                tester[0] = "협력회사";
                tester[1] = "other";
                tester[2] = "오리배";
                UserBy user = UserBy.builder()
                        .uId("testUnit" + i)
                        .uPassword(passwordEncoder.encode("1234"))
                        .uName("테스터" + i)
                        .userType(tester[1])
                        .userJob(tester[0])
                        .uEmail("Admin" + i + "@admin.admin")
                        .roleSet(Set.of(MemberRole.USER))
                        .uPhone("01000000000")
                        .build();

                UserByDTO userDTO = modelMapper.map(user, UserByDTO.class);

                SupplierDTO supplierDTO = SupplierDTO.builder()
                        .sName(tester[2] + i)
                        .sRegNum("12345678" + i)
                        .sManager(tester[2] + i)
                        .sStatus("대기중")
                        .build();

                userByService.join(userDTO, supplierDTO);
            }
        }
    }
}
