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
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.InputService;
import org.zerock.b01.service.NoticeService;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.ReturnService;
import org.zerock.b01.service.UserByService;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '자재부서'))")
@RequestMapping("/inPut")
public class InPutController {

    private final UserByService userByService;
    private final PageService pageService;
    private final InputService inputService;
    private final NoticeService noticeService;
    private final ReturnService returnService;


    @GetMapping("/inPutManage")
    public void inPut(PageRequestDTO pageRequestDTO, Model model){
        log.info("##MATERIAL RECEIPT PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10);
        }

        PageResponseDTO<DeliveryRequestDTO> responseDTO = pageService.deliveryRequestWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<DeliveryRequestDTO> filteredList = Optional.ofNullable(responseDTO.getDtoList())
                .orElse(Collections.emptyList())
                .stream()
                .filter(dto -> dto.getDrNum() != 0)
                .collect(Collectors.toList());

        List<DeliveryRequestDTO> deliveryRequestList = inputService.getDeliveryRequest();

        model.addAttribute("deliveryRequestList", deliveryRequestList);
        model.addAttribute("responseDTO", responseDTO);
        log.info("## deliveryRequestList : " + deliveryRequestList);
        log.info("## DR responseDTO : " + responseDTO);

        Set<CurrentStatus> drStateSet = deliveryRequestList.stream()
                .map(DeliveryRequestDTO::getDrState)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new)); // 순서 유지

        log.info("## drStateSet : " + drStateSet);
        model.addAttribute("filteredDeliveryRequestList", filteredList);
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDRState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");
        model.addAttribute("drStateSet", drStateSet);

    }

    @PostMapping("/inputRegister")
    public String inventoryRegisterPost(String uId, InputDTO inputDTO, Model model, RedirectAttributes redirectAttributes){

        log.info(" ^^^^ " + uId);

        try {
            inputService.registerInput(inputDTO);
            redirectAttributes.addFlashAttribute("message", "입고 처리가 완료되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        noticeService.addNotice("ip");

        return "redirect:inPutManage";
    }

    @GetMapping("/inPutList")
    public void inPutList(PageRequestDTO pageRequestDTO, Model model){

        log.info("##MATERIAL RECEIPT LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10);
        }

        PageResponseDTO<InputDTO> responseDTO = pageService.inputWithAll(pageRequestDTO);

        List<String> mNameList = Optional.ofNullable(responseDTO.getDtoList())
                .orElse(Collections.emptyList())
                .stream()
                .map(dto -> dto.getMName())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("mNameList", mNameList);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<InputDTO> inputDTOList = inputService.getInputs();
        model.addAttribute("inputDTOList", inputDTOList);
        model.addAttribute("responseDTO", responseDTO);

        log.info("## inputDTOList : " + inputDTOList);
        log.info("## IP responseDTO : " + responseDTO);

        List<DeliveryRequestDTO> deliveryRequestList = inputService.getDeliveryRequest();

        Set<CurrentStatus> drStateSet = deliveryRequestList.stream()
                .map(DeliveryRequestDTO::getDrState)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<DeliveryRequestDTO> filteredList = deliveryRequestList.stream()
                .filter(dto -> dto.getDrNum() != null && dto.getDrNum() != 0)
                .collect(Collectors.toList());

        Set<CurrentStatus> ipStateSet = inputDTOList.stream()
                .map(InputDTO::getIpState)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));


        log.info("## ipStateSet : " + ipStateSet);
        log.info("## drStateSet : " + drStateSet);
        model.addAttribute("filteredDeliveryRequestList", filteredList);
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDRState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");
        model.addAttribute("selectedIPState", pageRequestDTO.getIpState() != null ? pageRequestDTO.getIpState() : "");
        model.addAttribute("drStateSet", drStateSet);
        model.addAttribute("ipStateSet", ipStateSet);
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute InputDTO inputDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> ipIds) {
        log.info("pp remove post.....#@" + inputDTO);
        inputService.removeInput(ipIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:inPutList";
    }

    @PostMapping("/return")
    public String remove(String uId, ReturnByDTO returnByDTO, RedirectAttributes redirectAttributes) {
        log.info("pp remove post.....#@" + returnByDTO);
        log.info(" ^^^^ " + uId);
        try {
            returnService.returnInput(returnByDTO);
            redirectAttributes.addFlashAttribute("message", "반품 처리가 완료되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:inPutList";
    }

}
