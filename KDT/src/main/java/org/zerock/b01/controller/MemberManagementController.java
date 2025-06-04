package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.SupplierAllDTO;
import org.zerock.b01.dto.allDTO.UserByAllDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
@RequestMapping("/memberManagement")
public class MemberManagementController {

    private final PageService pageService;
    private final UserByService userByService;

    @GetMapping("/employeeList")
    public void employeeList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<UserByAllDTO> responseDTO =
                pageService.userByWithAllList(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^&" + responseDTO);
    }


    @GetMapping("/supplierList")
    public void supplierList(PageRequestDTO pageRequestDTO, Model model) {
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        //b를 준 이유는 다른 페이지에서 같은 메서드를 사용하기 위해
        PageResponseDTO<SupplierAllDTO> responseDTO =
                pageService.supplierWithAll(pageRequestDTO, "b");

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/employeeApproval")
    public void eaList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<UserByAllDTO> responseDTO =
                pageService.userByWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^&" + responseDTO);
    }

    @PostMapping("/employeeApprovalAgree")
    public String employeeApprovalAgree(@RequestParam("uId") List<String> uId, @RequestParam("userRank") List<String> userRank,
                                        @RequestParam("userJob") List<String> userJob, @RequestParam("pageType") String pageType,
                                        @RequestParam("status") List<String> status,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        for (int i = 0; i < uId.size(); i++) {
            String id = uId.get(i);
            String ur = userRank.get(i);
            String uj = userJob.get(i);
            String st = status.get(i);

            userByService.agreeEmployee(id, ur, uj, st);
        }

        if (pageType.equals("a")) {
            redirectAttributes.addFlashAttribute("message", "가입 승인이 완료되었습니다.");
            return "redirect:employeeApproval";
        }
        else {
            redirectAttributes.addFlashAttribute("message", "정보 변경이 완료되었습니다.");
            return "redirect:employeeList";
        }
    }

    @PostMapping("/employeeApprovalDisAgree")
    public String employeeApprovalDisAgree(@RequestParam("uId") List<String> uId, @RequestParam("userRank") List<String> userRank,
                                           Model model, RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) throws IOException {

        for (int i = 0; i < uId.size(); i++) {
            String id = uId.get(i);
            String ur = userRank.get(i);

            userByService.disAgreeEmployee(id, ur);
            redirectAttributes.addFlashAttribute("message", "정보 삭제가 완료되었습니다.");
        }

        return "redirect:employeeList";
    }


    @GetMapping("/supplierApproval")
    public void supplierApproval(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        //a를 준 이유는 다른 페이지에서 같은 메서드를 사용하기 위해
        PageResponseDTO<SupplierAllDTO> responseDTO =
                pageService.supplierWithAll(pageRequestDTO, "a");

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^& " + responseDTO);
    }

    @PostMapping("/supplierApprovalAgree")
    public String supplierApprovalAgree(@RequestParam("uId") List<String> uId,
                                        @RequestParam("sStatus") List<String> sStatus,
                                        @RequestParam("pageType") String pageType,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        for (int i = 0; i < uId.size(); i++) {
            String id = uId.get(i);
            String st = sStatus.get(i);

            userByService.agreeSupplier(id, st);
        }

        if(pageType.equals("a")){
            redirectAttributes.addFlashAttribute("message", "가입 승인이 완료되었습니다.");
            return "redirect:supplierApproval";
        } else{
            redirectAttributes.addFlashAttribute("message", "정보 변경이 완료되었습니다.");
            return "redirect:supplierList";
        }
    }

    @PostMapping("/supplierApprovalRmvAgree")
    public String supplierApprovalRmvAgree(@RequestParam("uId") List<String> uId,
                                           Model model, RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) throws IOException {

        for (String uid : uId) {
            userByService.disAgreeSupplier(uid);
        }
        redirectAttributes.addFlashAttribute("message", "완료되었습니다.");
        return "redirect:supplierList";
    }

    // 반려
    @PostMapping("/supplierApprovalDisAgree")
    public String supplierApprovalDisAgree(@RequestParam("uId") List<String> uId,
                                           Model model, RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) throws IOException {

        for (String uid : uId) {
            userByService.disAgreeSupplier(uid);
        }
        redirectAttributes.addFlashAttribute("message", "완료되었습니다.");
        return "redirect:supplierList";
    }
}
